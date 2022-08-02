package l2p.gameserver.model.visual;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class VisualConfig {

    private static final Logger _log = LoggerFactory.getLogger(VisualConfig.class);
    private static final TIntObjectHashMap<ConfigVisual> _configs = new TIntObjectHashMap<ConfigVisual>();

    public static void load() {
        try {
            _configs.clear();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            File file = new File("config/armsets.xml");

            Document doc = factory.newDocumentBuilder().parse(file);

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (!"list".equalsIgnoreCase(n.getNodeName())) {
                    continue;
                }
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {

                    if (!"sets".equalsIgnoreCase(d.getNodeName())) {
                        continue;
                    }
                    ConfigVisual _config = new ConfigVisual();

                    NamedNodeMap attrs = d.getAttributes();

                    _config.ID = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());

                    _config.isArmor = Boolean.parseBoolean(attrs.getNamedItem("isArmor").getNodeValue());
                    _config.nameRu = attrs.getNamedItem("name").getNodeValue();

                    _config.ITEM_ID = Integer.parseInt(attrs.getNamedItem("item").getNodeValue());
                    _config.PRICE = Long.parseLong(attrs.getNamedItem("price").getNodeValue());

                    for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {

                        if ("icon".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.icon = attrs.getNamedItem("val").getNodeValue();
                        } else if ("Helmet".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.helmetId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("FullBody".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.fullBodyId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("Chest".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.chestId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("Leggings".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.leggingsId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("Gloves".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.glovesId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("Boots".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.bootsId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        } else if ("Weapon".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.weaponId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                        }
                    }
                    addConfig(_config);
                }
            }

            _log.info("Loaded " + _configs.size() + " Visualized configs");
        } catch (IOException | NumberFormatException | ParserConfigurationException | DOMException | SAXException e) {
            _log.error("", e);
            _log.warn("Error parsing armsets.xml, by error: ");
        }
    }

    public static void addConfig(ConfigVisual config) {
        _configs.put(config.ID, config);
    }

    public static ConfigVisual getVisualConfigId(int id) {
        return _configs.get(id);
    }
    
    public static int size() {
        return _configs.size();
    }
}
