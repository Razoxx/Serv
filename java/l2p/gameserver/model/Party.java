package l2p.gameserver.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import l2p.commons.collections.LazyArrayList;
import l2p.commons.threading.RunnableImpl;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.model.base.Experience;
import l2p.gameserver.model.entity.DimensionalRift;
import l2p.gameserver.model.entity.Reflection;
import l2p.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.model.premium.PremiumConfig;
import l2p.gameserver.serverpackets.components.IStaticPacket;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.ExAskModifyPartyLooting;
import l2p.gameserver.serverpackets.ExMPCCClose;
import l2p.gameserver.serverpackets.ExMPCCOpen;
import l2p.gameserver.serverpackets.ExPartyPetWindowAdd;
import l2p.gameserver.serverpackets.ExPartyPetWindowDelete;
import l2p.gameserver.serverpackets.ExSetPartyLooting;
import l2p.gameserver.serverpackets.GetItem;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.serverpackets.PartyMemberPosition;
import l2p.gameserver.serverpackets.PartySmallWindowAdd;
import l2p.gameserver.serverpackets.PartySmallWindowAll;
import l2p.gameserver.serverpackets.PartySmallWindowDelete;
import l2p.gameserver.serverpackets.PartySmallWindowDeleteAll;
import l2p.gameserver.serverpackets.PartySpelled;
import l2p.gameserver.serverpackets.RelationChanged;
import l2p.gameserver.serverpackets.SystemMessage;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.taskmanager.LazyPrecisionTaskManager;
import l2p.gameserver.templates.item.ItemTemplate;
import l2p.gameserver.utils.ItemFunctions;
import l2p.gameserver.utils.Location;
import l2p.gameserver.utils.Log;

public class Party implements PlayerGroup {

    public static final int MAX_SIZE = Config.ALT_MAX_PARTY_SIZE;
    public static final int ITEM_LOOTER = 0;
    public static final int ITEM_RANDOM = 1;
    public static final int ITEM_RANDOM_SPOIL = 2;
    public static final int ITEM_ORDER = 3;
    public static final int ITEM_ORDER_SPOIL = 4;
    private final List<Player> _members = new CopyOnWriteArrayList<Player>();
    private int _partyLvl = 0;
    private int _itemDistribution = 0;
    private int _itemOrder = 0;
    private int _dimentionalRift;
    private Reflection _reflection;
    private CommandChannel _commandChannel;
    public double _rateExp;
    public double _rateSp;
    public double _rateDrop;
    public double _rateAdena;
    public double _rateSpoil;
    private ScheduledFuture<?> positionTask;
    private int _requestChangeLoot = -1;
    private long _requestChangeLootTimer = 0;
    private Set<Integer> _changeLootAnswers = null;
    private static final int[] LOOT_SYSSTRINGS = {487, 488, 798, 799, 800};
    private Future<?> _checkTask = null;

    /**
     * constructor ensures party has always one member - leader
     *
     * @param leader ?????????????????? ??????????
     * @param itemDistribution ?????????? ?????????????????????????? ????????
     */
    public Party(Player leader, int itemDistribution) {
        _itemDistribution = itemDistribution;
        _members.add(leader);
        _partyLvl = leader.getLevel();
        _rateExp = leader.hasBonus() ? PremiumConfig.getPremConfigId(leader.getBonus().getBonusId()).RATE_XP : 1;
        _rateSp = leader.hasBonus() ? PremiumConfig.getPremConfigId(leader.getBonus().getBonusId()).RATE_SP : 1;
        _rateAdena = leader.hasBonus() ? PremiumConfig.getPremConfigId(leader.getBonus().getBonusId()).RATE_ADENA : 1;
        _rateDrop = leader.hasBonus() ? PremiumConfig.getPremConfigId(leader.getBonus().getBonusId()).RATE_ITEM : 1;
        _rateSpoil = leader.hasBonus() ? PremiumConfig.getPremConfigId(leader.getBonus().getBonusId()).RATE_SPOIL : 1;
    }

