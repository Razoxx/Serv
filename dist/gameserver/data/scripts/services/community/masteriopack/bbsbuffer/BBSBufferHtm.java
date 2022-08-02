package services.community.masteriopack.bbsbuffer;

import l2p.gameserver.masteriopack.bbsbuffer.BufferMode;
import l2p.gameserver.masteriopack.bbsbuffer.Scheme;
import l2p.gameserver.masteriopack.bbsbuffer.Buff;
import l2p.gameserver.masteriopack.bbsbuffer.BuffTable;
import l2p.gameserver.masteriopack.bbsbuffer.BBSBuffer;
import javolution.util.FastList;
import javolution.util.FastMap;
import l2p.gameserver.Config;

/**
 *
 * @author Masterio
 *
 */
public class BBSBufferHtm {

    /**
     * Generate document content by selected BB.MODE
     *
     * @param BB
     * @return
     */
    public static String getBody(BBSBuffer BB) {
        switch (BB.getMode()) {
            case BufferMode.SHOW_MAINPAGE:
                return MainPage.getBody(BB);

            case BufferMode.SHOW_SINGLE_BUFF_LIST:
            case BufferMode.SCHEME_CREATE_BUFF_LIST:
            case BufferMode.SCHEME_EDIT_BUFF_LIST:
                return BuffPage.getBody(BB);

            case BufferMode.SCHEME_CREATE_NAME:
            case BufferMode.SHOW_SCHEME_MANAGER_EDIT:
            case BufferMode.SHOW_SCHEME_MANAGER_VIEW:
                return BuffManager.getBody(BB);

            default:
                return null;
        }

    }

    public static class MainPage {

        public static String getBody(BBSBuffer BB) {
            String file = BBSBufferHtmFile.MainPage.getBody(BB.getActiveChar());

            // header is static
            file = prepareInside(BB, file);
            file = prepareFooter(BB, file);

            return file;
        }

        private static String prepareInside(BBSBuffer BB, String file) {

            if (!Config.ALL_IN_ONE_PAGE_ENABLED) {
                file = file.replace("%inside%", BBSBufferHtmFile.MainPage.getInsideDefault(BB.getActiveChar()));
            } else {
                file = prepareInsideAllInOne(BB, file);
            }

            return file;
        }

