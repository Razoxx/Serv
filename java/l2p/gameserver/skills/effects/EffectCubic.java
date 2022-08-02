package l2p.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import l2p.commons.threading.RunnableImpl;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.ai.CtrlEvent;
import l2p.gameserver.data.xml.holder.CubicHolder;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Effect;
import l2p.gameserver.model.GameObject;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.serverpackets.MagicSkillLaunched;
import l2p.gameserver.serverpackets.MagicSkillUse;
import l2p.gameserver.stats.Env;
import l2p.gameserver.templates.CubicTemplate;

public class EffectCubic extends Effect {
    
    private class ActionTask extends RunnableImpl {
        
        @Override
        public void runImpl() throws Exception {
            if (!isActive()) {
                return;
            }
            
            Player player = _effected != null && _effected.isPlayer() ? (Player) _effected : null;
            if (player == null) {
                return;
            }
            
            doAction(player);
        }
    }
    private final CubicTemplate _template;
    private Future<?> _task = null;
    private long _reuse = 0L;
    
    public EffectCubic(Env env, EffectTemplate template) {
        super(env, template);
        _template = CubicHolder.getInstance().getTemplate(getTemplate().getParam().getInteger("cubicId"), getTemplate().getParam().getInteger("cubicLevel"));
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Player player = _effected.getPlayer();
        if (player == null) {
            return;
        }
        
        player.addCubic(this);
        _task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ActionTask(), 1000L, 1000L);
    }
    
    @Override
    public void onExit() {
        super.onExit();
        Player player = _effected.getPlayer();
        if (player == null) {
            return;
        }
        
        player.removeCubic(getId());
        _task.cancel(true);
        _task = null;
    }
    
    public void doAction(Player player) {
        if (_reuse > System.currentTimeMillis()) {
            return;
        }
        boolean result = false;
        int chance = Rnd.get(100);
        for (Map.Entry<Integer, List<CubicTemplate.SkillInfo>> entry : _template.getSkills()) {
            if ((chance -= (entry.getKey())) < 0) {
                for (CubicTemplate.SkillInfo skillInfo : entry.getValue()) {
                    switch (skillInfo.getActionType()) {
                        case ATTACK:
                            result = doAttack(player, skillInfo);
                            break;
                        case DEBUFF:
                            result = doDebuff(player, skillInfo);
                            break;
                        case HEAL:
                            result = doHeal(player, skillInfo);
                            break;
                        case CANCEL:
                            result = doCancel(player, skillInfo);
                            break;
                    }
                }
                
                break;
            }
        }
        if (result) {
            _reuse = (System.currentTimeMillis() + _template.getDelay() * 1000L);
        }
    }
    
    @Override
    protected boolean onActionTime() {
        return false;
    }
    
    @Override
    public boolean isHidden() {
        return true;
    }
    
    @Override
    public boolean isCancelable() {
        return false;
    }
    
    public int getId() {
        return _template.getId();
    }
    
    private static boolean doHeal(final Player player, CubicTemplate.SkillInfo info) {
        final Skill skill = info.getSkill();
        Creature target = null;
        if (player.getParty() == null) {
            if (!player.isCurrentHpFull() && !player.isDead()) {
                target = player;
            }
        } else {
            double currentHp = Integer.MAX_VALUE;
            for (Player member : player.getParty().getPartyMembers()) {
                if (member != null) {
                    if (player.isInRange(member, info.getSkill().getCastRange()) && !member.isCurrentHpFull() && !member.isDead() && member.getCurrentHp() < currentHp) {
                        currentHp = member.getCurrentHp();
                        target = member;
                    }
                }
            }
        }
        
        if (target == null) {
            return false;
        }
        
        int chance = info.getChance((int) target.getCurrentHpPercents());
        
        if (!Rnd.chance(chance)) {
            return false;
        }
        
        final Creature aimTarget = target;
        player.broadcastPacket(new MagicSkillUse(player, aimTarget, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                List<Creature> targets = new ArrayList<Creature>(1);
                targets.add(aimTarget);
                player.broadcastPacket(new MagicSkillLaunched(player, skill.getDisplayId(), skill.getDisplayLevel(), targets));
                player.callSkill(skill, targets, false);
            }
        }, skill.getHitTime());
        return true;
    }
    
    private static boolean doAttack(final Player player, CubicTemplate.SkillInfo info) {
        if (!Rnd.chance(info.getChance())) {
            return false;
        }
        
        Creature target = getTarget(player, info);
        if (target == null) {
            return false;
        }
        final Creature aimTarget = target;
        final Skill skill = info.getSkill();
        player.broadcastPacket(new MagicSkillUse(player, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                List<Creature> targets = new ArrayList<Creature>(1);
                targets.add(aimTarget);
                
                player.broadcastPacket(new MagicSkillLaunched(player, skill.getDisplayId(), skill.getDisplayLevel(), targets));
                player.callSkill(skill, targets, false);
                
                if (aimTarget.isNpc()) {
                    if (aimTarget.paralizeOnAttack(player)) {
                        if (Config.PARALIZE_ON_RAID_DIFF) {
                            player.paralizeMe(aimTarget);
                        }
                    } else {
                        int damage = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : (int) skill.getPower();
                        aimTarget.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, damage);
                    }
                }
            }
        }, skill.getHitTime());
        return true;
    }
    
    private static boolean doDebuff(final Player player, CubicTemplate.SkillInfo info) {
        if (!Rnd.chance(info.getChance())) {
            return false;
        }
        
        Creature target = getTarget(player, info);
        if (target == null) {
            return false;
        }
        final Creature aimTarget = target;
        final Skill skill = info.getSkill();
        player.broadcastPacket(new MagicSkillUse(player, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                List<Creature> targets = new ArrayList<Creature>(1);
                targets.add(aimTarget);
                player.broadcastPacket(new MagicSkillLaunched(player, skill.getDisplayId(), skill.getDisplayLevel(), targets));
                player.callSkill(skill, targets, false);
            }
        }, skill.getHitTime());
        
        return true;
    }
    
    private static boolean doCancel(final Player player, CubicTemplate.SkillInfo info) {
        if (!Rnd.chance(info.getChance())) {
            return false;
        }
        
        final Skill skill = info.getSkill();
        boolean hasDebuff = false;
        for (Effect e : player.getEffectList().getAllEffects()) {
            if (e.isOffensive() && e.isCancelable() && !e.getTemplate()._applyOnCaster) {
                hasDebuff = true;
                break;
            }
        }
        
        if (!hasDebuff) {
            return false;
        }
        player.broadcastPacket(new MagicSkillUse(player, player, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                final List<Creature> targets = new ArrayList<Creature>(1);
                targets.add(player);
                player.broadcastPacket(new MagicSkillLaunched(player, skill.getDisplayId(), skill.getDisplayLevel(), targets));
                player.callSkill(skill, targets, false);
            }
        }, skill.getHitTime());
        return true;
    }
    
    private static Creature getTarget(Player owner, CubicTemplate.SkillInfo info) {
        if (!owner.isInCombat()) {
            return null;
        }
        GameObject object = owner.getTarget();
        if ((object == null) || (!object.isCreature())) {
            return null;
        }
        Creature target = (Creature) object;
        if (target.isDead()) {
            return null;
        }
        if ((target.getCurrentHp() < info.getMinHp()) && (target.getCurrentHpPercents() < info.getMinHpPercent())) {
            return null;
        }
        if ((target.isDoor()) && (!info.isCanAttackDoor())) {
            return null;
        }
        if (!owner.isInRangeZ(target, info.getSkill().getCastRange())) {
            return null;
        }
        Player targetPlayer = target.getPlayer();
        if ((targetPlayer != null) && (!targetPlayer.isInCombat())) {
            return null;
        }
        if (!target.isAutoAttackable(owner)) {
            return null;
        }
        return target;
    }
}
