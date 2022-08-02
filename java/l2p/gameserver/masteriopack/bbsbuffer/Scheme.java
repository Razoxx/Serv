package l2p.gameserver.masteriopack.bbsbuffer;

import l2p.gameserver.masteriopack.bbsbuffer.DBStatus;
import l2p.gameserver.masteriopack.bbsbuffer.BBSBuffer;
import javolution.util.FastList;

/**
 * @author Masterio
 *
 */
public class Scheme {

    private int _schemeId = 0;
    private String _schemeName = null;

    /**
     * [buffId]
     */
    private FastList<Integer> _buffList = new FastList<>();

    private int _dbStatus = DBStatus.NONE;

    /**
     * @return the _schemeId
     */
    public int getSchemeId() {
        return _schemeId;
    }

    /**
     * @param _schemeId the _schemeId to set
     */
    public void setSchemeId(int schemeId) {
        _schemeId = schemeId;
    }

    /**
     * @return the _schemeName
     */
    public String getSchemeName() {
        return _schemeName;
    }

    /**
     * @param _schemeName the _schemeName to set
     */
    public void setSchemeName(String schemeName) {
        _schemeName = schemeName;
    }

    /**
     * Returns true if no errors. If some error founded, then BB._errorMessage
     * is edited.
     *
     * @param BB
     * @param schemeName the _schemeName to set
     * @return
     */
    public boolean trySetSchemeName(BBSBuffer BB, String schemeName) {
        if (schemeName == null || schemeName.length() == 0) {
            BB.setErrorMessage("Value incorrect, try again");
        } else if (schemeName.length() > 14) {
            BB.setErrorMessage("Scheme name too long (max 14 characters)");
        } else if (BB.getCharacter().isSchemeNameExists(BB.getActiveChar().getObjectId(), schemeName)) {
            BB.setErrorMessage("Scheme name is used");
        } else {
            setSchemeName(schemeName);

            BB.setErrorMessage("&nbsp;");
            BB.setMode(BufferMode.SCHEME_CREATE_BUFF_LIST);
            BB.setPage(1);

            return true;
        }
        return false;
    }

    /**
     * @return the _buffList
     */
    public FastList<Integer> getBuffList() {
        return _buffList;
    }

    /**
     * @param _buffList the _buffList to set
     */
    public void setBuffList(FastList<Integer> buffList) {
        _buffList = buffList;
    }

    public boolean addBuff(int skillId) {

        if (!_buffList.contains(skillId)) {
            _buffList.add(skillId);
            return true;
        }

        return false;
    }

    public boolean removeBuff(int skillId) {
        int idx = _buffList.indexOf(skillId);

        if (idx >= 0) {
            _buffList.remove(idx);
            return true;
        }

        return false;
    }

    /**
     * @return the _dbStatus
     */
    public int getDbStatus() {
        return _dbStatus;
    }

    /**
     * @param _dbStatus the _dbStatus to set
     */
    public void setDbStatus(int dbStatus) {
        _dbStatus = dbStatus;
    }

    public Scheme getClone() {

        Scheme bs = new Scheme();

        bs.setSchemeId(getSchemeId());
        bs.setSchemeName(getSchemeName());
        bs.setDbStatus(getDbStatus());

        for (FastList.Node<Integer> n = getBuffList().head(), end = getBuffList().tail(); (n = n.getNext()) != end;) {
            bs.addBuff(n.getValue());
        }

        return bs;
    }

}
