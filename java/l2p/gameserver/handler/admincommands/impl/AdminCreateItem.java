package l2p.gameserver.handler.admincommands.impl;

import l2p.commons.dao.JdbcEntityState;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.data.StringHolder;
import l2p.gameserver.handler.admincommands.IAdminCommandHandler;
import l2p.gameserver.model.GameObject;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.model.base.Element;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.model.mail.Mail;
import l2p.gameserver.serverpackets.ExNoticePostArrived;
import l2p.gameserver.serverpackets.InventoryUpdate;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.utils.ItemFunctions;
import l2p.gameserver.utils.Location;
import l2p.gameserver.utils.Log;

public class AdminCreateItem implements IAdminCommandHandler {

    private static enum Commands {

        admin_itemcreate,
        admin_create_item,
        admin_create_item_all,
        admin_create_item_mail_all,
        admin_create_item_target,
        admin_ci,
        admin_spreaditem,
        admin_add_pp,
        admin_add_pcp,
        admin_create_item_element
    }

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().UseGMShop) {
            return false;
        }

        switch (command) {
            case admin_itemcreate:
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_ci:
            case admin_create_item:
                try {
                    if (wordList.length < 2) {
                        activeChar.sendMessage("USAGE: create_item id [count]");
                        return false;
                    }

                    int item_id = Integer.parseInt(wordList[1]);
                    long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
                    createItem(activeChar, item_id, item_count);
                } catch (NumberFormatException nfe) {
                    activeChar.sendMessage("USAGE: create_item id [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_create_item_all:
                try {
                    if (wordList.length < 2) {
                        activeChar.sendMessage("USAGE: create_item id [count]");
                        return false;
                    }

                    int item_id = Integer.parseInt(wordList[1]);
                    long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
                    for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                        createItem(player, item_id, item_count);
                    }
                } catch (NumberFormatException nfe) {
                    activeChar.sendMessage("USAGE: create_item id [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;

            case admin_create_item_mail_all:
                try {
                    if (wordList.length < 2) {
                        activeChar.sendMessage("USAGE: admin_create_item_mail_all id [count]");
                        return false;
                    }

                    int item_id = Integer.parseInt(wordList[1]);
                    long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);

                    for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                        createItemMail(player, item_id, item_count);
                    }
                } catch (NumberFormatException nfe) {
                    activeChar.sendMessage("USAGE: admin_create_item_mail_all id [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;

            case admin_create_item_target:
                try {
                    GameObject target = activeChar.getTarget();
                    if (target == null || !(target.isPlayer() || target.isPet())) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    if (wordList.length < 2) {
                        activeChar.sendMessage("USAGE: create_item_target id [count]");
                        return false;
                    }

                    int item_id = Integer.parseInt(wordList[1]);
                    long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
                    createItem((Player) activeChar.getTarget(), item_id, item_count);
                } catch (NumberFormatException nfe) {
                    activeChar.sendMessage("USAGE: create_item_target id [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_add_pp:
                try {
                    GameObject target = activeChar.getTarget();
                    if (target == null || !(target.isPlayer() || target.isPet())) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    Player player = target.getPlayer();
                    if (wordList.length < 2) {
                        activeChar.sendMessage("USAGE: add_pp [count]");
                        return false;
                    }

                    int item_count = Integer.parseInt(wordList[1]);
                    player.addPremiumPoints(item_count);
                } catch (NumberFormatException nfe) {
                    activeChar.sendMessage("USAGE: add_pp [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_add_pcp:
                try {
                    GameObject target = activeChar.getTarget();
                    if (target == null || !(target.isPlayer() || target.isPet())) {
                        activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                        return false;
                    }
                    if (wordList.length < 2) {
                        activeChar.sendMessage("USAGE: add_pcp [count]");
                        return false;
                    }

                    int item_count = Integer.parseInt(wordList[1]);
                    Player player = target.getPlayer();
                    player.addPcBangPoints(item_count, false);
                } catch (NumberFormatException nfe) {
                    activeChar.sendMessage("USAGE: add_pcp [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_spreaditem:
                try {
                    int id = Integer.parseInt(wordList[1]);
                    int num = wordList.length > 2 ? Integer.parseInt(wordList[2]) : 1;
                    long count = wordList.length > 3 ? Long.parseLong(wordList[3]) : 1;
                    for (int i = 0; i < num; i++) {
                        ItemInstance createditem = ItemFunctions.createItem(id);
                        createditem.setCount(count);
                        createditem.dropMe(activeChar, Location.findPointToStay(activeChar, 100));
                    }
                } catch (NumberFormatException nfe) {
                    activeChar.sendMessage("Specify a valid number.");
                } catch (StringIndexOutOfBoundsException e) {
                    activeChar.sendMessage("Can't create this item.");
                }
                break;
            case admin_create_item_element:
                try {
                    if (wordList.length < 4) {
                        activeChar.sendMessage("USAGE: create_item_attribue [id] [element id] [value]");
                        return false;
                    }

                    int item_id = Integer.parseInt(wordList[1]);
                    int elementId = Integer.parseInt(wordList[2]);
                    int value = Integer.parseInt(wordList[3]);
                    if (elementId > 5 || elementId < 0) {
                        activeChar.sendMessage("Improper element Id");
                        return false;
                    }
                    if (value < 1 || value > 300) {
                        activeChar.sendMessage("Improper element value");
                        return false;
                    }

                    ItemInstance item = createItem(activeChar, item_id, 1);
                    Element element = Element.getElementById(elementId);
                    item.setAttributeElement(element, item.getAttributeElementValue(element, false) + value);
                    item.setJdbcState(JdbcEntityState.UPDATED);
                    item.update();
                    activeChar.sendPacket(new InventoryUpdate().addModifiedItem(item));
                } catch (NumberFormatException nfe) {
                    activeChar.sendMessage("USAGE: create_item id [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("data/html/admin/itemcreation.htm"));
                break;
        }

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private ItemInstance createItem(Player activeChar, int itemId, long count) {
        ItemInstance createditem = ItemFunctions.createItem(itemId);
        createditem.setCount(count);
        Log.LogItem(activeChar, Log.Create, createditem);
        activeChar.getInventory().addItem(createditem);
        if (!createditem.isStackable()) {
            for (long i = 0; i < count - 1; i++) {
                createditem = ItemFunctions.createItem(itemId);
                Log.LogItem(activeChar, Log.Create, createditem);
                activeChar.getInventory().addItem(createditem);
            }
        }
        activeChar.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
        return createditem;
    }

    private void createItemMail(Player activeChar, int itemId, long count) {
        Mail mail = new Mail();

        mail.setSenderId(1);
        mail.setSenderName(StringHolder.getInstance().getNotNull(activeChar, "item.mail.all.npc"));
        mail.setReceiverId(activeChar.getObjectId());
        mail.setReceiverName(activeChar.getName());
        mail.setTopic(StringHolder.getInstance().getNotNull(activeChar, "item.mail.all.title"));
        mail.setBody(StringHolder.getInstance().getNotNull(activeChar, "item.mail.all.text"));
        mail.setPrice(0);
        mail.setUnread(true);

        ItemInstance item = ItemFunctions.createItem(itemId);
        item.setLocation(ItemInstance.ItemLocation.MAIL);
        item.setCount(count);
        item.save();
        mail.addAttachment(item);

        mail.setType(Mail.SenderType.NEWS_INFORMER);
        mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));

        mail.save();

        Player player = World.getPlayer(activeChar.getName());
        if (player != null) {
            player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
            player.sendPacket(Msg.THE_MAIL_HAS_ARRIVED);
        }
    }
}