        private static String prepareInsideAllInOne(BBSBuffer BB, String file) {
            String inside = BBSBufferHtmFile.MainPage.getInsideAllInOne(BB.getActiveChar());

            // prepare scheme list:
            String list = "";

            FastMap<Integer, Scheme> sl = BB.getCharacter().getSchemeList();

            for (FastMap.Entry<Integer, Scheme> e = sl.head(), end = sl.tail(); (e = e.getNext()) != end;) {
                Scheme scheme = e.getValue();

                // prepare scheme field:
                String list_item = "";

                if (Config.AUTOREBUFF_ENABLED && Config.ALL_IN_ONE_PAGE_ENABLED) {
                    list_item = BBSBufferHtmFile.BuffManager.getAutoRebuffListItemMainPage(BB.getActiveChar());
                } else {
                    list_item = BBSBufferHtmFile.BuffManager.getListItemMainPage(BB.getActiveChar());
                }

                list_item = list_item.replace("%scheme_name%", scheme.getSchemeName());
                list_item = list_item.replace("%scheme_id%", "" + scheme.getSchemeId());

                // auto-rebuff
                if (Config.AUTOREBUFF_ENABLED && BB.getMode() == BufferMode.SHOW_MAINPAGE) {
                    if (BB.getFilter().isShowBuffsForPlayer() && e.getValue().getSchemeId() != BB.getRebuffPlayerSchemeId()) {
                        list_item = list_item.replace("%button_rebuff%", "<button value=\"A\" action=\"bypass BBSB.Rebuff.Start:" + scheme.getSchemeId() + ",1\" width=20 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    } else if (BB.getFilter().isShowBuffsForPlayer() && e.getValue().getSchemeId() == BB.getRebuffPlayerSchemeId()) {
                        list_item = list_item.replace("%button_rebuff%", "<button value=\"-\" action=\"bypass BBSB.Rebuff.Stop:1\" width=20 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    } else if (!BB.getFilter().isShowBuffsForPlayer() && e.getValue().getSchemeId() != BB.getRebuffSummonSchemeId()) {
                        list_item = list_item.replace("%button_rebuff%", "<button value=\"A\" action=\"bypass BBSB.Rebuff.Start:" + scheme.getSchemeId() + ",0\" width=20 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    } else if (!BB.getFilter().isShowBuffsForPlayer() && e.getValue().getSchemeId() == BB.getRebuffSummonSchemeId()) {
                        list_item = list_item.replace("%button_rebuff%", "<button value=\"-\" action=\"bypass BBSB.Rebuff.Stop:0\" width=20 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    }
                }

                list += list_item;

            }

            if (list.equals("")) {
                list = "&nbsp;";
            }

            inside = inside.replace("%scheme_list%", list);

            String schame_for = "";
            if (BB.getFilter().isShowBuffsForPlayer()) {
                if (BB.getActiveChar().isLangRus()) {
                    schame_for = "<button value=\"Использовать на питомца\" action=\"bypass BBSB.Filter.SBForPlayer:0\" width=160 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">";
                } else {
                    schame_for = "<button value=\"Use for pet\" action=\"bypass BBSB.Filter.SBForPlayer:0\" width=160 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">";
                }

            } else {
                if (BB.getActiveChar().isLangRus()) {
                    schame_for = "<button value=\"Использовать на меня\" action=\"bypass BBSB.Filter.SBForPlayer:1\" width=160 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">";
                } else {
                    schame_for = "<button value=\"Use for me\" action=\"bypass BBSB.Filter.SBForPlayer:1\" width=160 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">";
                }

            }

            inside = inside.replace("%scheme_for%", schame_for);

            file = file.replace("%inside%", inside);

            return file;
        }

        private static String prepareFooter(BBSBuffer BB, String file) {
            if (Config.ALL_IN_ONE_PAGE_ENABLED) {
                if (BB.getActiveChar().isLangRus()) {
                    file = file.replace("%button_right%", "<button value=\"Редактор схем\" action=\"bypass BBSB.SetMode:3\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                } else {
                    file = file.replace("%button_right%", "<button value=\"Schema editor\" action=\"bypass BBSB.SetMode:3\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                }

            } else {
                file = file.replace("%button_right%", "&nbsp;");
            }

            return file;
        }

    }

    public static class BuffManager {

        public static String getBody(BBSBuffer BB) {
            String file = "";

            if (BB.getMode() == BufferMode.SCHEME_CREATE_NAME) {
                file = BBSBufferHtmFile.BuffManager.getBodySchemeName(BB.getActiveChar());

                file = prepareHeader(BB, file);
                file = prepareBodySchemeName(BB, file);
            } else {
                file = BBSBufferHtmFile.BuffManager.getBody(BB.getActiveChar());

                file = prepareHeader(BB, file);
                file = prepareInside(BB, file);
                file = prepareFooter(BB, file);
            }

            return file;
        }

        private static String prepareHeader(BBSBuffer BB, String file) {
            file = file.replace("%header%", "&nbsp;");

            return file;
        }

        private static String prepareBodySchemeName(BBSBuffer BB, String file) {
            file = file.replace("%error%", BB.getErrorMessage());

            return file;
        }

        private static String prepareInside(BBSBuffer BB, String file) {
            // prepare inside:
            String inside = BBSBufferHtmFile.BuffManager.getInside(BB.getActiveChar());

            // prepare scheme list:
            String list = "";

            FastMap<Integer, Scheme> sl = BB.getCharacter().getSchemeList();

            for (FastMap.Entry<Integer, Scheme> e = sl.head(), end = sl.tail(); (e = e.getNext()) != end;) {
                Scheme scheme = e.getValue();

                // prepare skill field:
                String list_item = "";

                if (Config.AUTOREBUFF_ENABLED && BB.getMode() == BufferMode.SHOW_SCHEME_MANAGER_VIEW) {
                    list_item = BBSBufferHtmFile.BuffManager.getAutoRebuffListItem(BB.getActiveChar());
                } else {
                    list_item = BBSBufferHtmFile.BuffManager.getListItem(BB.getActiveChar());
                }

                list_item = list_item.replace("%scheme_name%", scheme.getSchemeName());
                list_item = list_item.replace("%scheme_buff_count%", "" + scheme.getBuffList().size());
                //list_item = list_item.replace("%scheme_price%", "Price: "+scheme.getPrice()); new feature will be added soon.
                list_item = list_item.replace("%scheme_price%", "&nbsp;");

                if (BB.getMode() == BufferMode.SHOW_SCHEME_MANAGER_EDIT) {
                    if (BB.getActiveChar().isLangRus()) {
                        list_item = list_item.replace("%button_left%", "<button value=\"Удалить\" action=\"bypass BBSB.Scheme.Remove:" + scheme.getSchemeId() + "\" width=100 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                        list_item = list_item.replace("%button_right%", "<button value=\"Редакт.\" action=\"bypass BBSB.Scheme.Edit:" + scheme.getSchemeId() + "\" width=100 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    } else {
                        list_item = list_item.replace("%button_left%", "<button value=\"Remove\" action=\"bypass BBSB.Scheme.Remove:" + scheme.getSchemeId() + "\" width=100 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                        list_item = list_item.replace("%button_right%", "<button value=\"Edit\" action=\"bypass BBSB.Scheme.Edit:" + scheme.getSchemeId() + "\" width=100 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    }
                } else // show_scheme_manager_view
                {
                    list_item = list_item.replace("%button_left%", "<button value=\"Use on Pet\" action=\"bypass BBSB.Use.SchemeOn:" + scheme.getSchemeId() + ",0\" width=100 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    list_item = list_item.replace("%button_right%", "<button value=\"Use on Self\" action=\"bypass BBSB.Use.SchemeOn:" + scheme.getSchemeId() + ",1\" width=100 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                }

                // auto-rebuff
                if (Config.AUTOREBUFF_ENABLED && BB.getMode() == BufferMode.SHOW_SCHEME_MANAGER_VIEW) {
                    if (e.getValue().getSchemeId() != BB.getRebuffPlayerSchemeId()) {
                        list_item = list_item.replace("%button_rebuff_player%", "<button value=\"A\" action=\"bypass BBSB.Rebuff.Start:" + scheme.getSchemeId() + ",1\" width=20 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    } else if (e.getValue().getSchemeId() == BB.getRebuffPlayerSchemeId()) {
                        list_item = list_item.replace("%button_rebuff_player%", "<button value=\"-\" action=\"bypass BBSB.Rebuff.Stop:1\" width=20 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    }

                    if (e.getValue().getSchemeId() != BB.getRebuffSummonSchemeId()) {
                        list_item = list_item.replace("%button_rebuff_pet%", "<button value=\"A\" action=\"bypass BBSB.Rebuff.Start:" + scheme.getSchemeId() + ",0\" width=20 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    } else if (e.getValue().getSchemeId() == BB.getRebuffSummonSchemeId()) {
                        list_item = list_item.replace("%button_rebuff_pet%", "<button value=\"-\" action=\"bypass BBSB.Rebuff.Stop:0\" width=20 height=30 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    }
                }

                list += list_item;

            }

            if (list.equals("")) {
                list = "No scheme's defined yet";
            }

            inside = inside.replace("%list%", list);

            inside = inside.replace("%scheme_counter%", BB.getCharacter().getSchemeList().size() + "/" + Config.SCHEME_LIMIT);

            file = file.replace("%inside%", inside);

            return file;
        }

        private static String prepareFooter(BBSBuffer BB, String file) {

            // scheme list:
            if (BB.getMode() == BufferMode.SHOW_SCHEME_MANAGER_VIEW) {
                file = file.replace("%button_left%", "<button value=\"Назад\" action=\"bypass BBSB.SetMode:0\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");

                if (BB.getCharacter().getSchemeList().size() == 0) {
                    file = file.replace("%button_right%", "<button value=\"Новая схема\" action=\"bypass BBSB.Scheme.Init\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                } else {
                    file = file.replace("%button_right%", "<button value=\"Редактор\" action=\"bypass BBSB.SetMode:3\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                }

                file = file.replace("%footer_label%", "Схемы игрока");
            } // scheme list editor:
            else if (BB.getMode() == BufferMode.SHOW_SCHEME_MANAGER_EDIT) {
                if (Config.ALL_IN_ONE_PAGE_ENABLED) {
                    file = file.replace("%button_left%", "<button value=\"Назад\" action=\"bypass BBSB.SetMode:0\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                } else {
                    file = file.replace("%button_left%", "<button value=\"Назад\" action=\"bypass BBSB.SetMode:2\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                }

                if (BB.getCharacter().getSchemeList().size() < Config.SCHEME_LIMIT) {
                    file = file.replace("%button_right%", "<button value=\"Новая схема\" action=\"bypass BBSB.Scheme.Init\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                } else {
                    file = file.replace("%button_right%", "&nbsp;");
                }

                file = file.replace("%footer_label%", "Редактор схем");
            } // scheme create buff list:
            else if (BB.getMode() == BufferMode.SCHEME_CREATE_BUFF_LIST) {
                file = file.replace("%button_left%", "<button value=\"Назад\" action=\"bypass BBSB.SetMode:4\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                file = file.replace("%button_right%", "<button value=\"Сохранить\" action=\"bypass BBSB.Scheme.Save\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");

                file = file.replace("%footer_label%", "Выберите бафы для схемы: " + BB.getTempBuffScheme().getSchemeName());
            } // scheme edit buff list:
            else if (BB.getMode() == BufferMode.SCHEME_EDIT_BUFF_LIST) {
                file = file.replace("%button_left%", "<button value=\"Назад\" action=\"bypass BBSB.SetMode:3\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                file = file.replace("%button_right%", "<button value=\"Сохранить\" action=\"bypass BBSB.Scheme.Save\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");

                file = file.replace("%footer_label%", "Выберите бафы для схемы: " + BB.getTempBuffScheme().getSchemeName());
            }

            return file;
        }

    }

    public static class BuffPage {

        public static String getBody(BBSBuffer BB) {
            String file = BBSBufferHtmFile.BuffPage.getBody(BB.getActiveChar());

            file = prepareHeader(BB, file);
            file = prepareInside(BB, file);
            file = prepareFooter(BB, file);

            return file;
        }

        private static String prepareHeader(BBSBuffer BB, String file) {
            if (Config.BUFF_GROUPS_ENABLED && Config.BUFF_GROUPS_MENU_TYPE == 1) {
                String menu = BBSBufferHtmFile.BuffPage.getListGroupMenu(BB.getActiveChar());

                // prepare selected/unselected items in groups menu:
                FastList<Integer> list = BuffTable.getInstance().getBuffGroupList();
                for (FastList.Node<Integer> n = list.head(), end = list.tail(); (n = n.getNext()) != end;) {
                    if (BB.getFilter().getShowBuffsFromGroup() == n.getValue()) {
                        menu = menu.replace("%H_" + n.getValue() + "%", "" + BBSBufferStyle.H_SELECTED);
                    } else {
                        menu = menu.replace("%H_" + n.getValue() + "%", "" + BBSBufferStyle.H_UNSELECTED);
                    }
                }

                file = file.replace("%header%", menu);
            } else {
                file = file.replace("%header%", "&nbsp;");
            }

            return file;
        }

        private static String prepareInside(BBSBuffer BB, String file) {
            // prepare buff list:
            String list_left = "";
            String list_right = "";
            String list_right_2 = "";

            FastMap<Integer, Buff> bl = BuffTable.getInstance().getBuffList(BB);

            int i = 0;
            for (FastMap.Entry<Integer, Buff> e = bl.head(), end = bl.tail(); (e = e.getNext()) != end;) {
                Buff buff = e.getValue();

                // prepare skill field:
                String list_item = "";

                if (BB.getMode() == BufferMode.SHOW_SINGLE_BUFF_LIST) {
                    if (Config.BUFF_LIST_WITH_3_COLUMNS) {
                        list_item = BBSBufferHtmFile.BuffPage.getListItemSelectedSmall(BB.getActiveChar());
                    } else {
                        list_item = BBSBufferHtmFile.BuffPage.getListItemSelected(BB.getActiveChar());
                    }

                    list_item = list_item.replace("%button%", "<button value=\"\" action=\"bypass BBSB.Use.Buff:" + buff.getSkillId() + "\"  width=32 height=32 back=\"000000\" fore=" + getSkillIcon(buff.getSkillId()) + ">");
                } else if (BB.getTempBuffScheme().getBuffList().contains(buff.getSkillId())) // if scheme edit buff list
                {
                    if (Config.BUFF_LIST_WITH_3_COLUMNS) {
                        list_item = BBSBufferHtmFile.BuffPage.getListItemSelectedSmall(BB.getActiveChar());
                    } else {
                        list_item = BBSBufferHtmFile.BuffPage.getListItemSelected(BB.getActiveChar());
                    }

                    list_item = list_item.replace("%button%", "<button value=\"\" action=\"bypass BBSB.Scheme.RemoveBuff:" + buff.getSkillId() + "\"  width=32 height=32 back=\"000000\" fore=" + getSkillIcon(buff.getSkillId()) + ">");
                } else {
                    if (Config.BUFF_LIST_WITH_3_COLUMNS) {
                        list_item = BBSBufferHtmFile.BuffPage.getListItemUnselectedSmall(BB.getActiveChar());
                    } else {
                        list_item = BBSBufferHtmFile.BuffPage.getListItemUnselected(BB.getActiveChar());
                    }

                    list_item = list_item.replace("%button%", "<button value=\"\" action=\"bypass BBSB.Scheme.AddBuff:" + buff.getSkillId() + "\"  width=32 height=32 back=\"000000\" fore=" + getSkillIcon(buff.getSkillId()) + ">");
                }

                list_item = list_item.replace("%skill_name%", buff.getSkillName());
                list_item = list_item.replace("%skill_info%", buff.getSkillDescription());

                if (i < 10) {
                    list_left += list_item;
                } else if (i >= 10 && i < 20) {
                    list_right += list_item;
                } else {
                    list_right_2 += list_item;
                }

                i++;
            }

            // prepare inside:
            String inside = BBSBufferHtmFile.BuffPage.getInside(BB.getActiveChar());

            if (Config.BUFF_LIST_WITH_3_COLUMNS) {
                inside = inside.replace("%buff_table%", BBSBufferHtmFile.BuffPage.getInsideBuffTableSmall(BB.getActiveChar()));
            } else {
                inside = inside.replace("%buff_table%", BBSBufferHtmFile.BuffPage.getInsideBuffTable(BB.getActiveChar()));
            }

            if (list_left.equals("")) {
                list_left = "&nbsp;";
            }

            if (list_right.equals("")) {
                list_right = "&nbsp;";
            }

            if (list_right_2.equals("")) {
                list_right_2 = "&nbsp;";
            }

            inside = inside.replace("%list_left%", list_left);
            inside = inside.replace("%list_right%", list_right);
            if (Config.BUFF_LIST_WITH_3_COLUMNS) {
                inside = inside.replace("%list_right_2%", list_right_2);
            }

            // prepare buff counter:
            if (BB.getMode() == BufferMode.SCHEME_CREATE_BUFF_LIST || BB.getMode() == BufferMode.SCHEME_EDIT_BUFF_LIST) {
                inside = inside.replace("%buff_counter%", "Buff's: " + BB.getTempBuffScheme().getBuffList().size() + "/" + Config.SCHEME_BUFF_LIMIT);
            } else {
                inside = inside.replace("%buff_counter%", "&nbsp;");
            }

            // prepare page changer:
            inside = preparePageChanger(BB, inside);

            // prepare selector's:
            inside = prepareSelector(BB, inside);

            // prepare buff with same effect button:
            String group_button;

            if (BB.getFilter().isShowBuffsWithTheSameEffect()) {
                group_button = "<button value=\"Group\" action=\"bypass BBSB.Filter.SBWithTheSameEffect:0\" width=80 height=20 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">";
            } else {
                group_button = "<button value=\"Ungroup\" action=\"bypass BBSB.Filter.SBWithTheSameEffect:1\" width=80 height=20 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">";
            }

            inside = inside.replace("%group_button%", group_button);

            file = file.replace("%inside%", inside);

            return file;
        }

        private static String getSkillIcon(int skillId) {
            String icon = "Icon.skill";

            // if summoners skills (exception) for icons only.
            if (skillId == 4699 || skillId == 4700) {
                icon += 1331;
            } else if (skillId == 4702 || skillId == 4703) {
                icon += 1332;
            } else if (skillId < 100) {
                icon += "00" + skillId;
            } else if (skillId < 1000) {
                icon += "0" + skillId;
            } else {
                icon += "" + skillId;
            }

            return icon;
        }

        private static String preparePageChanger(BBSBuffer BB, String inside) {

            // page_changer_left:
            if (BB.getPage() > 1) {
                inside = inside.replace("%page_changer_left%", "<button value=\"back\" action=\"bypass BBSB.SetPage:" + (BB.getPage() - 1) + "\"  width=30 height=20 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
            } else {
                inside = inside.replace("%page_changer_left%", "&nbsp;");
            }

            // page_changer_center:
            inside = inside.replace("%page_changer_center%", BB.getPage() + "/" + BB.getPageCount());

            // page_changer_right:
            if (BB.getPage() < BB.getPageCount() && BB.getPageCount() > 1) {
                inside = inside.replace("%page_changer_right%", "<button value=\"next\" action=\"bypass BBSB.SetPage:" + (BB.getPage() + 1) + "\"  width=30 height=20 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
            } else {
                inside = inside.replace("%page_changer_right%", "&nbsp;");
            }

            return inside;
        }

        private static String prepareSelector(BBSBuffer BB, String inside) {

            if (BB.getMode() == BufferMode.SCHEME_CREATE_BUFF_LIST || BB.getMode() == BufferMode.SCHEME_EDIT_BUFF_LIST) {
                inside = inside.replace("%selector_1%", "<button value=\"++\" action=\"bypass BBSB.SelectAll\" width=30 height=20 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                inside = inside.replace("%selector_2%", "<button value=\"--\" action=\"bypass BBSB.DeselectAll\" width=30 height=20 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
            } else {
                inside = inside.replace("%selector_1%", "&nbsp;");
                inside = inside.replace("%selector_2%", "&nbsp;");
            }

            return inside;
        }

        private static String prepareFooter(BBSBuffer BB, String file) {

            // single buff list:
            if (BB.getMode() == BufferMode.SHOW_SINGLE_BUFF_LIST) {
                file = file.replace("%button_left%", "<button value=\"Назад\" action=\"bypass BBSB.SetMode:0\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");

                if (!BB.getFilter().isShowBuffsForPlayer()) {
                    file = file.replace("%button_right%", "<button value=\"Показать бафы игрока\" action=\"bypass BBSB.Filter.SBForPlayer:1\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    file = file.replace("%footer_label%", "Баф на пета");
                } else {
                    file = file.replace("%button_right%", "<button value=\"Показать бафы питомца\" action=\"bypass BBSB.Filter.SBForPlayer:0\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                    file = file.replace("%footer_label%", "Баф на чара");
                }

            }

            // scheme create buff list:
            if (BB.getMode() == BufferMode.SCHEME_CREATE_BUFF_LIST) {
                file = file.replace("%button_left%", "<button value=\"Назад\" action=\"bypass BBSB.SetMode:4\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                file = file.replace("%button_right%", "<button value=\"Сохранить\" action=\"bypass BBSB.Scheme.Save\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");

                file = file.replace("%footer_label%", "Выберите бафы для схемы: " + BB.getTempBuffScheme().getSchemeName());
            }

            // scheme edit buff list:
            if (BB.getMode() == BufferMode.SCHEME_EDIT_BUFF_LIST) {
                file = file.replace("%button_left%", "<button value=\"Назад\" action=\"bypass BBSB.SetMode:3\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");
                file = file.replace("%button_right%", "<button value=\"Сохранить\" action=\"bypass BBSB.Scheme.Save\" width=128 height=26 back=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_BACK + " fore=" + BBSBufferStyle.PAGE_CHANGE_BUTTON_FORE + ">");

                file = file.replace("%footer_label%", "Выберите бафы для схемы: " + BB.getTempBuffScheme().getSchemeName());
            }

            return file;
        }

    }

    public static class Help {

        public static String getBody(BBSBuffer BB) {

            return null;
        }
    }

}