    /**
     * @return number of party members
     */
    @Override
    public int getMemberCount() {
        return _members.size();
    }

    public int getMemberCountInRange(Player player, int range) {
        int count = 0;
        for (Player member : _members) {
            if (member == player || member.isInRangeZ(player, range)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return all party members
     */
    public List<Player> getPartyMembers() {
        return _members;
    }

    public List<Integer> getPartyMembersObjIds() {
        List<Integer> result = new ArrayList<Integer>(_members.size());
        for (Player member : _members) {
            result.add(member.getObjectId());
        }
        return result;
    }

    public List<Playable> getPartyMembersWithPets() {
        List<Playable> result = new ArrayList<Playable>();
        for (Player member : _members) {
            result.add(member);
            if (member.getPet() != null) {
                result.add(member.getPet());
            }
        }
        return result;
    }

    /**
     * @return next item looter
     */
    private Player getNextLooterInRange(Player player, ItemInstance item, int range) {
        synchronized (_members) {
            int antiloop = _members.size();
            while (--antiloop > 0) {
                int looter = _itemOrder;
                _itemOrder++;
                if (_itemOrder > _members.size() - 1) {
                    _itemOrder = 0;
                }

                Player ret = looter < _members.size() ? _members.get(looter) : player;

                if (ret != null && !ret.isDead() && ret.isInRangeZ(player, range) && ret.getInventory().validateCapacity(item) && ret.getInventory().validateWeight(item)) {
                    return ret;
                }
            }
        }
        return player;
    }

    /**
     * true if player is party leader
     */
    public boolean isLeader(Player player) {
        return getPartyLeader() == player;
    }

    /**
     * ???????????????????? ???????????? ????????????
     *
     * @return L2Player ?????????? ????????????
     */
    public Player getPartyLeader() {
        synchronized (_members) {
            if (_members.size() == 0) {
                return null;
            }
            return _members.get(0);
        }
    }

    /**
     * Broadcasts packet to every party member
     *
     * @param msg packet to broadcast
     */
    @Override
    public void broadCast(IStaticPacket... msg) {
        for (Player member : _members) {
            member.sendPacket(msg);
        }
    }

    /**
     * ?????????????????? ?????????????????? ?????????????????? ???????? ???????????? ????????????
     *
     * @param msg ??????????????????
     */
    public void broadcastMessageToPartyMembers(String msg) {
        this.broadCast(new SystemMessage(msg));
    }

    /**
     * ?????????????????? ?????????? ???????? ???????????? ???????????? ???????????????? ???????????????????? ??????????????????<BR><BR>
     */
    public void broadcastToPartyMembers(Player exclude, L2GameServerPacket msg) {
        for (Player member : _members) {
            if (exclude != member) {
                member.sendPacket(msg);
            }
        }
    }

    public void broadcastToPartyMembersInRange(Player player, L2GameServerPacket msg, int range) {
        for (Player member : _members) {
            if (player.isInRangeZ(member, range)) {
                member.sendPacket(msg);
            }
        }
    }

    public boolean containsMember(Player player) {
        return _members.contains(player);
    }

    /**
     * adds new member to party
     *
     * @param player L2Player to add
     */
    public boolean addPartyMember(Player player) {
        Player leader = getPartyLeader();
        if (leader == null) {
            return false;
        }

        synchronized (_members) {
            if (_members.isEmpty()) {
                return false;
            }
            if (_members.contains(player)) {
                return false;
            }
            if (_members.size() == MAX_SIZE) {
                return false;
            }
            _members.add(player);
        }

        if (_requestChangeLoot != -1) {
            finishLootRequest(false); // cancel on invite
        }
        player.setParty(this);
        player.getListeners().onPartyInvite();

        Summon pet;
        List<L2GameServerPacket> addInfo = new ArrayList<>(4 + _members.size() * 4);
        List<L2GameServerPacket> pplayer = new ArrayList<>(20);

        //sends new member party window for all members
        //we do all actions before adding member to a list, this speeds things up a little
        pplayer.add(new PartySmallWindowAll(this, player));
        pplayer.add(new SystemMessage2(SystemMsg.YOU_HAVE_JOINED_S1S_PARTY).addName(leader));

        addInfo.add(new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_PARTY).addName(player));
        addInfo.add(new PartySpelled(player, true));
        if ((pet = player.getPet()) != null) {
            addInfo.add(new ExPartyPetWindowAdd(pet));
            addInfo.add(new PartySpelled(pet, true));
        }

        PartyMemberPosition pmp = new PartyMemberPosition();
        List<L2GameServerPacket> pmember;
        for (Player member : _members) {
            if (member != player) {
                pmember = new ArrayList<>(addInfo.size() + 4);
                pmember.addAll(addInfo);
                pmember.add(new PartySmallWindowAdd(member, player));
                pmember.add(new PartyMemberPosition().add(player));
                pmember.add(new RelationChanged().add(player, member));
                member.sendPacket(pmember);

                pplayer.add(new PartySpelled(member, true));
                if ((pet = member.getPet()) != null) {
                    pplayer.add(new PartySpelled(pet, true));
                    pet.broadcastCharInfoImpl(player);
                }
                ((List) pplayer).add(new RelationChanged().add(member, player));
                pmp.add(member);
            }
        }

        pplayer.add(pmp);
        // ???????? ???????????? ?????? ?? ????, ???? ?????????? ?????????????????? ???????????????? ?????????? ???????????????? ???????? ????
        if (isInCommandChannel()) {
            pplayer.add(ExMPCCOpen.STATIC);
        }

        player.sendPacket(pplayer);
        if (player.getPet() != null) {
            player.getPet().broadcastCharInfoImpl(this);
        }
        startUpdatePositionTask();
        recalculatePartyData();

        if (isInReflection() && getReflection() instanceof DimensionalRift) {
            ((DimensionalRift) getReflection()).partyMemberInvited();
        }

        return true;
    }

    /**
     * ?????????????? ?????? ??????????
     */
    public void dissolveParty() {
        for (Player p : _members) {
            p.sendPacket(PartySmallWindowDeleteAll.STATIC);
            p.setParty(null);
        }

        synchronized (_members) {
            _members.clear();
        }

        setDimensionalRift(null);
        setCommandChannel(null);
        stopUpdatePositionTask();
    }

    /**
     * removes player from party
     *
     * @param player L2Player to remove
     */
    public boolean removePartyMember(Player player, boolean kick) {
        boolean isLeader = isLeader(player);
        boolean dissolve = false;

        synchronized (_members) {
            if (!_members.remove(player)) {
                return false;
            }
            dissolve = _members.size() == 1;
        }

        player.getListeners().onPartyLeave();

        player.setParty(null);
        recalculatePartyData();

        List<L2GameServerPacket> pplayer = new ArrayList<L2GameServerPacket>(4 + _members.size() * 2);

        // ?????????????????? ?????????????????? ?????????? ???????????????? ????
        if (isInCommandChannel()) {
            pplayer.add(ExMPCCClose.STATIC);
        }
        if (kick) {
            pplayer.add(new SystemMessage2(SystemMsg.YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY));
        } else {
            pplayer.add(new SystemMessage2(SystemMsg.YOU_HAVE_WITHDRAWN_FROM_THE_PARTY));
        }
        pplayer.add(PartySmallWindowDeleteAll.STATIC);

        Summon pet;
        List<L2GameServerPacket> outsInfo = new ArrayList<L2GameServerPacket>(3);
        if ((pet = player.getPet()) != null) {
            outsInfo.add(new ExPartyPetWindowDelete(pet));
        }
        outsInfo.add(new PartySmallWindowDelete(player));
        if (kick) {
            outsInfo.add(new SystemMessage2(SystemMsg.C1_WAS_EXPELLED_FROM_THE_PARTY).addName(player));
        } else {
            outsInfo.add(new SystemMessage2(SystemMsg.S1_HAS_LEFT_THE_PARTY).addName(player));
        }

        List<L2GameServerPacket> pmember = new ArrayList(2 + outsInfo.size());
        for (Player member : _members) {

            pmember.addAll(outsInfo);
            pmember.add(new RelationChanged().add(player, member));
            if (member.getPet() != null) {
                member.getPet().broadcastCharInfoImpl(player);
            }
            member.sendPacket(pmember);
            pplayer.add(new RelationChanged().add(member, player));
        }
        if (player.getPet() != null) {
            player.getPet().broadcastCharInfoImpl(this);
        }
        player.sendPacket(pplayer);

        Reflection reflection = getReflection();

        if (reflection instanceof DarknessFestival) {
            ((DarknessFestival) reflection).partyMemberExited();
        } else if (isInReflection() && getReflection() instanceof DimensionalRift) {
            ((DimensionalRift) getReflection()).partyMemberExited(player);
        }
        if (reflection != null && player.getReflection() == reflection && reflection.getReturnLoc() != null) {
            player.teleToLocation(reflection.getReturnLoc(), ReflectionManager.DEFAULT);
        }

        Player leader = getPartyLeader();

        if (dissolve) {
            // ???????? ?? ???????????? ?????????????? 1 ??????????????, ???? ?????????????? ???? ???? ????
            if (isInCommandChannel()) {
                _commandChannel.removeParty(this);
            } else if (reflection != null) {
                //lastMember.teleToLocation(getReflection().getReturnLoc(), 0);
                //getReflection().stopCollapseTimer();
                //getReflection().collapse();
                if (reflection.getInstancedZone() != null && reflection.getInstancedZone().isCollapseOnPartyDismiss()) {
                    if (reflection.getParty() == this) // TODO: ???????????? ??????????????
                    {
                        reflection.startCollapseTimer(reflection.getInstancedZone().getTimerOnCollapse() * 1000);
                    }
                    if (leader != null && leader.getReflection() == reflection) {
                        leader.broadcastPacket(new SystemMessage2(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(1));
                    }
                }
            }

            dissolveParty();
        } else {
            if (isInCommandChannel() && _commandChannel.getChannelLeader() == player) {
                _commandChannel.setChannelLeader(leader);
            }

            if (isLeader) {
                updateLeaderInfo();
            }
        }

        if (_checkTask != null) {
            _checkTask.cancel(true);
            _checkTask = null;
        }

        return true;
    }

    public boolean changePartyLeader(Player player) {
        Player leader = getPartyLeader();

        // ???????????? ?????????????? ???????????? ?? ???????????????? ????????????
        synchronized (_members) {
            int index = _members.indexOf(player);
            if (index == -1) {
                return false;
            }
            _members.set(0, player);
            _members.set(index, leader);
        }

        updateLeaderInfo();

        if (isInCommandChannel() && _commandChannel.getChannelLeader() == leader) {
            _commandChannel.setChannelLeader(player);
        }

        return true;
    }

    private void updateLeaderInfo() {
        Player leader = getPartyLeader();
        if (leader == null) // ??????????????????, ???? ?????????? NPE.
        {
            return;
        }

        SystemMessage2 msg = new SystemMessage2(SystemMsg.C1_HAS_BECOME_THE_PARTY_LEADER).addName(leader);

        for (Player member : _members) {
            // ???????????????????????????? ???????????? - ???????????????? ?? ?????????????????????????? ????????
            member.sendPacket(PartySmallWindowDeleteAll.STATIC, // ?????????????? ?????? ????????????
                    new PartySmallWindowAll(this, member), // ???????????????????? ????????????
                    msg); // ???????????????? ?? ?????????? ????????????
        }

        // ???????????????????? ??????????????????
        for (Player member : _members) {
            broadcastToPartyMembers(member, new PartySpelled(member, true)); // ???????????????????? ????????????
            if (member.getPet() != null) {
                this.broadCast(new ExPartyPetWindowAdd(member.getPet())); // ???????????????????? ???????????? ??????????
            }			// broadcastToPartyMembers(member, new PartyMemberPosition(member)); // ?????????????????? ?????????????? ???? ??????????
        }
    }

    /**
     * finds a player in the party by name
     *
     * @param name ?????? ?????? ????????????
     * @return ???????????????? L2Player ?????? null ???????? ???? ??????????????
     */
    public Player getPlayerByName(String name) {
        for (Player member : _members) {
            if (name.equalsIgnoreCase(member.getName())) {
                return member;
            }
        }
        return null;
    }

    /**
     * distribute item(s) to party members
     *
     * @param player
     * @param item
     */
    public void distributeItem(Player player, ItemInstance item, NpcInstance fromNpc) {
        switch (item.getItemId()) {
            case ItemTemplate.ITEM_ID_ADENA:
                distributeAdena(player, item, fromNpc);
                break;
            default:
                distributeItem0(player, item, fromNpc);
                break;
        }

    }

    private void distributeItem0(Player player, ItemInstance item, NpcInstance fromNpc) {
        Player target = null;

        List<Player> ret = null;
        switch (_itemDistribution) {
            case ITEM_RANDOM:
            case ITEM_RANDOM_SPOIL:
                ret = new ArrayList<Player>(_members.size());
                for (Player member : _members) {
                    if (member.isInRangeZ(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) && !member.isDead() && member.getInventory().validateCapacity(item) && member.getInventory().validateWeight(item)) {
                        ret.add(member);
                    }
                }

                target = ret.isEmpty() ? null : ret.get(Rnd.get(ret.size()));
                break;
            case ITEM_ORDER:
            case ITEM_ORDER_SPOIL:
                synchronized (_members) {
                    ret = new CopyOnWriteArrayList<Player>(_members);
                    while (target == null && !ret.isEmpty()) {
                        int looter = _itemOrder;
                        _itemOrder++;
                        if (_itemOrder > ret.size() - 1) {
                            _itemOrder = 0;
                        }

                        Player looterPlayer = looter < ret.size() ? ret.get(looter) : null;

                        if (looterPlayer != null) {
                            if (!looterPlayer.isDead() && looterPlayer.isInRangeZ(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) && ItemFunctions.canAddItem(looterPlayer, item)) {
                                target = looterPlayer;
                            } else {
                                ret.remove(looterPlayer);
                            }
                        }
                    }
                }

                if (target == null) {
                    return;
                }
                break;
            case ITEM_LOOTER:
            default:
                target = player;
                break;
        }

        if (target == null) {
            target = player;
        }

        if (target.pickupItem(item, Log.PartyPickup)) {
            if (fromNpc == null) {
                player.broadcastPacket(new GetItem(item, player.getObjectId()));
            }

            player.broadcastPickUpMsg(item);
            item.pickupMe();

            broadcastToPartyMembers(target, SystemMessage2.obtainItemsBy(item, target));
        } else {
            item.dropToTheGround(player, fromNpc);
        }
    }

    private void distributeAdena(Player player, ItemInstance item, NpcInstance fromNpc) {
        if (player == null) {
            return;
        }

        List<Player> membersInRange = new ArrayList<>();

        if (item.getCount() < _members.size()) {
            membersInRange.add(player);
        } else {
            for (Player member : _members) {
                if (!member.isDead() && (member == player || player.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE)) && ItemFunctions.canAddItem(player, item)) {
                    membersInRange.add(member);
                }
            }
        }

        if (membersInRange.isEmpty()) {
            membersInRange.add(player);
        }

        long totalAdena = item.getCount();
        long amount = totalAdena / membersInRange.size();
        long ost = totalAdena % membersInRange.size();

        for (Player member : membersInRange) {
            long count = member.equals(player) ? amount + ost : amount;
            member.getInventory().addAdena(count);
            member.sendPacket(SystemMessage2.obtainItems(ItemTemplate.ITEM_ID_ADENA, count, 0));
        }

        if (fromNpc == null) {
            player.broadcastPacket(new GetItem(item, player.getObjectId()));
        }

        item.pickupMe();
    }

    public void distributeXpAndSp(double xpReward, double spReward, List<Player> rewardedMembers, Creature lastAttacker, MonsterInstance monster) {
        recalculatePartyData();

        List<Player> mtr = new ArrayList<Player>();
        int partyLevel = lastAttacker.getLevel();
        int partyLvlSum = 0;

        // ?????????????? ??????????????????????/???????????????????????? ??????????????
        for (Player member : rewardedMembers) {
            if (!monster.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                continue;
            }
            partyLevel = Math.max(partyLevel, member.getLevel());
        }

        // ???????????????????? ???????????? ??????????????, ?????????????????????????????? ??????????????????????
        for (Player member : rewardedMembers) {
            if (!monster.isInRangeZ(member, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                continue;
            }
            if (member.getLevel() <= partyLevel - 15) {
                continue;
            }
            partyLvlSum += member.getLevel();
            mtr.add(member);
        }

        if (mtr.isEmpty()) {
            return;
        }

        // ?????????? ???? ????????
        double bonus = Config.ALT_PARTY_BONUS[mtr.size() - 1];

        // ???????????????????? ???????? ?? ???? ?????? ?????????????? ???? ????????
        double XP = xpReward * bonus;
        double SP = spReward * bonus;

        for (Player member : mtr) {
            double lvlPenalty = Experience.penaltyModifier(monster.calculateLevelDiffForDrop(member.getLevel()), 9);
            int lvlDiff = partyLevel - member.getLevel();
            if (lvlDiff >= 10 && lvlDiff <= 14) {
                lvlPenalty *= 0.3D;
            }

            // ???????????? ?????? ?????????? ?? ???????????? ????????????????
            double memberXp = XP * lvlPenalty * member.getLevel() / partyLvlSum;
            double memberSp = SP * lvlPenalty * member.getLevel() / partyLvlSum;

            // ???????????? ?????? ???????? ???? ??????????
            memberXp = Math.min(memberXp, xpReward);
            memberSp = Math.min(memberSp, spReward);

            member.addExpAndCheckBonus(monster, (long) memberXp, (long) memberSp, memberXp / xpReward);
        }

        recalculatePartyData();
    }

    public void recalculatePartyData() {
        _partyLvl = 0;
        double rateExp = 0.;
        double rateSp = 0.;
        double rateDrop = 0.;
        double rateAdena = 0.;
        double rateSpoil = 0.;
        double minRateExp = Double.MAX_VALUE;
        double minRateSp = Double.MAX_VALUE;
        double minRateDrop = Double.MAX_VALUE;
        double minRateAdena = Double.MAX_VALUE;
        double minRateSpoil = Double.MAX_VALUE;
        int count = 0;

        for (Player member : _members) {
            int level = member.getLevel();
            _partyLvl = Math.max(_partyLvl, level);
            count++;

            rateExp += member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_XP : 1;
            rateSp += member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_SP : 1;
            rateDrop += member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_ITEM : 1;
            rateAdena += member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_ADENA : 1;
            rateSpoil += member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_SPOIL : 1;

            minRateExp = Math.min(minRateExp, member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_XP : 1);
            minRateSp = Math.min(minRateSp, member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_SP : 1);
            minRateDrop = Math.min(minRateDrop, member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_ITEM : 1);
            minRateAdena = Math.min(minRateAdena, member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_ADENA : 1);
            minRateSpoil = Math.min(minRateSpoil, member.hasBonus() ? PremiumConfig.getPremConfigId(member.getBonus().getBonusId()).RATE_SPOIL : 1);
        }

        _rateExp = Config.RATE_PARTY_MIN ? minRateExp : rateExp / count;
        _rateSp = Config.RATE_PARTY_MIN ? minRateSp : rateSp / count;
        _rateDrop = Config.RATE_PARTY_MIN ? minRateDrop : rateDrop / count;
        _rateAdena = Config.RATE_PARTY_MIN ? minRateAdena : rateAdena / count;
        _rateSpoil = Config.RATE_PARTY_MIN ? minRateSpoil : rateSpoil / count;
    }

    public int getLevel() {
        return _partyLvl;
    }

    public int getLootDistribution() {
        return _itemDistribution;
    }

    public boolean isDistributeSpoilLoot() {
        boolean rv = false;

        if (_itemDistribution == ITEM_RANDOM_SPOIL || _itemDistribution == ITEM_ORDER_SPOIL) {
            rv = true;
        }

        return rv;
    }

    public boolean isInDimensionalRift() {
        return _dimentionalRift > 0 && getDimensionalRift() != null;
    }

    public void setDimensionalRift(DimensionalRift dr) {
        _dimentionalRift = dr == null ? 0 : dr.getId();
    }

    public DimensionalRift getDimensionalRift() {
        return _dimentionalRift == 0 ? null : (DimensionalRift) ReflectionManager.getInstance().get(_dimentionalRift);
    }

    public boolean isInReflection() {
        if (_reflection != null) {
            return true;
        }
        if (_commandChannel != null) {
            return _commandChannel.isInReflection();
        }
        return false;
    }

    public void setReflection(Reflection reflection) {
        _reflection = reflection;
    }

    public Reflection getReflection() {
        if (_reflection != null) {
            return _reflection;
        }
        if (_commandChannel != null) {
            return _commandChannel.getReflection();
        }
        return null;
    }

    public boolean isInCommandChannel() {
        return _commandChannel != null;
    }

    public CommandChannel getCommandChannel() {
        return _commandChannel;
    }

    public void setCommandChannel(CommandChannel channel) {
        _commandChannel = channel;
    }

    /**
     * ???????????????? ???????? ???????? ?? ???????? ?????????? (x,y,z)
     */
    public void Teleport(int x, int y, int z) {
        TeleportParty(getPartyMembers(), new Location(x, y, z));
    }

    /**
     * ???????????????? ???????? ???????? ?? ???????? ?????????? dest
     */
    public void Teleport(Location dest) {
        TeleportParty(getPartyMembers(), dest);
    }

    /**
     * ???????????????? ???????? ???????? ???? ????????????????????, ???????????? ?????????????????????????? ???????????????? ????
     * ????????????????????
     */
    public void Teleport(Territory territory) {
        RandomTeleportParty(getPartyMembers(), territory);
    }

    /**
     * ???????????????? ???????? ???????? ???? ????????????????????, ?????????? ???????????????? ?? ?????????? dest, ?? ??????
     * ?????????????????? ???????????????????????? ????????????
     */
    public void Teleport(Territory territory, Location dest) {
        TeleportParty(getPartyMembers(), territory, dest);
    }

    public static void TeleportParty(List<Player> members, Location dest) {
        for (Player _member : members) {
            if (_member == null) {
                continue;
            }
            _member.teleToLocation(dest);
        }
    }

    public static void TeleportParty(List<Player> members, Territory territory, Location dest) {
        if (!territory.isInside(dest.x, dest.y)) {
            Log.add("TeleportParty: dest is out of territory", "errors");
            Thread.dumpStack();
            return;
        }
        int base_x = members.get(0).getX();
        int base_y = members.get(0).getY();

        for (Player _member : members) {
            if (_member == null) {
                continue;
            }
            int diff_x = _member.getX() - base_x;
            int diff_y = _member.getY() - base_y;
            Location loc = new Location(dest.x + diff_x, dest.y + diff_y, dest.z);
            while (!territory.isInside(loc.x, loc.y)) {
                diff_x = loc.x - dest.x;
                diff_y = loc.y - dest.y;
                if (diff_x != 0) {
                    loc.x -= diff_x / Math.abs(diff_x);
                }
                if (diff_y != 0) {
                    loc.y -= diff_y / Math.abs(diff_y);
                }
            }
            _member.teleToLocation(loc);
        }
    }

    public static void RandomTeleportParty(List<Player> members, Territory territory) {
        for (Player member : members) {
            member.teleToLocation(Territory.getRandomLoc(territory, member.getGeoIndex()));
        }
    }

    private void startUpdatePositionTask() {
        if (positionTask == null) {
            positionTask = LazyPrecisionTaskManager.getInstance().scheduleAtFixedRate(new UpdatePositionTask(), 1000, 1000);
        }
    }

    private void stopUpdatePositionTask() {
        if (positionTask != null) {
            positionTask.cancel(false);
        }
    }

    private class UpdatePositionTask extends RunnableImpl {

        @Override
        public void runImpl() throws Exception {
            LazyArrayList<Player> update = LazyArrayList.newInstance();

            for (Player member : _members) {
                Location loc = member.getLastPartyPosition();
                if (loc == null || member.getDistance(loc) > 256) //TODO ??????????????????????????????????
                {
                    member.setLastPartyPosition(member.getLoc());
                    update.add(member);
                }
            }

            if (!update.isEmpty()) {
                for (Player member : _members) {
                    PartyMemberPosition pmp = new PartyMemberPosition();
                    for (Player m : update) {
                        if (m != member) {
                            pmp.add(m);
                        }
                    }
                    if (pmp.size() > 0) {
                        member.sendPacket(pmp);
                    }
                }
            }

            LazyArrayList.recycle(update);
        }
    }

    public void requestLootChange(byte type) {
        if (_requestChangeLoot != -1) {
            if (System.currentTimeMillis() > _requestChangeLootTimer) {
                finishLootRequest(false);
            } else {
                return;
            }
        }
        _requestChangeLoot = type;
        int additionalTime = 45000; // timeout 45sec, guess
        _requestChangeLootTimer = System.currentTimeMillis() + additionalTime;
        _changeLootAnswers = new CopyOnWriteArraySet<Integer>();
        _checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ChangeLootCheck(), additionalTime + 1000, 5000);
        broadcastToPartyMembers(getPartyLeader(), new ExAskModifyPartyLooting(getPartyLeader().getName(), type));
        SystemMessage2 sm = new SystemMessage2(SystemMsg.REQUESTING_APPROVAL_CHANGE_PARTY_LOOT_S1);
        sm.addSysString(LOOT_SYSSTRINGS[type]);
        getPartyLeader().sendPacket(sm);
    }

    public synchronized void answerLootChangeRequest(Player member, boolean answer) {
        if (_requestChangeLoot == -1) {
            return;
        }
        if (_changeLootAnswers.contains(member.getObjectId())) {
            return;
        }
        if (!answer) {
            finishLootRequest(false);
            return;
        }
        _changeLootAnswers.add(member.getObjectId());
        if (_changeLootAnswers.size() >= getMemberCount() - 1) {
            finishLootRequest(true);
        }
    }

    private synchronized void finishLootRequest(boolean success) {
        if (_requestChangeLoot == -1) {
            return;
        }
        if (_checkTask != null) {
            _checkTask.cancel(false);
            _checkTask = null;
        }
        if (success) {
            this.broadCast(new ExSetPartyLooting(1, _requestChangeLoot));
            _itemDistribution = _requestChangeLoot;
            SystemMessage2 sm = new SystemMessage2(SystemMsg.PARTY_LOOT_CHANGED_S1);
            sm.addSysString(LOOT_SYSSTRINGS[_requestChangeLoot]);
            this.broadCast(sm);
        } else {
            this.broadCast(new ExSetPartyLooting(0, (byte) 0));
            this.broadCast(new SystemMessage2(SystemMsg.PARTY_LOOT_CHANGE_CANCELLED));
        }
        _changeLootAnswers = null;
        _requestChangeLoot = -1;
        _requestChangeLootTimer = 0;
    }

    private class ChangeLootCheck extends RunnableImpl {

        @Override
        public void runImpl() throws Exception {
            if (System.currentTimeMillis() > Party.this._requestChangeLootTimer) {
                Party.this.finishLootRequest(false);
            }
        }
    }

    @Override
    public Player getGroupLeader() {
        return getPartyLeader();
    }

    @Override
    public Iterator<Player> iterator() {
        return _members.iterator();
    }
}
