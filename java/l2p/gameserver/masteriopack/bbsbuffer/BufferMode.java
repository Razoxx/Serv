package l2p.gameserver.masteriopack.bbsbuffer;

/**
 *
 * @author Masterio
 *
 */
public class BufferMode {

	// basic pages:
    /**
     * Show Main page of BBS Buffer.
     */
    public static final int SHOW_MAINPAGE = 0;
    /**
     * Show buff list, with buff's for player or pet.
     */
    public static final int SHOW_SINGLE_BUFF_LIST = 1;
    /**
     * Show scheme manager in view mode.
     */
    public static final int SHOW_SCHEME_MANAGER_VIEW = 2;
    /**
     * Show scheme manager in edit mode.
     */
    public static final int SHOW_SCHEME_MANAGER_EDIT = 3;

	// scheme manager sub pages:
    /**
     * Show page with form for put scheme name.
     */
    public static final int SCHEME_CREATE_NAME = 4;
    /**
     * Show buff list, with buff's what can be selected for scheme. Only for
     * create scheme mode. <br> Is possible change scheme name.
     */
    public static final int SCHEME_CREATE_BUFF_LIST = 5;
    /**
     * Show buff list, with buff's what can be selected for scheme. Only for
     * edit scheme mode. <br> Is not possible change scheme name.
     */
    public static final int SCHEME_EDIT_BUFF_LIST = 6;

}
