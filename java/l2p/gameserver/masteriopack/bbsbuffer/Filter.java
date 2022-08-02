package l2p.gameserver.masteriopack.bbsbuffer;

/**
 *
 * @author Masterio
 *
 */
public class Filter {

    private boolean _showBuffsForPlayer = true;
    private boolean _showBuffsWithTheSameEffect = false;
    private int _showBuffsFromGroup = 0;

    public boolean isShowBuffsForPlayer() {
        return _showBuffsForPlayer;
    }

    public void setShowBuffsForPlayer(boolean showBuffsForPlayer) {
        _showBuffsForPlayer = showBuffsForPlayer;
    }

    public void setShowBuffsForPlayer(int showBuffsForPlayer) {

        if (showBuffsForPlayer == 1) {
            _showBuffsForPlayer = true;
        } else {
            _showBuffsForPlayer = false;
        }

    }

    public boolean isShowBuffsWithTheSameEffect() {
        return _showBuffsWithTheSameEffect;
    }

    public void setShowBuffsWithTheSameEffect(boolean showBuffsWithTheSameEffect) {
        _showBuffsWithTheSameEffect = showBuffsWithTheSameEffect;
    }

    public void setShowBuffsWithTheSameEffect(int showBuffsWithTheSameEffect) {

        if (showBuffsWithTheSameEffect == 1) {
            _showBuffsWithTheSameEffect = true;
        } else {
            _showBuffsWithTheSameEffect = false;
        }

    }

    public int getShowBuffsFromGroup() {
        return _showBuffsFromGroup;
    }

    public void setShowBuffsFromGroup(int showBuffsFromGroup) {
        _showBuffsFromGroup = showBuffsFromGroup;
    }

}
