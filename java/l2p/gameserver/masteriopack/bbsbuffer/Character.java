package l2p.gameserver.masteriopack.bbsbuffer;

import l2p.gameserver.masteriopack.bbsbuffer.DBStatus;
import javolution.util.FastMap;

/**
 * @author Masterio
 */
public class Character {

    private int _charObjId = 0;

    // If cleaner check it as TRUE then object will be removed.
    private boolean toRemove = false;

    /**
     * [scheme_id, Scheme]
     */ // this list contains inserted, updated, deleted schemes.
    private FastMap<Integer, Scheme> _schemeList = new FastMap<>();

    public Character(int charObjId) {
        _charObjId = charObjId;
    }

    public boolean isSchemeNameExists(int charObjId, String schemeName) {
        FastMap<Integer, Scheme> sl = getSchemeList();

        if (sl != null) {
            for (FastMap.Entry<Integer, Scheme> e = sl.head(), end = sl.tail(); (e = e.getNext()) != end;) {
                if (e.getValue().getSchemeName().toLowerCase().equals(schemeName.toLowerCase())) {
                    return true;
                }
            }
        }

        return false;
    }

    public Scheme getSchemeById(int schemeId) {
        return _schemeList.get(schemeId);
    }

    /**
     * Override existing scheme.
     *
     * @param scheme
     */
    public void setScheme(Scheme scheme) {
        if (_schemeList.getEntry(scheme.getSchemeId()) != null) {
            if (scheme.getDbStatus() != DBStatus.INSERTED && scheme.getDbStatus() != DBStatus.DELETED) {
                scheme.setDbStatus(DBStatus.UPDATED);
            }

            _schemeList.getEntry(scheme.getSchemeId()).setValue(scheme);
        }
    }

    /**
     * Used only when load from database, method add only schemes data without
     * buffs (buffs loaded in other method).
     *
     * @param schemeId
     * @param schemeName
     * @return
     */
    public boolean addSchemeForDBLoader(int schemeId, String schemeName) {

        if (_schemeList.containsKey(schemeId)) {
            return false;
        }

        Scheme s = new Scheme();

        s.setSchemeId(schemeId);
        s.setSchemeName(schemeName);

        _schemeList.put(s.getSchemeId(), s);

        return true;

    }

    /**
     * Add buff scheme into object model and set DBStatus for INSERTED.
     *
     * @param scheme
     * @return
     */
    public boolean addScheme(Scheme scheme) {
        // I must operate on _schemeList.
        if (scheme != null && !_schemeList.containsKey(scheme.getSchemeId())) {
            scheme.setSchemeId(generateSchemeId());

            scheme.setDbStatus(DBStatus.INSERTED);

            _schemeList.put(scheme.getSchemeId(), scheme);

            return true;
        }

        return false;
    }

    /**
     * Generate new scheme id, there is no problem with removed schemes because
     * removed schemes are still on list till scheduler start works..
     *
     * @return
     */
    private int generateSchemeId() {
        int i = 1;

        while (_schemeList.containsKey(i)) {
            i++;
        }

        return i;
    }

    /**
     * Set scheme status on DELETED.
     *
     * @param schemeId
     * @return
     */
    public boolean removeScheme(int schemeId) {
        // special security not required here.
        if (_schemeList.getEntry(schemeId) != null) {
            _schemeList.get(schemeId).setDbStatus(DBStatus.DELETED);
        }

        return false;
    }

    /**
     * Should be assigned to local variable and iterate over it.
     *
     * @return
     */
    public FastMap<Integer, Scheme> getSchemeList() {
        FastMap<Integer, Scheme> sl = new FastMap<>();

        for (FastMap.Entry<Integer, Scheme> e = _schemeList.head(), end = _schemeList.tail(); (e = e.getNext()) != end;) {
            if (e.getValue().getDbStatus() != DBStatus.DELETED) {
                sl.putEntry(e.getKey(), e.getValue());
            }
        }

        return sl;
    }

    /**
     * Returns _schemeList.
     *
     * @return
     */
    public FastMap<Integer, Scheme> getSchemeListEntry() {
        return _schemeList;
    }

    public void setSchemeList(FastMap<Integer, Scheme> schemeList) {
        _schemeList = schemeList;
    }

    public int getCharObjId() {
        return _charObjId;
    }

    public void setCharObjId(int charObjId) {
        _charObjId = charObjId;
    }

    /**
     * Special security for short time disconnected players. It not removes
     * players data on online check, but in 2nd check.
     */
    public boolean isToRemove() {
        return toRemove;
    }

    /**
     * Special security for short time disconnected players. It not removes
     * players data on online check, but in 2nd check.
     *
     * @param toRemove
     */
    public void setToRemove(boolean toRemove) {
        this.toRemove = toRemove;
    }

}
