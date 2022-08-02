package l2p.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;
import l2p.commons.data.xml.AbstractDirParser;
import l2p.gameserver.Config;
import l2p.gameserver.data.xml.holder.PetDataTemplateHolder;
import l2p.gameserver.templates.StatsSet;
import l2p.gameserver.templates.pet.PetDataTemplate;
import l2p.gameserver.templates.pet.PetLvlData;
import org.dom4j.Element;

public final class PetDataTemplateParser extends AbstractDirParser<PetDataTemplateHolder> {

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {

        protected static final PetDataTemplateParser _instance = new PetDataTemplateParser();
    }

    public static PetDataTemplateParser getInstance() {
        return SingletonHolder._instance;
    }

    private PetDataTemplateParser() {
        super(PetDataTemplateHolder.getInstance());
    }

    @Override
    public File getXMLDir() {
        return new File(Config.DATAPACK_ROOT, "data/xml/pets/");
    }

    @Override
    public boolean isIgnored(File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "pet_data.dtd";
    }

    @Override
    protected void readData(Element rootElement)
            throws Exception {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();

            int npcId = Integer.parseInt(element.attributeValue("npc_id"));
            int controlItemId = element.attributeValue("control_item") == null ? 0 : Integer.parseInt(element.attributeValue("control_item"));
            String[] sync_levels = element.attributeValue("sync_level") == null ? new String[0] : element.attributeValue("sync_level").split(";");
            int[] syncLvls = new int[sync_levels.length];
            for (int i = 0; i < sync_levels.length; i++) {
                syncLvls[i] = Integer.parseInt(sync_levels[i]);
            }
            int minLvl = 99;
            int maxLvl = 0;

            PetDataTemplate template = new PetDataTemplate(npcId, controlItemId, syncLvls);
            for (Iterator<Element> attributeIterator = element.elementIterator(); attributeIterator.hasNext();) {
                Element secondElement = (Element) attributeIterator.next();
                if ("level_data".equalsIgnoreCase(secondElement.getName())) {
                    for (attributeIterator = secondElement.elementIterator(); attributeIterator.hasNext();) {
                        Element thirdElement = (Element) attributeIterator.next();
                        if ("stats".equalsIgnoreCase(thirdElement.getName())) {
                            int level = Integer.parseInt(thirdElement.attributeValue("level"));
                            if (minLvl > level) {
                                minLvl = level;
                            }
                            if (maxLvl < level) {
                                maxLvl = level;
                            }
                            StatsSet stats_set = new StatsSet();
                            for (Iterator<Element> fourthIterator = thirdElement.elementIterator(); fourthIterator.hasNext();) {
                                Element fourthElement = (Element) fourthIterator.next();
                                if ("set".equalsIgnoreCase(fourthElement.getName())) {
                                    stats_set.set(fourthElement.attributeValue("name"), fourthElement.attributeValue("value"));
                                }
                            }
                            PetLvlData lvlData = new PetLvlData(stats_set);
                            template.addLvlData(level, lvlData);
                        }
                    }
                }
            }
            Iterator<Element> attributeIterator;
            template.setMinLvl(minLvl);
            template.setMaxLvl(maxLvl);
            ((PetDataTemplateHolder) getHolder()).addTemplate(template);
        }
    }
}
