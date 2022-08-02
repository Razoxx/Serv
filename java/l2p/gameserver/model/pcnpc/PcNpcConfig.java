package l2p.gameserver.model.pcnpc;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import l2p.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class PcNpcConfig {

    private static final Logger _log = LoggerFactory.getLogger(PcNpcConfig.class);
    private static final TIntObjectHashMap<ConfigPcNpc> _configs = new TIntObjectHashMap<ConfigPcNpc>();

    public static void load() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            File file = new File("config/pc_npc.xml");

            Document doc = factory.newDocumentBuilder().parse(file);

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (!"list".equalsIgnoreCase(n.getNodeName())) {
                    continue;
                }
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {

                    if (!"npc".equalsIgnoreCase(d.getNodeName())) {
                        continue;
                    }
                    ConfigPcNpc _config = new ConfigPcNpc();

                    NamedNodeMap attrs = d.getAttributes();

                    _config.npcId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                    _config.npcId = Integer.parseInt(attrs.getNamedItem("classId").getNodeValue());
                    _config.isHero = Boolean.parseBoolean(attrs.getNamedItem("hero").getNodeValue());
                    _config.male = Boolean.parseBoolean(attrs.getNamedItem("sex").getNodeValue());

                    for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {

                        if ("Helmet".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.helmetId = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
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
                        } else if ("coords".equalsIgnoreCase(cd.getNodeName())) {
                            attrs = cd.getAttributes();
                            _config.coords.add(Location.parseLoc(attrs.getNamedItem("loc").getNodeValue()));
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

    public static void addConfig(ConfigPcNpc config) {
        _configs.put(config.npcId, config);
    }

    public static ConfigPcNpc getPcNpcConfigId(int id) {
        return _configs.get(id);
    }

    public static int size() {
        return _configs.size();
    }
}
