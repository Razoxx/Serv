package l2p.gameserver.model.premium;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import l2p.gameserver.utils.GArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PremiumConfig {

    private static final Logger _log = LoggerFactory.getLogger(PremiumConfig.class);
    public static GArray<Configs> _configs = new GArray<Configs>();

    public static void load() {
        try {
            _configs.clear();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            File file = new File("config/Premium.xml");

            Document doc = factory.newDocumentBuilder().parse(file);

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (!"list".equalsIgnoreCase(n.getNodeName())) {
                    continue;
                }
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if (!"premium".equalsIgnoreCase(d.getNodeName())) {
                        continue;
                    }
                    Configs _config = new Configs();

                    NamedNodeMap attrs = d.getAttributes();

                    _config.ID = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());

                    _config.PRICE = Long.parseLong(attrs.getNamedItem("price").getNodeValue());
                    _config.ITEM_ID = Integer.parseInt(attrs.getNamedItem("item").getNodeValue());
                    _config.TIME = Integer.parseInt(attrs.getNamedItem("time").getNodeValue());

                    for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                        if ("RateValue".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_VALUE = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateXP".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_XP = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateSP".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_SP = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateAdena".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_ADENA = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateItem".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_ITEM = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateSpoil".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_SPOIL = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateCrafMW".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_CRAFT_MW = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateCraftDouble".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_CRAFT_DOUBLE = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateQuestReward".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_QUEST_REWARD = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("RateQuestDrop".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.RATE_QUEST_DROP = Double.parseDouble(attrs.getNamedItem("val").getNodeValue());
                        } else if ("AllowTransferItem".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ALLOW_TRANSFER_ITEM = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
                        } else if ("AllowAutoLootItem".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ALLOW_AUTO_LOOT_ITEM = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
                        } else if ("AllowHeroAura".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ALLOW_HERO_AURA = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
                        } else if ("AllowSoulSpiritShotInfinitely".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
                        } else if ("AllowSoulSpiritShotInfinitelyPet".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY_PET = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
                        } else if ("AllowArrowInfinitely".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ALLOW_ARROW_INFINITELY = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChance".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChanceArmor".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE_ARMOR = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChanceAccessory".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE_ACCESSORY = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChanceBless".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE_BLESSED = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChanceArmorBless".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE_ARMOR_BLESSED = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChanceAccessoryBless".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE_ACCESSORY_BLESSED = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChanceCrystal".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE_CRYSTAL = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChanceArmorCrystal".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE_ARMOR_CRYSTAL = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantChanceAccessoryCrystal".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.ENCHANT_CHANCE_ACCESSORY_CRYSTAL = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("UseAltEnchant".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.USE_ALT_ENCHANT = Boolean.parseBoolean(attrs.getNamedItem("val").getNodeValue());
                        } else if ("EnchantWeaponFighter".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_WEAPON_FIGHTER.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantWeaponFighterCrystal".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_WEAPON_FIGHTER_CRYSTALL.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantWeaponFighterBlessed".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_WEAPON_FIGHTER_BLESSED.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantWeaponMage".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_WEAPON_MAGE.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantWeaponMageCrystal".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_WEAPON_MAGE_CRYSTALL.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantWeaponMageBlessed".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_WEAPON_MAGE_BLESSED.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantArmor".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_ARMOR.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantArmorCrystal".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_ARMOR_CRYSTALL.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantArmorBlessed".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_ARMOR_BLESSED.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantAccessory".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_ACCESSORY.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantAccessoryCrystal".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_ACCESSORY_CRYSTALL.add(Integer.parseInt(id));
                            }
                        } else if ("EnchantAccessoryBlessed".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            String[] itemId = attrs.getNamedItem("val").getNodeValue().split(",");
                            for (String id : itemId) {
                                _config.ENCHANT_ACCESSORY_BLESSED.add(Integer.parseInt(id));
                            }
                        }
                    }
                    _configs.add(_config);
                }
            }

            _configs.sort();
            _log.info("Loaded " + _configs.size() + " Premium configs");
        } catch (Exception e) {
            _log.warn("Error parsing Premium.xml, by error: ");
        }
    }

    public static Configs getPremConfigId(int id) {
        return _configs.get(id);
    }
}
