package l2p.gameserver.skills.skillclasses;

import java.util.List;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.dao.CharacterEffectDAO;
import l2p.gameserver.data.xml.holder.NpcHolder;
import l2p.gameserver.idfactory.IdFactory;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.GameObjectTasks;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.World;
import l2p.gameserver.model.base.Experience;
import l2p.gameserver.model.entity.events.impl.SiegeEvent;
import l2p.gameserver.model.instances.MerchantInstance;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.instances.SummonInstance;
import l2p.gameserver.model.instances.TrapInstance;
import l2p.gameserver.serverpackets.SystemMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.stats.Stats;
import l2p.gameserver.stats.funcs.FuncAdd;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.templates.StatsSet;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.Location;
import l2p.gameserver.utils.NpcUtils;

public class Summon extends Skill {

    private final SummonType _summonType;
    private final double _expPenalty;
    private final int _itemConsumeIdInTime;
    private final int _itemConsumeCountInTime;
    private final int _itemConsumeDelay;
    private final int _lifeTime;
    private final int _minRadius;

    private static enum SummonType {

        PET,
        SIEGE_SUMMON,
        AGATHION,
        TRAP,
        MERCHANT,
        NPC
    }

    public Summon(StatsSet set) {
        super(set);

        _summonType = Enum.valueOf(SummonType.class, set.getString("summonType", "PET").toUpperCase());
        _expPenalty = set.getDouble("expPenalty", 0.f);
        _itemConsumeIdInTime = set.getInteger("itemConsumeIdInTime", 0);
        _itemConsumeCountInTime = set.getInteger("itemConsumeCountInTime", 0);
        _itemConsumeDelay = set.getInteger("itemConsumeDelay", 240) * 1000;
        _lifeTime = set.getInteger("lifeTime", 1200) * 1000;
        _minRadius = set.getInteger("minRadius", 0);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        Player player = activeChar.getPlayer();
        if (player == null) {
            return false;
        }

        if (player.isProcessingRequest()) {
            player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
        }

        switch (_summonType) {
            case TRAP:
                if (player.isInZonePeace()) {
                    activeChar.sendPacket(SystemMsg.A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE);
                    return false;
                }
                break;
            case PET:
            case SIEGE_SUMMON:
                if (player.getPet() != null || player.isMounted()) {
                    player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
                    return false;
                }
                break;
            case AGATHION:
                if (player.getAgathionId() > 0 && _npcId != 0) {
                    player.sendPacket(SystemMsg.AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED);
                    return false;
                }
            case NPC:
                if (_minRadius > 0) {
                    for (NpcInstance npc : World.getAroundNpc(player, this._minRadius, 200)) {
                        if ((npc != null) && (npc.getNpcId() == getNpcId())) {
                            player.sendPacket(new SystemMessage(SystemMsg.SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN).addName(npc));
                            return false;
                        }
                    }
                }
                break;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature caster, List<Creature> targets) {
        Player activeChar = caster.getPlayer();

        switch (_summonType) {
            case AGATHION:
                activeChar.setAgathion(getNpcId());
                break;
            case TRAP:
                Skill trapSkill = getFirstAddedSkill();

                if (activeChar.getTrapsCount() >= 5) {
                    activeChar.destroyFirstTrap();
                }
                TrapInstance trap = new TrapInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(getNpcId()), activeChar, trapSkill);
                activeChar.addTrap(trap);
                trap.spawnMe();
                break;
            case PET:
            case SIEGE_SUMMON:
                // ???????????????? ??????????, ???????? ???????? ???????????? ???? ??????????.
                Location loc = null;
                if (_targetType == SkillTargetType.TARGET_CORPSE) {
                    for (Creature target : targets) {
                        if (target != null && target.isDead()) {
                            activeChar.getAI().setAttackTarget(null);
                            loc = target.getLoc();
                            if (target.isNpc()) {
                                ((NpcInstance) target).endDecayTask();
                            } else if (target.isSummon()) {
                                ((SummonInstance) target).endDecayTask();
                            } else {
                                return; // ?????? ???????? ?
                            }
                        }
                    }
                }

                if (activeChar.getPet() != null || activeChar.isMounted()) {
                    return;
                }

                NpcTemplate summonTemplate = NpcHolder.getInstance().getTemplate(getNpcId());
                SummonInstance summon = new SummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, activeChar, _lifeTime, _itemConsumeIdInTime, _itemConsumeCountInTime, _itemConsumeDelay, this);
                activeChar.setPet(summon);

                summon.setTitle(activeChar.getName());
                summon.setExpPenalty(_expPenalty);
                summon.setExp(Experience.LEVEL[Math.min(summon.getLevel(), Experience.LEVEL.length - 1)]);
                summon.setHeading(activeChar.getHeading());
                summon.setReflection(activeChar.getReflection());
                summon.spawnMe(loc == null ? Location.findAroundPosition(activeChar, 50, 70) : loc);
                summon.setRunning();
                summon.setFollowMode(true);

                if (summon.getSkillLevel(4140) > 0) {
                    summon.altUseSkill(SkillTable.getInstance().getInfo(4140, summon.getSkillLevel(4140)), activeChar);
                }

                if (summon.getName().equalsIgnoreCase("Shadow"))//FIXME [G1ta0] ?????????????????? ??????????????
                {
                    summon.addStatFunc(new FuncAdd(Stats.ABSORB_DAMAGE_PERCENT, 0x40, this, 15));
                }

                CharacterEffectDAO.getInstance().restoreEffects(summon);
                if (activeChar.isInOlympiadMode()) {
                    summon.getEffectList().stopAllEffects();
                }

                summon.setCurrentHpMp(summon.getMaxHp(), summon.getMaxMp(), false);

                if (_summonType == SummonType.SIEGE_SUMMON) {
                    SiegeEvent siegeEvent = activeChar.getEvent(SiegeEvent.class);

                    siegeEvent.addSiegeSummon(summon);
                }
                break;
            case MERCHANT:
                if (activeChar.getPet() != null || activeChar.isMounted()) {
                    return;
                }

                NpcTemplate merchantTemplate = NpcHolder.getInstance().getTemplate(getNpcId());
                MerchantInstance merchant = new MerchantInstance(IdFactory.getInstance().getNextId(), merchantTemplate);

                merchant.setCurrentHp(merchant.getMaxHp(), false);
                merchant.setCurrentMp(merchant.getMaxMp());
                merchant.setHeading(activeChar.getHeading());
                merchant.setReflection(activeChar.getReflection());
                merchant.spawnMe(activeChar.getLoc());

                ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(merchant), _lifeTime);
                break;
            case NPC:
                NpcUtils.spawnSingle(getNpcId(), activeChar.getLoc(), activeChar.getReflection(), _lifeTime, activeChar.getName());
        }

        if (isSSPossible()) {
            caster.unChargeShots(isMagic());
        }
    }

    @Override
    public boolean isOffensive() {
        return _targetType == SkillTargetType.TARGET_CORPSE;
    }
}
