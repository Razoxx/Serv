package l2p.gameserver.handler.voicecommands.impl;

import java.text.NumberFormat;
import java.util.Locale;
import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.base.Element;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.model.premium.PremiumConfig;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.stats.Formulas;
import l2p.gameserver.stats.Stats;
import l2p.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2p.gameserver.utils.HtmlUtils;
import org.apache.commons.lang3.text.StrBuilder;

public class WhoAmI implements IVoicedCommandHandler {

    private final String[] _commandList = new String[]{"whoami", "whoiam"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        Creature target = null;

        //TODO [G1ta0] добавить рефлекты
        //TODO [G1ta0] возможно стоит показывать статы в зависимости от цели
        double hpRegen = Formulas.calcHpRegen(player);
        double cpRegen = Formulas.calcCpRegen(player);
        double mpRegen = Formulas.calcMpRegen(player);
        double hpDrain = player.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0., target, null);
        double mpDrain = player.calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0., target, null);
        double hpGain = player.calcStat(Stats.HEAL_EFFECTIVNESS, 100., target, null);
        double mpGain = player.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., target, null);
        double critPerc = 2 * player.calcStat(Stats.CRITICAL_DAMAGE, target, null);
        double critStatic = player.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, null);
        double mCritRate = player.calcStat(Stats.MCRITICAL_RATE, target, null);
        double blowRate = player.calcStat(Stats.FATALBLOW_RATE, target, null);

        ItemInstance shld = player.getSecondaryWeaponInstance();
        boolean shield = shld != null && shld.getItemType() == WeaponType.NONE;

        double shieldDef = shield ? player.calcStat(Stats.SHIELD_DEFENCE, player.getTemplate().baseShldDef, target, null) : 0.;
        double shieldRate = shield ? player.calcStat(Stats.SHIELD_RATE, target, null) : 0.;

        double xpRate = Config.RATE_XP * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_XP : 1);
        double spRate = Config.RATE_SP * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_SP : 1);
        double dropRate = Config.RATE_DROP_ITEMS * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_ITEM : 1);
        double adenaRate = Config.RATE_DROP_ADENA * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_ADENA : 1);
        double spoilRate = Config.RATE_DROP_SPOIL * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_SPOIL : 1);

        double fireResist = player.calcStat(Element.FIRE.getDefence(), 0., target, null);
        double windResist = player.calcStat(Element.WIND.getDefence(), 0., target, null);
        double waterResist = player.calcStat(Element.WATER.getDefence(), 0., target, null);
        double earthResist = player.calcStat(Element.EARTH.getDefence(), 0., target, null);
        double holyResist = player.calcStat(Element.HOLY.getDefence(), 0., target, null);
        double unholyResist = player.calcStat(Element.UNHOLY.getDefence(), 0., target, null);

        double bleedPower = player.calcStat(Stats.BLEED_POWER, 100., target, null) - 100.;
		double bleedResist = player.calcStat(Stats.BLEED_RESIST, 100., target, null) - 100.;
		double poisonPower = player.calcStat(Stats.POISON_POWER, 100., target, null) - 100.;
		double poisonResist = player.calcStat(Stats.POISON_RESIST, 100., target, null) - 100.;
		double stunPower = player.calcStat(Stats.STUN_POWER, 100., target, null) - 100.;
		double stunResist = player.calcStat(Stats.STUN_RESIST, 100., target, null) - 100.;
		double rootPower = player.calcStat(Stats.ROOT_POWER, 100., target, null) - 100.;
		double rootResist = player.calcStat(Stats.ROOT_RESIST, 100., target, null) - 100.;
		double sleepPower = player.calcStat(Stats.SLEEP_POWER, 100., target, null) - 100.;
		double sleepResist = player.calcStat(Stats.SLEEP_RESIST, 100., target, null) - 100.;
		double paralyzePower = player.calcStat(Stats.PARALYZE_POWER, 100., target, null) - 100.;
		double paralyzeResist = player.calcStat(Stats.PARALYZE_RESIST, 100., target, null) - 100.;
		double mentalPower = player.calcStat(Stats.MENTAL_POWER, 100., target, null) - 100.;
		double mentalResist = player.calcStat(Stats.MENTAL_RESIST, 100., target, null) - 100.;
		double debuffPower = player.calcStat(Stats.DEBUFF_POWER, 100., target, null) - 100.;
		double debuffResist = player.calcStat(Stats.DEBUFF_RESIST, 100., target, null) - 100.;
        double cancelPower = player.calcStat(Stats.CANCEL_POWER, target, null);
        double cancelResist = player.calcStat(Stats.CANCEL_RESIST, target, null);

        double swordResist = 100. - player.calcStat(Stats.SWORD_WPN_VULNERABILITY, target, null);
        double dualResist = 100. - player.calcStat(Stats.DUAL_WPN_VULNERABILITY, target, null);
        double bluntResist = 100. - player.calcStat(Stats.BLUNT_WPN_VULNERABILITY, target, null);
        double daggerResist = 100. - player.calcStat(Stats.DAGGER_WPN_VULNERABILITY, target, null);
        double bowResist = 100. - player.calcStat(Stats.BOW_WPN_VULNERABILITY, target, null);
        double crossbowResist = 100. - player.calcStat(Stats.CROSSBOW_WPN_VULNERABILITY, target, null);
        double poleResist = 100. - player.calcStat(Stats.POLE_WPN_VULNERABILITY, target, null);
        double fistResist = 100. - player.calcStat(Stats.FIST_WPN_VULNERABILITY, target, null);

        double critChanceResist = 100. - player.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, target, null);
        double critDamResistStatic = player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, target, null);
        double critDamResist = 100. - 100 * (player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, 1., target, null) - critDamResistStatic);

        String dialog = HtmCache.getInstance().getHtml("command/whoami.htm", player);

        NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);

        StrBuilder sb = new StrBuilder(dialog);
        sb.replaceFirst("%hpRegen%", df.format(hpRegen));
        sb.replaceFirst("%cpRegen%", df.format(cpRegen));
        sb.replaceFirst("%mpRegen%", df.format(mpRegen));
        sb.replaceFirst("%hpDrain%", df.format(hpDrain));
        sb.replaceFirst("%mpDrain%", df.format(mpDrain));
        sb.replaceFirst("%hpGain%", df.format(hpGain));
        sb.replaceFirst("%mpGain%", df.format(mpGain));
        sb.replaceFirst("%critPerc%", df.format(critPerc));
        sb.replaceFirst("%critStatic%", df.format(critStatic));
        sb.replaceFirst("%mCritRate%", df.format(mCritRate));
        sb.replaceFirst("%blowRate%", df.format(blowRate));
        sb.replaceFirst("%shieldDef%", df.format(shieldDef));
        sb.replaceFirst("%shieldRate%", df.format(shieldRate));
        sb.replaceFirst("%xpRate%", df.format(xpRate));
        sb.replaceFirst("%spRate%", df.format(spRate));
        sb.replaceFirst("%dropRate%", df.format(dropRate));
        sb.replaceFirst("%adenaRate%", df.format(adenaRate));
        sb.replaceFirst("%spoilRate%", df.format(spoilRate));
        sb.replaceFirst("%fireResist%", df.format(fireResist));
        sb.replaceFirst("%windResist%", df.format(windResist));
        sb.replaceFirst("%waterResist%", df.format(waterResist));
        sb.replaceFirst("%earthResist%", df.format(earthResist));
        sb.replaceFirst("%holyResist%", df.format(holyResist));
        sb.replaceFirst("%darkResist%", df.format(unholyResist));
        sb.replaceFirst("%bleedPower%", df.format(bleedPower));
        sb.replaceFirst("%bleedResist%", df.format(bleedResist));
        sb.replaceFirst("%poisonPower%", df.format(poisonPower));
        sb.replaceFirst("%poisonResist%", df.format(poisonResist));
        sb.replaceFirst("%stunPower%", df.format(stunPower));
        sb.replaceFirst("%stunResist%", df.format(stunResist));
        sb.replaceFirst("%rootPower%", df.format(rootPower));
        sb.replaceFirst("%rootResist%", df.format(rootResist));
        sb.replaceFirst("%sleepPower%", df.format(sleepPower));
        sb.replaceFirst("%sleepResist%", df.format(sleepResist));
        sb.replaceFirst("%paralyzePower%", df.format(paralyzePower));
        sb.replaceFirst("%paralyzeResist%", df.format(paralyzeResist));
        sb.replaceFirst("%mentalPower%", df.format(mentalPower));
        sb.replaceFirst("%mentalResist%", df.format(mentalResist));
        sb.replaceFirst("%debuffPower%", df.format(debuffPower));
        sb.replaceFirst("%debuffResist%", df.format(debuffResist));
        sb.replaceFirst("%cancelPower%", df.format(cancelPower));
        sb.replaceFirst("%cancelResist%", df.format(cancelResist));
        sb.replaceFirst("%swordResist%", df.format(swordResist));
        sb.replaceFirst("%dualResist%", df.format(dualResist));
        sb.replaceFirst("%bluntResist%", df.format(bluntResist));
        sb.replaceFirst("%daggerResist%", df.format(daggerResist));
        sb.replaceFirst("%bowResist%", df.format(bowResist));
        sb.replaceFirst("%crossbowResist%", df.format(crossbowResist));
        sb.replaceFirst("%fistResist%", df.format(fistResist));
        sb.replaceFirst("%poleResist%", df.format(poleResist));
        sb.replaceFirst("%critChanceResist%", df.format(critChanceResist));
        sb.replaceFirst("%critDamResist%", df.format(critDamResist));

        HtmlMessage msg = new HtmlMessage(0);
        msg.setHtml(HtmlUtils.bbParse(sb.toString()));
        player.sendPacket(msg);

        return true;
    }
}
