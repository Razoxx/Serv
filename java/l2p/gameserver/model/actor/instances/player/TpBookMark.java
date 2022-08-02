package l2p.gameserver.model.actor.instances.player;

import l2p.gameserver.utils.Location;

public class TpBookMark
        extends Location {

    private int _icon;
    private String _name;
    private String _acronym;

    public TpBookMark(int x, int y, int z, int aicon, String aname, String aacronym) {
        super(x, y, z);
        setIcon(aicon);
        setName(aname);
        setAcronym(aacronym);
    }

    public void setIcon(int val) {
        _icon = val;
    }

    public int getIcon() {
        return _icon;
    }

    public void setName(String val) {
        _name = val;
    }

    public String getName() {
        return _name;
    }

    public void setAcronym(String val) {
        _acronym = val;
    }

    public String getAcronym() {
        return _acronym;
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != getClass())) {
            return false;
        }
        TpBookMark tp = (TpBookMark) o;

        return (tp.getName().equals(getName())) && (tp.getX() == getX()) && (tp.getY() == getY()) && (tp.getZ() == getZ());
    }
}
