package services.community.masteriopack.bbsbuffer;

import l2p.gameserver.Config;
import l2p.gameserver.masteriopack.bbsbuffer.BufferMode;
import l2p.gameserver.masteriopack.bbsbuffer.Scheme;
import l2p.gameserver.masteriopack.bbsbuffer.Filter;
import l2p.gameserver.masteriopack.bbsbuffer.DBStatus;
import l2p.gameserver.masteriopack.bbsbuffer.BBSBuffer;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.ShowBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BBSBufferBBSManager extends Functions implements ScriptFile, ICommunityBoardHandler {

    static final Logger _log = LoggerFactory.getLogger(BBSBufferBBSManager.class);

    @Override
    public String[] getBypassCommands() {
        /*
         * Commands list:
         * 
         * Format: BBSB.<command>:<parameters separated with [,]>
         * 
         * BBSBuffer:
         * 		BBSB.SetMode:(mode_id)								// mode define page like: main page, buff list, scheme list, scheme manager, etc.
         * 		BBSB.SetPage:(page)									// set the page number for list if possible
         * 
         * Use:
         * 		BBSB.Use.Buff:(buff_id)								// target is gathered from Filter()
         * 		BBSB.Use.Scheme:(scheme_id)							// target is gathered from Filter()
         * 		BBSB.Use.SchemeOn:(scheme_id),(scheme_for)			// target player: 1, target pet: 0
         * 
         * Scheme:
         * 		BBSB.Scheme.Init									// prepare clear tempScheme and tempBuffList and go to setSchemeName page.
         * 		BBSB.Scheme.SetName:(name)				
         * 		BBSB.Scheme.AddBuff:(buff_id)	
         *		BBSB.Scheme.RemoveBuff:(buff_id)
         *		BBSB.Scheme.Load:(scheme_id)						// load existing scheme to tempBuffScheme
         *		BBSB.Scheme.Edit:(scheme_id)						// load scheme to temporary scheme variable in _bbsbuffer, and go to scheme buff list. // used for editing scheme.
         *		BBSB.Scheme.Save
         *		BBSB.Scheme.Remove:(scheme_id)
         *
         * Filter:
         * 		BBSB.Filter.SBForPlayer:(value)  					// Show Buffs For Player (1: TRUE, 0: FALSE)
         * 		BBSB.Filter.SBWithTheSameEffect:(value)  			// Show Buffs With The Same Effect (1: TRUE, 0: FALSE)
         * 		BBSB.Filter.SBFromGroup:(value)  					// Show Buffs From Group defined in database
         *  	BBSB.Filter.SBFromGroupForSimpleMode:(value)  		// Show Buffs From Group defined in database, executed from main page
         * 		BBSB.Filter.Reset  									// Reset Filter
         *
         * Other:
         * 		BBSB.SelectAll
         * 		BBSB.DeselectAll
         * 		BBSB.CancelBuffs
         * 		BBSB.RestoreCpHpMp
         * 
         * Special:
         * 		BBSB.Special.Use.Scheme:1							// Use scheme predefined in configuration file on player or summon.
         * 		BBSB.Special.Use.Scheme:2							// Use scheme predefined in configuration file on player or summon.
         * 
         * Rebuff:						
         * 		BBSB.Rebuff.Start:(scheme_id),(scheme_for)			// Activate auto-rebuff for selected scheme, rebuff activate if buff disappear from player buff list.
         * 		BBSB.Rebuff.Stop:(scheme_for)						// Stop auto-rebuff.
         * 															// 1 - scheme_for_player, 0 - scheme_for_pet
         * 
         */

        return new String[]{"BBSB."};
    }

    @Override
    public void onBypassCommand(Player activeChar, String command) {

        activeChar.setSessionVar("BBSB", null);

        // check BBSBuffer instance:
        if (activeChar._bbsbuffer == null) {
            activeChar._bbsbuffer = new BBSBuffer(activeChar);
        }

        // set handler to short named variable:
        BBSBuffer BB = activeChar._bbsbuffer;

        if (command.startsWith("BBSB.SetMode:")) {
            int mode = 0;

            String param = command.split(":", 2)[1].trim();

            if (param != null) {
                try {
                    mode = Integer.parseInt(param);

                    if (mode < 0) {
                        mode = 0;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            BB.setMode(mode);
            BB.reset();
        } else if (command.startsWith("BBSB.SetPage:")) {
            int page = 0;

            String param = command.split(":", 2)[1].trim();

            if (param != null) {
                try {
                    page = Integer.parseInt(param);

                    if (page < 1) {
                        page = 1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            BB.setPage(page);
        } else if (command.startsWith("BBSB.Use.")) {

            String use = command.split(":", 2)[0].split("\\.", 3)[2];
            String[] param = command.split(":", 2)[1].trim().split(",", 2); // get max 2 parameters

            try {
                if (use.equals("Buff")) {
                    BB.useBuff(Integer.parseInt(param[0].trim()), true);
                } else if (use.equals("Scheme")) {
                    BB.useBuffScheme(Integer.parseInt(param[0].trim()), true);
                } else if (use.equals("SchemeOn")) {
                    BB.getFilter().setShowBuffsForPlayer(Integer.parseInt(param[1].trim()));
                    BB.useBuffScheme(Integer.parseInt(param[0].trim()), true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command.startsWith("BBSB.Scheme.")) {
            String[] cmd_split = command.split(":", 2);

            String scheme = cmd_split[0].split("\\.", 3)[2];
            String param = "";

            if (cmd_split.length == 2) {
                param = cmd_split[1].trim();
            }

            try {
                if (scheme.equals("Init")) // initializing scheme creation
                {
                    BB.setTempBuffScheme(new Scheme());
                    BB.getTempBuffScheme().setDbStatus(DBStatus.INSERTED);
                    BB.setFilter(new Filter());

                    //BB.getFilter().setShowBuffsForPlayer(Integer.parseInt(param));
                    BB.setMode(BufferMode.SCHEME_CREATE_NAME);
                } else if (scheme.equals("SetName")) {
                    BB.getTempBuffScheme().trySetSchemeName(BB, param);
                } else if (scheme.equals("AddBuff")) {
                    if (BB.getTempBuffScheme().getBuffList().size() < Config.SCHEME_BUFF_LIMIT) {
                        BB.getTempBuffScheme().addBuff(Integer.parseInt(param));
                    } else {
                        BB.getActiveChar().sendMessage("You can't add more buff's");
                    }
                } else if (scheme.equals("RemoveBuff")) {
                    BB.getTempBuffScheme().removeBuff(Integer.parseInt(param));
                } else if (scheme.equals("Load")) // load existing scheme to tempBuffScheme
                {
                    BB.loadBuffScheme(Integer.parseInt(param));
                    BB.setMode(BufferMode.SCHEME_EDIT_BUFF_LIST);
                } else if (scheme.equals("Edit")) // load existing scheme to tempBuffScheme and go to buff list of this scheme.
                {
                    BB.loadBuffScheme(Integer.parseInt(param));
                    BB.setMode(BufferMode.SCHEME_EDIT_BUFF_LIST);
                } else if (scheme.equals("Save")) {
                    BB.saveBuffScheme();
                } else if (scheme.equals("Remove")) {
                    BB.removeBuffScheme(Integer.parseInt(param));
                    BB.setMode(BufferMode.SHOW_SCHEME_MANAGER_EDIT);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command.startsWith("BBSB.Filter.")) {
            String filter = command.split(":", 2)[0].split("\\.", 3)[2];
            String param = command.split(":", 2)[1].trim();

            try {
                if (filter.equals("SBForPlayer")) {
                    BB.getFilter().setShowBuffsForPlayer(Integer.parseInt(param));
                } else if (filter.equals("SBWithTheSameEffect")) {
                    BB.getFilter().setShowBuffsWithTheSameEffect(Integer.parseInt(param));
                    BB.setPage(1);
                } else if (filter.equals("SBFromGroup")) {
                    BB.getFilter().setShowBuffsFromGroup(Integer.parseInt(param));
                    BB.setPage(1);
                } else if (filter.equals("SBFromGroupForSimpleMode")) {
                    BB.getFilter().setShowBuffsFromGroup(Integer.parseInt(param));
                    BB.setMode(BufferMode.SHOW_SINGLE_BUFF_LIST);
                    BB.setPage(1);
                } else if (filter.equals("Reset")) {
                    BB.setFilter(new Filter());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command.startsWith("BBSB.SelectAll")) {
            BB.selectAllSchemeBuffs();
        } else if (command.startsWith("BBSB.DeselectAll")) {
            BB.deselectAllSchemeBuffs();
        } else if (command.startsWith("BBSB.CancelBuffs")) {
            BB.cancelBuffs();
        } else if (command.startsWith("BBSB.RestoreCpHpMp")) {
            BB.restoreCpHpMp();
        } else if (command.startsWith("BBSB.Special.Use.Scheme")) {

            String param = command.split(":", 2)[1].trim();

            if (param.equals("1")) {
                BB.usePredefinedBuffScheme(1, true);
            } else if (param.equals("2")) {
                BB.usePredefinedBuffScheme(2, true);
            }
        } else if (command.startsWith("BBSB.Rebuff")) {
            if (Config.AUTOREBUFF_ENABLED) {
                String rebuff = command.split(":", 2)[0].split("\\.", 3)[2];

                // scheme_id
                String param1 = command.split(":", 2)[1].trim().split(",", 2)[0];

                if (rebuff.equals("Start")) {

                    String param2 = command.split(":", 2)[1].trim().split(",", 2)[1];

                    try {
                        //if player
                        if (Integer.parseInt(param2) == 1) {
                            BB.setRebuffPlayerSchemeId(Integer.parseInt(param1));
                            BB.startRebuffPlayerTaskSchedule();
                        } //if summon
                        else {
                            BB.setRebuffSummonSchemeId(Integer.parseInt(param1));
                            BB.startRebuffSummonTaskSchedule();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (rebuff.equals("Stop")) {
                    if (Integer.parseInt(param1) == 1) {
                        BB.setRebuffPlayerSchemeId(0);
                        BB.stopRebuffPlayerTaskSchedule();
                    } else {
                        BB.setRebuffSummonSchemeId(0);
                        BB.stopRebuffSummonTaskSchedule();
                    }
                }
            } else {
                System.out.println("You should enable Auto-Rebuff in config file.");
            }
        } else {
            ShowBoard.separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", activeChar);
            return;
        }

        // Send response to player:
        ShowBoard.separateAndSend(BBSBufferHtm.getBody(BB), activeChar);

    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

    @Override
    public void onLoad() {
        if (Config.BBSBUFFER_ENABLED) {
            _log.info("CommunityBoard: BBSBufferBBSManager service loaded.");
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.BBSBUFFER_ENABLED) {
            CommunityBoardManager.getInstance().removeHandler(this);
        }
    }

    @Override
    public void onShutdown() {

    }

}
