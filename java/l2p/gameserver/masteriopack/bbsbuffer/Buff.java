package l2p.gameserver.masteriopack.bbsbuffer;

/**
 * @author Masterio
 *
 */
public class Buff {

    private int _skillId = 0;
    private int _skillLevel = 0;
    private String _skillName = null;
    private String _skillDescription = null;
    private boolean _sameEffectGroup = false;
    private int _groupId = 0;
    private int _bufferLevel = 0;
    private int _durationMinutes = 0;
    private int _itemId;
    private long _itemCount;

    /**
     * @return the _skillId
     */
    public int getSkillId() {
        return _skillId;
    }

    /**
     * @param skillId the _skillId to set
     */
    public void setSkillId(int skillId) {
        _skillId = skillId;
    }

    /**
     * @return the _skillLevel
     */
    public int getSkillLevel() {
        return _skillLevel;
    }

    /**
     * @param skillLevel the _skillLevel to set
     */
    public void setSkillLevel(int skillLevel) {
        _skillLevel = skillLevel;
    }

    /**
     * @return the _skillName
     */
    public String getSkillName() {
        return _skillName;
    }

    /**
     * @param skillName the _skillName to set
     */
    public void setSkillName(String skillName) {
        _skillName = skillName;
    }

    /**
     * @return the _skillDescription
     */
    public String getSkillDescription() {
        return _skillDescription;
    }

    /**
     * @param skillDescription the _skillDescription to set
     */
    public void setSkillDescription(String skillDescription) {
        _skillDescription = skillDescription;
    }

    public int getGroupId() {
        return _groupId;
    }

    public void setGroupId(int groupId) {
        _groupId = groupId;
    }

    /**
     * @return the _bufferLevel
     */
    public int getBufferLevel() {
        return _bufferLevel;
    }

    /**
     * @param bufferLevel the _bufferLevel to set
     */
    public void setBufferLevel(int bufferLevel) {
        _bufferLevel = bufferLevel;
    }

    public int getDurationMinutes() {
        return _durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        _durationMinutes = durationMinutes;
    }
    
    public int getItemId() {
        return _itemId;
    }

    public void setItemId(int itemId) {
        _itemId = itemId;
    }
    
    public long getItemCount() {
        return _itemCount;
    }

    public void setItemCount(long itemCount) {
        _itemCount = itemCount;
    }

    public boolean isSameEffectGroup() {
        return _sameEffectGroup;
    }

    public void setSameEffectGroup(boolean sameEffectGroup) {
        _sameEffectGroup = sameEffectGroup;
    }

}
