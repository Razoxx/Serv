package l2p.gameserver.templates.item;

/**
 * @author Ro0TT
 * @date 18.4.2012
 *
 */
public class CreateItem {

    private final int id;
    private final int count;
    private final int enchant;
    private final boolean equipable;
    private final int shortcut;

    public CreateItem(int id, int count, int enchant, boolean equipable, int shortcut) {
        this.id = id;
        this.count = count;
        this.enchant = enchant;
        this.equipable = equipable;
        this.shortcut = shortcut;
    }

    public int getItemId() {
        return id;
    }

    public int getCount() {
        return count;
    }
    
    public int getEnchantLevel() {
        return enchant;
    }

    public boolean isEquipable() {
        return equipable;
    }

    public int getShortcut() {
        return shortcut;
    }
}