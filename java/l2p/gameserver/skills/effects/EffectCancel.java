package l2p.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;
import l2p.commons.collections.CollectionUtils;
import l2p.commons.collections.LazyArrayList;
import l2p.commons.util.Rnd;
import l2p.gameserver.model.Effect;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.stats.Env;
import l2p.gameserver.stats.Formulas;
import l2p.gameserver.stats.Stats;
import l2p.gameserver.utils.EffectsComparator;
import org.apache.commons.lang3.ArrayUtils;

public class EffectCancel
        extends Effect {

    private final int _minChance;
    private final int _maxChance;
    private final int _cancelRate;
    private final String[] _stackTypes;
    private final int _negateCount;

    public EffectCancel(Env env, EffectTemplate template) {
        super(env, template);
        _cancelRate = template.getParam().getInteger("cancelRate", 0);
        _minChance = template.getParam().getInteger("minChance", 25);
        _maxChance = template.getParam().getInteger("maxChance", 75);
        _negateCount = template.getParam().getInteger("negateCount", 5);
        String st = template.getParam().getString("negateStackTypes", null);
        _stackTypes = (st != null ? st.split(";") : null);
    }

    @Override
    public void onStart() {
        if (_effected.getEffectList().isEmpty()) {
            return;
        }
        List<Effect> effectList = new ArrayList(_effected.getEffectList().getAllEffects());
        CollectionUtils.shellSort(effectList, EffectsComparator.getReverseInstance());

        LazyArrayList<Effect> buffList = LazyArrayList.newInstance();
        for (Effect e : effectList) {
            if ((e.isCancelable()) && (!e.getSkill().isToggle()) && (!e.isOffensive()) && ((_stackTypes == null)
                    || (ArrayUtils.contains(_stackTypes, e.getStackType())) || (ArrayUtils.contains(_stackTypes, e.getStackType2())))) {
                buffList.add(e);
            }
        }
        if (buffList.isEmpty()) {
            LazyArrayList.recycle(buffList);
            return;
        }
        boolean[] debug = Formulas.isDebugEnabled(_effector, _effected);
        boolean debugGlobal = debug[0];
        boolean debugCaster = debug[1];
        boolean debugTarget = debug[2];
        StringBuilder stat = null;

        double cancel_res_multiplier = Math.max(1.0D - _effected.calcStat(Stats.CANCEL_RESIST, 0.0D, null, null) / 100.0D, 0.0D);
        int magicLevel = getSkill().getMagicLevel();
        if (debugGlobal) {
            stat = new StringBuilder(100);
            stat.append("Cancel power:");
            stat.append(_cancelRate);
            stat.append(" res:");
            stat.append(String.format("%1.1f", new Object[]{cancel_res_multiplier}));
            stat.append("\n");
        }
        int negated = 0;
        for (Effect e : buffList) {
            if (negated >= _negateCount) {
                break;
            }
            int eml = e.getSkill().getMagicLevel();
            int dml = magicLevel - (eml == 0 ? _effected.getLevel() : eml);
            int buffTime = e.getTimeLeft() / 120;
            double prelimChance = (2.0D * dml + _cancelRate + buffTime) * cancel_res_multiplier;
            prelimChance = Math.max(Math.min(prelimChance, _maxChance), _minChance);
            boolean result = Rnd.chance(prelimChance);
            if (result) {
                negated++;
                e.exit();
                if (_effected.isPlayer()) {
                    _effected.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getDisplayId(), e.getSkill().getDisplayLevel()));
                }
            }
            if (debugGlobal) {
                stat.append(e.getSkill().getName());
                stat.append(" Lvl:");
                stat.append(eml);
                stat.append(" Delta:");
                stat.append(dml);
                stat.append(" Time:");
                stat.append(buffTime);
                stat.append(" Chance:");
                stat.append(String.format("%1.1f", new Object[]{Double.valueOf(prelimChance)}));
                if (!result) {
                    stat.append(" failed");
                }
                stat.append("\n");
            }
        }
        if (debugGlobal) {
            if (debugCaster) {
                _effector.getPlayer().sendMessage(stat.toString());
            }
            if (debugTarget) {
                _effected.getPlayer().sendMessage(stat.toString());
            }
        }
        buffList.clear();
        LazyArrayList.recycle(buffList);
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
