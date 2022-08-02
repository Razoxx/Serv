package services.community;

import java.util.StringTokenizer;

import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.premium.PremiumConfig;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.stats.Formulas;
import l2p.gameserver.stats.Stats;
import l2p.gameserver.utils.Language;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageFavorites implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(ManageFavorites.class);

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Manage Favorites service loaded.");
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            CommunityBoardManager.getInstance().removeHandler(this);
        }
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String[] getBypassCommands() {
        return new String[]{"_bbsgetfav", "_bbssetfav_"};
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        StringTokenizer st = new StringTokenizer(bypass, "_");
        String cmd = st.nextToken();

        if ("bbsgetfav".equals(cmd)) {
            String dialog = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_getfavorite.htm", player);

            Creature target = null;

            dialog = dialog.replaceFirst("%prem%", String.valueOf(player.hasBonus() ? player.isLangRus() ? "Премиум" : "Premium" : player.isLangRus() ? "Стандартный" : "Standard"));
            dialog = dialog.replaceFirst("%name%", String.valueOf(player.getName()));
            dialog = dialog.replaceFirst("%title%", String.valueOf(player.getTitle() != null ? player.getTitle() : ""));
            dialog = dialog.replaceFirst("%clan%", String.valueOf(player.getClan() != null ? player.getClan().getName() : player.isLangRus() ? "Вы не состоите в клане" : "You are not in a clan"));
            dialog = dialog.replaceFirst("%aly%", String.valueOf(player.getAlliance() != null ? player.getAlliance().getAllyName() : player.isLangRus() ? "Вы не состоите в альянсе" : "You are not in a alliance"));
            dialog = dialog.replaceFirst("%sex%", String.valueOf(player.getSex() == 0 ? player.isLangRus() ? "Мужской" : "male" : player.isLangRus() ? "Женский" : "female"));
            dialog = dialog.replaceFirst("%sub%", String.valueOf(player.getSubClasses().size() - 1));
            dialog = dialog.replaceFirst("%exp%", String.valueOf(Config.RATE_XP * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_XP : 1)).toString());
            dialog = dialog.replaceFirst("%sp%", String.valueOf(Config.RATE_SP * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_SP : 1)).toString());
            dialog = dialog.replaceFirst("%items%", String.valueOf(Config.RATE_DROP_ITEMS * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_ITEM : 1)).toString());
            dialog = dialog.replaceFirst("%adena%", String.valueOf(Config.RATE_DROP_ADENA * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_ADENA : 1)).toString());
            dialog = dialog.replaceFirst("%spoil%", String.valueOf(Config.RATE_DROP_SPOIL * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_SPOIL : 1)).toString());
            dialog = dialog.replaceFirst("%reward%", String.valueOf(Config.RATE_QUESTS_REWARD * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_QUEST_REWARD : 1)).toString());
            dialog = dialog.replaceFirst("%qrate%", String.valueOf(Config.RATE_QUESTS_DROP * (player.hasBonus() ? PremiumConfig.getPremConfigId(player.getBonus().getBonusId()).RATE_QUEST_DROP : 1)).toString());

            if (player.isLangRus()) {
                dialog = dialog.replaceFirst("%autoloot%", player.getVarB("autoloot") ? "Включено" : "Выключено");
                dialog = dialog.replaceFirst("%lang%", player.getLanguage().toString());
                dialog = dialog.replaceFirst("%noe%", player.getVarB("NoExp") ? "Включено" : "Выключено");
                dialog = dialog.replaceFirst("%notraders%", player.getVarB("notraders") ? "Включено" : "Выключено");
                dialog = dialog.replaceFirst("%notShowBuffAnim%", player.getVarB("notShowBuffAnim") ? "Включено" : "Выключено");
                dialog = dialog.replaceFirst("%noShift%", player.getVarB("noShift") ? "Выключено" : "Включено");
                dialog = dialog.replaceFirst("%sch%", player.getVarB("SkillsHideChance") ? "Включено" : "Выключено");

                dialog = dialog.replaceFirst("%vautoloot%", player.getVarB("autoloot") ? "Выключить" : "Включить");
                dialog = dialog.replaceFirst("%vlang%", player.getLanguage() == Language.RUSSIAN ? "ENGLISH" : "RUSSIAN");
                dialog = dialog.replaceFirst("%vnoe%", player.getVarB("NoExp") ? "Выключить" : "Включить");
                dialog = dialog.replaceFirst("%vnotraders%", player.getVarB("notraders") ? "Выключить" : "Включить");
                dialog = dialog.replaceFirst("%vnotShowBuffAnim%", player.getVarB("notShowBuffAnim") ? "Выключить" : "Включить");
                dialog = dialog.replaceFirst("%vnoShift%", player.getVarB("noShift") ? "Включить" : "Выключить");
                dialog = dialog.replaceFirst("%vsch%", player.getVarB("SkillsHideChance") ? "Выключить" : "Включить");
            } else {
                dialog = dialog.replaceFirst("%autoloot%", player.getVarB("autoloot") ? "on" : "off");
                dialog = dialog.replaceFirst("%lang%", player.getLanguage().toString());
                dialog = dialog.replaceFirst("%noe%", player.getVarB("NoExp") ? "on" : "off");
                dialog = dialog.replaceFirst("%notraders%", player.getVarB("notraders") ? "on" : "off");
                dialog = dialog.replaceFirst("%notShowBuffAnim%", player.getVarB("notShowBuffAnim") ? "on" : "off");
                dialog = dialog.replaceFirst("%noShift%", player.getVarB("noShift") ? "on" : "off");
                dialog = dialog.replaceFirst("%sch%", player.getVarB("SkillsHideChance") ? "on" : "off");

                dialog = dialog.replaceFirst("%vautoloot%", player.getVarB("autoloot") ? "Switch off" : "Switch on");
                dialog = dialog.replaceFirst("%vlang%", player.getLanguage() == Language.RUSSIAN ? "ENGLISH" : "RUSSIAN");
                dialog = dialog.replaceFirst("%vnoe%", player.getVarB("NoExp") ? "Switch off" : "Switch on");
                dialog = dialog.replaceFirst("%vnotraders%", player.getVarB("notraders") ? "Switch off" : "Switch on");
                dialog = dialog.replaceFirst("%vnotShowBuffAnim%", player.getVarB("notShowBuffAnim") ? "Switch off" : "Switch on");
                dialog = dialog.replaceFirst("%vnoShift%", player.getVarB("noShift") ? "Switch on" : "Switch off");
                dialog = dialog.replaceFirst("%vsch%", player.getVarB("SkillsHideChance") ? "Switch off" : "Switch on");
            }
            dialog = dialog.replaceFirst("%regcp%", String.format("%8.2f", Formulas.calcCpRegen(player)).replace(',', '.'));
            dialog = dialog.replaceFirst("%reghp%", String.format("%8.2f", Formulas.calcHpRegen(player)).replace(',', '.'));
            dialog = dialog.replaceFirst("%regmp%", String.format("%8.2f", Formulas.calcMpRegen(player)).replace(',', '.'));
            dialog = dialog.replaceFirst("%crit%", String.format("%8.2f", (2 * player.calcStat(Stats.CRITICAL_DAMAGE, target, null))));
            dialog = dialog.replaceFirst("%critStat%", String.valueOf(player.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, null)));
            dialog = dialog.replaceFirst("%vapm%", String.valueOf(player.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0., target, null)));
            dialog = dialog.replaceFirst("%mcrit%", String.valueOf(player.calcStat(Stats.MCRITICAL_RATE, target, null)));
            dialog = dialog.replaceFirst("%dps%", String.valueOf(0));
            dialog = dialog.replaceFirst("%bleed%", String.valueOf(player.calcStat(Stats.BLEED_RESIST, target, null) - 100.));
            dialog = dialog.replaceFirst("%poison%", String.valueOf(player.calcStat(Stats.POISON_RESIST, target, null) - 100.));
            dialog = dialog.replaceFirst("%stun%", String.valueOf(player.calcStat(Stats.STUN_RESIST, target, null) - 100.));
            dialog = dialog.replaceFirst("%deten%", String.valueOf(player.calcStat(Stats.ROOT_RESIST, target, null) - 100.));
            dialog = dialog.replaceFirst("%paralys%", String.valueOf(player.calcStat(Stats.PARALYZE_RESIST, target, null) - 100.));
            dialog = dialog.replaceFirst("%dream%", String.valueOf(player.calcStat(Stats.SLEEP_RESIST, target, null) - 100.));
            dialog = dialog.replaceFirst("%mental%", String.valueOf(player.calcStat(Stats.MENTAL_RESIST, target, null) - 100.));
            dialog = dialog.replaceFirst("%debaf%", String.valueOf(player.calcStat(Stats.DEBUFF_RESIST, target, null) - 100.));

            ShowBoard.separateAndSend(dialog, player);
        } else if ("bbssetfav".equals(cmd)) {
            String cmd2 = st.nextToken();
            if ("autoloot".equals(cmd2)) {
                if (player.getVarB("autoloot")) {
                    player.unsetVar("autoloot");
                    player.setAutoLoot(false);
                } else {
                    player.setVar("autoloot", "1", -1);
                    player.setAutoLoot(true);
                }
            } else if ("lang".equals(cmd2)) {
                if (player.getLanguage() == Language.RUSSIAN) {
                    player.setVar("lang@", "en", -1);
                } else if (player.getLanguage() == Language.ENGLISH) {
                    player.setVar("lang@", "ru", -1);
                }
            } else if ("noe".equals(cmd2)) {
                if (player.getVarB("NoExp")) {
                    player.unsetVar("NoExp");
                } else {
                    player.setVar("NoExp", "1", -1);
                }
            } else if ("notraders".equals(cmd2)) {
                if (player.getVarB("notraders")) {
                    player.unsetVar("notraders");
                } else {
                    player.setVar("notraders", "1", -1);
                }
            } else if ("notShowBuffAnim".equals(cmd2)) {
                if (player.getVarB("notShowBuffAnim")) {
                    player.unsetVar("notShowBuffAnim");
                } else {
                    player.setVar("notShowBuffAnim", "1", -1);
                }
            } else if ("noShift".equals(cmd2)) {
                if (player.getVarB("noShift")) {
                    player.unsetVar("noShift");
                } else {
                    player.setVar("noShift", "1", -1);
                }
            } else if ("sch".equals(cmd2)) {
                if (player.getVarB("SkillsHideChance")) {
                    player.unsetVar("SkillsHideChance");
                } else {
                    player.setVar("SkillsHideChance", "1", -1);
                }
            } else if ("schMob".equals(cmd2)) {
            }

            onBypassCommand(player, "_bbsgetfav");

        }

    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }
}
