package services.community;

import java.util.StringTokenizer;
import l2p.commons.dao.JdbcEntityState;

import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.model.base.Element;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.serverpackets.InventoryUpdate;
import l2p.gameserver.serverpackets.MagicSkillUse;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.model.base.TeamType;
import l2p.gameserver.templates.item.ItemTemplate;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.EnchantResult;
import l2p.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2p.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoardEnchanter extends Functions implements ScriptFile, ICommunityBoardHandler {

    static final Logger _log = LoggerFactory.getLogger(CommunityBoardEnchanter.class);
    private int enchant_item = Config.COMMUNITYBOARD_ENCHANT_ITEM;
    private int max_enchant = Config.COMMUNITYBOARD_MAX_ENCHANT;
    private int[] enchant_level = Config.COMMUNITYBOARD_ENCHANT_LVL;
    private int[] ench_price_weapon = Config.COMMUNITYBOARD_ENCHANT_PRICE_WEAPON;
    private int[] ench_price_armor = Config.COMMUNITYBOARD_ENCHANT_PRICE_ARMOR;
    private int[] atr_lvl_weapon = Config.COMMUNITYBOARD_ENCHANT_ATRIBUTE_LVL_WEAPON;
    private int[] atr_price_weapon = Config.COMMUNITYBOARD_ENCHANT_ATRIBUTE_PRICE_WEAPON;
    private int[] atr_lvl_armor = Config.COMMUNITYBOARD_ENCHANT_ATRIBUTE_LVL_ARMOR;
    private int[] atr_price_armor = Config.COMMUNITYBOARD_ENCHANT_ATRIBUTE_PRICE_ARMOR;
    private boolean atr_pvp = Config.COMMUNITYBOARD_ENCHANT_ATRIBUTE_PVP;

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Enchant Community service loaded.");
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
        return new String[]{
            "_bbsechant",
            "_bbsechantlist",
            "_bbsechantChus",
            "_bbsechantAtr",
            "_bbsechantgo",
            "_bbsechantuseAtr"
        };
    }

    @Override
    public void onBypassCommand(Player activeChar, String bypass) {
        if (!CheckCondition(activeChar)) {
            return;
        }

        if (bypass.startsWith("_bbsechant")) {
            String name = "None Name";
            String html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/enchant.htm", activeChar);
            name = ItemHolder.getInstance().getTemplate(enchant_item).getName();
            StringBuilder sb = new StringBuilder("");
            sb.append("<table width=400>");
            ItemInstance[] arr = activeChar.getInventory().getItems();
            int len = arr.length;
            for (int i = 0; i < len; i++) {
                ItemInstance _item = arr[i];
                if (_item == null || _item.getTemplate().isBelt() || _item.isCursed() || _item.isArrow()
                        || _item.getTemplate().isBracelet() || _item.getTemplate().isCloak() || _item.isNoEnchant()
                        || !_item.isEquipped() || _item.isShieldNoEnchant() || _item.getItemType() == ArmorType.SIGIL || _item.isHeroWeapon()
                        || _item.getItemId() >= 7816 && _item.getItemId() <= 7831 || _item.isShadowItem()
                        || _item.isCommonItem() || _item.getEnchantLevel() >= (max_enchant + 1) || !_item.canBeEnchanted(true)
                        || _item.getEquipSlot() == ItemTemplate.SLOT_HAIR || _item.getEquipSlot() == ItemTemplate.SLOT_DHAIR) {
                    continue;
                }
                sb.append(new StringBuilder("<tr><td><img src=icon." + _item.getTemplate().getIcon() + " width=32 height=32></td><td>"));
                sb.append(new StringBuilder("<font color=\"LEVEL\">" + _item.getTemplate().getName() + " " + (_item.getEnchantLevel() <= 0 ? "" : new StringBuilder("</font><br1><font color=3293F3>Заточено на: +" + _item.getEnchantLevel())) + "</font><br1>"));
                sb.append(new StringBuilder("Заточка за: <font color=\"LEVEL\">" + name + "</font>"));
                sb.append("<img src=\"l2ui.squaregray\" width=\"170\" height=\"1\">");
                sb.append("</td><td>");
                if (Config.ALLOW_BBS_ENCHANT_ELEMENTAR) {
                    sb.append(new StringBuilder("<button value=\"Обычная\" action=\"bypass _bbsechantlist:" + _item.getObjectId() + ";\" width=75 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"));
                }
                sb.append("</td><td>");
                if (Config.ALLOW_BBS_ENCHANT_ATT) {
                    sb.append(new StringBuilder("<button value=\"Аттрибут\" action=\"bypass _bbsechantChus:" + _item.getObjectId() + ";\" width=75 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"));
                }
                sb.append("</td></tr>");
            }

            sb.append("</table>");
            html = html.replace("%enchanter%", sb.toString());
            ShowBoard.separateAndSend(html, activeChar);
        }
        if (bypass.startsWith("_bbsechantlist")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            int ItemForEchantObjID = Integer.parseInt(mBypass[1]);
            String name = "None Name";
            String html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/enchant.htm", activeChar);
            name = ItemHolder.getInstance().getTemplate(enchant_item).getName();
            ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);

            StringBuilder sb = new StringBuilder("");
            sb.append("Для обычной заточки выбрана вещь:<br1><table width=300>");
            sb.append(new StringBuilder("<tr><td width=32><img src=icon." + EhchantItem.getTemplate().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>"));
            sb.append(new StringBuilder("<font color=\"LEVEL\">" + EhchantItem.getTemplate().getName() + " " + (EhchantItem.getEnchantLevel() <= 0 ? "" : new StringBuilder("</font><br1><font color=3293F3>Заточено на: +" + EhchantItem.getEnchantLevel())) + "</font><br1>"));

            sb.append(new StringBuilder("Заточка производится за: <font color=\"LEVEL\">" + name + "</font>"));
            sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
            sb.append(new StringBuilder("<td width=32><img src=icon." + EhchantItem.getTemplate().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>"));
            sb.append("</tr>");
            sb.append("</table>");
            sb.append("<br>");
            sb.append("<br>");
            sb.append("<table border=0 width=400><tr><td width=200>");
            for (int i = 0; i < enchant_level.length; i++) {
                if (EhchantItem.getEnchantLevel() >= enchant_level[i])
                    continue;
                sb.append(new StringBuilder("<button value=\"На +" + enchant_level[i] + " (Цена:" + (EhchantItem.getTemplate().isWeapon() != false ? ench_price_weapon[i] : ench_price_armor[i]) + " " + name + ")\" action=\"bypass _bbsechantgo:" + enchant_level[i] + ":" + (EhchantItem.getTemplate().isWeapon() != false ? ench_price_weapon[i] : ench_price_armor[i]) + ":" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
                //sb.append("<br1>");
            }
            sb.append("</td></tr></table><br1><button value=\"Назад\" action=\"bypass _bbsechant;\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            html = html.replace("%enchanter%", sb.toString());
            ShowBoard.separateAndSend(html, activeChar);
        }
        if (bypass.startsWith("_bbsechantChus")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            int ItemForEchantObjID = Integer.parseInt(mBypass[1]);
            String name = "None Name";
            String html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/enchant.htm", activeChar);
            name = ItemHolder.getInstance().getTemplate(enchant_item).getName();
            ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);

            StringBuilder sb = new StringBuilder("");
            sb.append("Для заточки на атрибут выбрана вещь:<br><table width=300>");
            sb.append(new StringBuilder("<tr><td width=32><img src=icon." + EhchantItem.getTemplate().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>"));
            sb.append(new StringBuilder("<font color=\"LEVEL\">" + EhchantItem.getTemplate().getName() + " " + (EhchantItem.getEnchantLevel() <= 0 ? "" : new StringBuilder("</font><br1><font color=3293F3>Заточено на: +" + EhchantItem.getEnchantLevel())) + "</font><br1>"));

            sb.append(new StringBuilder("Заточка производится за: <font color=\"LEVEL\">" + name + "</font>"));
            sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
            sb.append(new StringBuilder("<td width=32><img src=icon." + EhchantItem.getTemplate().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>"));
            sb.append("</tr>");
            sb.append("</table>");
            sb.append("<br>");
            sb.append("<br>");
            sb.append("<table border=0 width=400><tr><td width=200>");
            sb.append("<center><img src=icon.etc_wind_stone_i00 width=32 height=32></center><br>");
            sb.append(new StringBuilder("<button value=\"Wind \" action=\"bypass _bbsechantAtr:2:" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
            sb.append("<br><center><img src=icon.etc_earth_stone_i00 width=32 height=32></center><br>");
            sb.append(new StringBuilder("<button value=\"Earth \" action=\"bypass _bbsechantAtr:3:" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
            sb.append("<br><center><img src=icon.etc_fire_stone_i00 width=32 height=32></center><br>");
            sb.append(new StringBuilder("<button value=\"Fire \" action=\"bypass _bbsechantAtr:0:" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
            sb.append("</td><td width=200>");
            sb.append("<center><img src=icon.etc_water_stone_i00 width=32 height=32></center><br>");
            sb.append(new StringBuilder("<button value=\"Water \" action=\"bypass _bbsechantAtr:1:" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
            sb.append("<br><center><img src=icon.etc_holy_stone_i00 width=32 height=32></center><br>");
            sb.append(new StringBuilder("<button value=\"Divine \" action=\"bypass _bbsechantAtr:4:" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
            sb.append("<br><center><img src=icon.etc_unholy_stone_i00 width=32 height=32></center><br>");
            sb.append(new StringBuilder("<button value=\"Dark \" action=\"bypass _bbsechantAtr:5:" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
            sb.append("</td></tr></table><br1><button value=\"Назад\" action=\"bypass _bbsechant;\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            html = html.replace("%enchanter%", sb.toString());
            ShowBoard.separateAndSend(html, activeChar);
        }
        if (bypass.startsWith("_bbsechantAtr")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            int AtributType = Integer.parseInt(mBypass[1]);
            int ItemForEchantObjID = Integer.parseInt(mBypass[2]);
            String ElementName = "";
            if (AtributType == 0) {
                ElementName = "Fire";
            } else if (AtributType == 1) {
                ElementName = "Water";
            } else if (AtributType == 2) {
                ElementName = "Wind";
            } else if (AtributType == 3) {
                ElementName = "Earth";
            } else if (AtributType == 4) {
                ElementName = "Divine";
            } else if (AtributType == 5) {
                ElementName = "Dark";
            }
            String name = "None Name";
            String html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/enchant.htm", activeChar);
            name = ItemHolder.getInstance().getTemplate(enchant_item).getName();
            ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);
            StringBuilder sb = new StringBuilder("");
            sb.append(new StringBuilder("Выбран элемент: <font color=\"LEVEL\">" + ElementName + "</font><br1> Для заточки выбрана вещь:<br1><table width=300>"));
            sb.append(new StringBuilder("<tr><td width=32><img src=icon." + EhchantItem.getTemplate().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>"));
            sb.append(new StringBuilder("<font color=\"LEVEL\">" + EhchantItem.getTemplate().getName() + " " + (EhchantItem.getEnchantLevel() <= 0 ? "" : new StringBuilder("</font><br1><font color=3293F3>Заточено на: +" + EhchantItem.getEnchantLevel())) + "</font><br1>"));

            sb.append(new StringBuilder("Заточка производится за: <font color=\"LEVEL\">" + name + "</font>"));
            sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
            sb.append(new StringBuilder("<td width=32><img src=icon." + EhchantItem.getTemplate().getIcon() + " width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>"));
            sb.append("</tr>");
            sb.append("<br1>");
            sb.append("<br1>");
            if (EhchantItem.getTemplate().getCrystalType() == ItemTemplate.Grade.S || EhchantItem.getTemplate().getCrystalType() == ItemTemplate.Grade.S80 || EhchantItem.getTemplate().getCrystalType() == ItemTemplate.Grade.S84) {
                sb.append("<table border=0 width=400><tr><td width=200>");
                for (int i = 0; i < (EhchantItem.getTemplate().isWeapon() != false ? atr_lvl_weapon.length : atr_lvl_armor.length); i++) {
                    sb.append("<center><button value=\"На +");
                    sb.append(new StringBuilder((EhchantItem.getTemplate().isWeapon() != false ? atr_lvl_weapon[i] : atr_lvl_armor[i]) + " (Цена:" + (EhchantItem.getTemplate().isWeapon() != false ? atr_price_weapon[i] : atr_price_armor[i]) + " " + name + ")\" action=\"bypass _bbsechantuseAtr:" + (EhchantItem.getTemplate().isWeapon() != false ? atr_lvl_weapon[i] : atr_lvl_armor[i]) + ":" + AtributType + ":" + (EhchantItem.getTemplate().isWeapon() != false ? atr_price_weapon[i] : atr_price_armor[i]) + ":" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
                    sb.append("<br1>");
                }
                sb.append("</td></tr></table><br1>");
            } else if (EhchantItem.getTemplate().getCrystalType() == ItemTemplate.Grade.S || EhchantItem.getTemplate().getCrystalType() == ItemTemplate.Grade.S80 || EhchantItem.getTemplate().getCrystalType() == ItemTemplate.Grade.S84) {
                sb.append("<table border=0 width=400><tr><td width=200>");
                for (int i = 0; i < (EhchantItem.getTemplate().isWeapon() != false ? atr_lvl_weapon.length : atr_lvl_armor.length); i++) {
                    sb.append(new StringBuilder("<center><button value=\"На +" + (EhchantItem.getTemplate().isWeapon() != false ? atr_lvl_weapon[i] : atr_lvl_armor[i]) + " (Цена:" + (EhchantItem.getTemplate().isWeapon() != false ? atr_price_weapon[i] : atr_price_armor[i]) + " " + name + ")\" action=\"bypass _bbsechantuseAtr:" + (EhchantItem.getTemplate().isWeapon() != false ? atr_lvl_weapon[i] : atr_lvl_armor[i]) + ":" + AtributType + ":" + (EhchantItem.getTemplate().isWeapon() != false ? atr_price_weapon[i] : atr_price_armor[i]) + ":" + ItemForEchantObjID + ";\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">"));
                    sb.append("<br1>");
                }
                sb.append("</td></tr></table><br1>");
                sb.append("</table>");
            } else {
                sb.append("<table border=0 width=400><tr><td width=200>");
                sb.append("<br1>");
                sb.append("<br1>");
                sb.append("<br1>");
                sb.append("<br1>");
                sb.append("<center><font color=\"LEVEL\">Заточка данной вещи не возможна!</font></center>");
                sb.append("<br1>");
                sb.append("<br1>");
                sb.append("<br1>");
                sb.append("<br1>");
                sb.append("</td></tr></table><br1>");
            }
            sb.append("<button value=\"Назад\" action=\"bypass _bbsechant;\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            html = html.replace("%enchanter%", sb.toString());
            ShowBoard.separateAndSend(html, activeChar);
        }
        if (bypass.startsWith("_bbsechantgo")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            int EchantVal = Integer.parseInt(mBypass[1]);
            int EchantPrice = Integer.parseInt(mBypass[2]);
            int EchantObjID = Integer.parseInt(mBypass[3]);
            ItemTemplate item = ItemHolder.getInstance().getTemplate(enchant_item);
            ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
            ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(EchantObjID);
            if (pay != null && pay.getCount() >= EchantPrice) {
                activeChar.getInventory().destroyItem(pay, EchantPrice);
                
                activeChar.getInventory().unEquipItem(EhchantItem);
                EhchantItem.setEnchantLevel(EchantVal);
                EhchantItem.setJdbcState(JdbcEntityState.UPDATED);
                EhchantItem.update();
                
                activeChar.getInventory().equipItem(EhchantItem);
                activeChar.sendPacket(new InventoryUpdate().addModifiedItem(EhchantItem));
                
                activeChar.sendPacket(EnchantResult.SUCESS);

                if (EhchantItem.getEnchantLevel() > 3) {
                    activeChar.getPlayer().broadcastPacket(new MagicSkillUse(activeChar, activeChar, 5965, 1, 500, 1500));
                }
                
                activeChar.updateStats();
                activeChar.sendMessage(new StringBuilder("" + EhchantItem.getTemplate().getName() + " было заточено до " + EchantVal + ".").toString());
                Log.add(new StringBuilder(activeChar.getName() + " enchant item:" + EhchantItem.getTemplate().getName() + " val: " + EchantVal + "").toString(), "wmzSeller");
                onBypassCommand(activeChar, "_bbsechant");
            } else {
                activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            }
        }

        if (bypass.startsWith("_bbsechantuseAtr")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            int EchantVal = Integer.parseInt(mBypass[1]);
            int AtrType = Integer.parseInt(mBypass[2]);
            int EchantPrice = Integer.parseInt(mBypass[3]);
            int EchantObjID = Integer.parseInt(mBypass[4]);
            ItemTemplate item = ItemHolder.getInstance().getTemplate(enchant_item);
            ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
            ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(EchantObjID);
            if (EhchantItem.isWeapon()) {
                if (pay != null && pay.getCount() >= EchantPrice) {
                    activeChar.getInventory().destroyItem(pay, EchantPrice);
                    activeChar.getInventory().unEquipItem(EhchantItem);
                    EhchantItem.setAttributeElement(getAttr(AtrType), EchantVal);
                    activeChar.getInventory().equipItem(EhchantItem);
                    activeChar.sendPacket(new InventoryUpdate().addModifiedItem(EhchantItem));
                    activeChar.broadcastUserInfo(true);
                    activeChar.sendMessage(new StringBuilder("Значение атрибута " + EhchantItem.getTemplate().getName() + " увеличено до " + EchantVal + ".").toString());
                    Log.add(new StringBuilder(activeChar.getName() + " enchant item:" + EhchantItem.getTemplate().getName() + " val: " + EchantVal + " AtributType:" + AtrType).toString(), "wmzSeller");
                    onBypassCommand(activeChar, "_bbsechant");
                } else {
                    activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                }
            } else if (EhchantItem.isArmor()) {
                if (!canEnchantArmorAttribute(AtrType, EhchantItem)) {
                    activeChar.sendMessage("Невозможно вставить аттрибут в броню, не соблюдены условия");
                    return;
                }
                if (pay != null && pay.getCount() >= EchantPrice) {
                    activeChar.getInventory().destroyItem(pay, EchantPrice);
                    activeChar.getInventory().unEquipItem(EhchantItem);
                    EhchantItem.setAttributeElement(getAttr(AtrType), EchantVal);
                    activeChar.getInventory().equipItem(EhchantItem);
                    activeChar.sendPacket(new InventoryUpdate().addModifiedItem(EhchantItem));
                    activeChar.broadcastUserInfo(true);
                    activeChar.sendMessage(new StringBuilder("Значение атрибута " + EhchantItem.getTemplate().getName() + " увеличено до " + EchantVal + ".").toString());
                    Log.add(new StringBuilder(activeChar.getName() + " enchant item:" + EhchantItem.getTemplate().getName() + " val: " + EchantVal + " AtributType:" + AtrType).toString(), "wmzSeller");
                    onBypassCommand(activeChar, "_bbsechant");
                }
            } else {
                if (activeChar.isLangRus()) {
                    activeChar.sendMessage("В данную вещь нельзя вставить атрибут.");
                } else {
                    activeChar.sendMessage("In this thing you can not insert the attribute.");
                }
            }
        }
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean canEnchantArmorAttribute(int attr, ItemInstance item) {
        switch (attr) {
            case 0:
                if (item.getDefenceWater() != 0) {
                    return false;
                }
                break;
            case 1:
                if (item.getDefenceFire() != 0) {
                    return false;
                }
                break;
            case 2:
                if (item.getDefenceEarth() != 0) {
                    return false;
                }
                break;
            case 3:
                if (item.getDefenceWind() != 0) {
                    return false;
                }
                break;
            case 4:
                if (item.getDefenceUnholy() != 0) {
                    return false;
                }
                break;
            case 5:
                if (item.getDefenceHoly() != 0) {
                    return false;
                }
                break;
        }
        return true;
    }

    private Element getAttr(int attr) {
        Element El = Element.NONE;
        switch (attr) {
            case 0:
                El = Element.FIRE;
                break;
            case 1:
                El = Element.WATER;
                break;
            case 2:
                El = Element.WIND;
                break;
            case 3:
                El = Element.EARTH;
                break;
            case 4:
                El = Element.HOLY;
                break;
            case 5:
                El = Element.UNHOLY;
                break;
        }
        return El;
    }

    private static boolean CheckCondition(Player player) {
        if (player == null) {
            return false;
        }

        if (player.isDead()) {
            return false;
        }

        if ((player.getPvpFlag() != 0 || player.isInDuel() || player.isInCombat() || player.isAttackingNow())) {
            if (player.isLangRus()) {
                player.sendMessage("Во время боя нельзя использовать данную функцию.");
            } else {
                player.sendMessage("During combat, you can not use this feature.");
            }
            return false;
        }

        if (player.isInOlympiadMode()) {
            if (player.isLangRus()) {
                player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
            } else {
                player.sendMessage("During the Olympics you can not use this feature.");
            }
            return false;
        }

        if (!Config.COMMUNITYBOARD_ENCHANT_ENABLED) {
            if (player.isLangRus()) {
                player.sendMessage("Функция заточки отключена.");
            } else {
                player.sendMessage("Enchant off function.");
            }
            return false;
        }

        if (player.getTeam() != TeamType.NONE) {
            if (player.isLangRus()) {
                player.sendMessage("Нельзя использовать заточку во время эвентов.");
            } else {
                player.sendMessage("You can not use the enchant during Events.");
            }
            return false;
        }
        return true;
    }
}