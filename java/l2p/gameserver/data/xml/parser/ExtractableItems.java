package l2p.gameserver.data.xml.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import javolution.util.FastList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2p.commons.crypt.CryptUtil;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.serverpackets.SystemMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExtractableItems {

    private static final Logger _log = LoggerFactory.getLogger(ExtractableItems.class);
    private static ExtractableItems _instance;
    static Map<Integer, L2ExtractableItems> _lists;

    public static ExtractableItems getInstance() {
        if (_instance == null) {
            _instance = new ExtractableItems();
        }
        return _instance;
    }

    public static void reload() {
        _instance = new ExtractableItems();
    }

    public ExtractableItems() {
        _lists = new HashMap<Integer, L2ExtractableItems>();
        _log.info("ExtractableItems: Initializing");
        load();
        _log.info("ExtractableItems: Loaded " + _lists.size() + " Extractable.");
    }

    private void load() {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            final File file = new File(Config.DATAPACK_ROOT + "/data/xml/other/extractable_items.xml");
            if (!file.exists()) {
                if (Config.DEBUG) {
                    System.out.println("ExtractableItems: NO FILE");
                }
                return;
            }

            final Document doc = factory.newDocumentBuilder().parse(CryptUtil.decryptOnDemand(file));
            int counterItems = 0;

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if ("item".equalsIgnoreCase(d.getNodeName())) {
                            final NamedNodeMap attrs = d.getAttributes();
                            final int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                            if (id == 0) {
                                continue;
                            }
                            counterItems++;
                            final int type = Integer.parseInt(attrs.getNamedItem("type").getNodeValue());
                            boolean isQuest = false;
                            if (attrs.getNamedItem("quest") != null) {
                                isQuest = Boolean.parseBoolean(attrs.getNamedItem("quest").getNodeValue());
                            }
                            final L2ExtractableItems ei = new L2ExtractableItems(id);
                            ei.setType(type);
                            ei.setIsQuest(isQuest);
                            for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                                if ("product".equalsIgnoreCase(cd.getNodeName())) {
                                    final int itemId = Integer.parseInt(cd.getAttributes().getNamedItem("id").getNodeValue());
                                    final int chance = Integer.parseInt(cd.getAttributes().getNamedItem("chance").getNodeValue());
                                    final int count = Integer.parseInt(cd.getAttributes().getNamedItem("count").getNodeValue());
                                    final ExtractableItemsList item = new ExtractableItemsList();
                                    item.setProductId(itemId);
                                    item.setCount(count);
                                    item.setChance(chance);
                                    ei.addItem(item);
                                }
                            }
                            _lists.put(id, ei);
                        }
                    }
                }
            }
            if (Config.DEBUG) {
                System.out.println("ExtractableItems: OK");
            }
        } catch (final Exception e) {
            _log.error("ExtractableItems: Error parsing extractable_items file. " + e);
        }
    }

    public boolean useHandler(final Creature playable, final ItemInstance item) {
        if (playable == null || item == null) {
            return false;
        }
        if (!playable.isPlayer()) {
            return false;
        }
        final L2ExtractableItems ei = _lists.get(item.getItemId());
        if (ei == null) {
            return false;
        }
        Player player = playable.getPlayer();
        if (ei.isQuset() && !player.isQuestContinuationPossible(true)) {
            player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return false;
        }
        if (ei.getType() == 0) {
            return ei.getItemByChance(item, player);
        } else if (ei.getType() == 1) {
            return ei.getAllItems(item, player);
        }
        return false;
    }

    private class L2ExtractableItems {

        private final FastList<ExtractableItemsList> _items = FastList.newInstance();
        private final int _itemId;
        private int _type;
        private boolean _isQuest;

        private L2ExtractableItems(int itemId) {
            _itemId = itemId;
        }

        private void setType(int type) {
            _type = type;
        }

        private void setIsQuest(boolean isQuest) {
            _isQuest = isQuest;
        }

        private int getItem() {
            return _itemId;
        }

        private int getType() {
            return _type;
        }

        private boolean isQuset() {
            return _isQuest;
        }

        private void addItem(ExtractableItemsList item) {
            _items.add(item);
        }

        private boolean getItemByChance(ItemInstance item, Player player) {
            int chancesumm = 0;
            int productId = 0;
            int chance = Rnd.get(0, 9999);
            int count = 0;
            player.getInventory().destroyItemByItemId(item.getItemId(), 1);
            player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_DISAPPEARED).addItemName(item.getItemId()));
            for (ExtractableItemsList items : _items) {
                chancesumm = chancesumm + items.getChance();
                if (chancesumm > chance) {
                    productId = items.getProductId();
                    count = items.getCount();
                    player.getInventory().addItem(productId, count);
                    if (count > 1) {
                        player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S2_S1S).addItemName(productId).addNumber(count));
                    } else {
                        player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1).addItemName(productId));
                    }
                    return true;
                }
            }
            return true;
        }

        private boolean getAllItems(ItemInstance item, Player player) {
            int productId = 0;
            int count = 0;
            player.getInventory().destroyItemByItemId(item.getItemId(), 1);
            player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_DISAPPEARED).addItemName(item.getItemId()));
            for (ExtractableItemsList items : _items) {
                if (Rnd.get(0, 9999) < items.getChance()) {
                    productId = items.getProductId();
                    count = items.getCount();
                    player.getInventory().addItem(productId, count);
                    if (count > 1) {
                        player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S2_S1S).addItemName(productId).addNumber(count));
                    } else {
                        player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1).addItemName(productId));
                    }
                }
            }
            return true;
        }
    }

    private class ExtractableItemsList {

        public int _productId;
        public int _chance;
        public int _count;

        private void setProductId(int productId) {
            _productId = productId;
        }

        private void setChance(int chance) {
            _chance = chance;
        }

        private void setCount(int count) {
            _count = count;
        }

        private int getProductId() {
            return _productId;
        }

        private int getChance() {
            return _chance;
        }

        private int getCount() {
            return _count;
        }
    }
}