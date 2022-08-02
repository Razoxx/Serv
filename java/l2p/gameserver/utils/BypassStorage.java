package l2p.gameserver.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BypassStorage {

    private static final Pattern htmlBypass = Pattern.compile("<(?:button|a)[^>]+?action=\"bypass +(?:-h +)?([^\"]+?)\"[^>]*?>", 2);
    private static final Pattern htmlLink = Pattern.compile("<(?:button|a)[^>]+?action=\"link +([^\"]+?)\"[^>]*?>", 2);
    private static final Pattern bbsWrite = Pattern.compile("<(?:button|a)[^>]+?action=\"write +(\\S+) +\\S+ +\\S+ +\\S+ +\\S+ +\\S+\"[^>]*?>", 2);
    private static final Pattern directHtmlBypass = Pattern.compile("^(_mrsl|_diary|_match|manor_menu_select|_olympiad).*", 32);
    private static final Pattern directBbsBypass = Pattern.compile("^(_bbshome|_bbsgetfav|_bbsaddfav|_bbslink|_bbsloc|_bbsclan|_bbsmemo|_maillist|_friendlist).*", 32);

    public static class ValidBypass {

        public String bypass;
        public boolean args;
        public boolean bbs;

        public ValidBypass(String bypass, boolean args, boolean bbs) {
            this.bypass = bypass;
            this.args = args;
            this.bbs = bbs;
        }
    }

    private List<ValidBypass> bypasses = new CopyOnWriteArrayList();

    public void parseHtml(CharSequence html, boolean bbs) {
        clear(bbs);

        Matcher m = htmlBypass.matcher(html);
        while (m.find()) {
            String bypass = m.group(1);

            int i = bypass.indexOf(" $");
            if (i > 0) {
                bypass = bypass.substring(0, i);
            }
            addBypass(new ValidBypass(bypass, i >= 0, bbs));
        }
        if (bbs) {
            m = bbsWrite.matcher(html);
            while (m.find()) {
                String bypass = m.group(1);
                addBypass(new ValidBypass(bypass, true, bbs));
            }
        }
        m = htmlLink.matcher(html);
        while (m.find()) {
            String bypass = m.group(1);
            addBypass(new ValidBypass(bypass, false, bbs));
        }
    }

    public ValidBypass validate(String bypass) {
        ValidBypass ret = null;
        boolean args;
        if (directHtmlBypass.matcher(bypass).matches()) {
            ret = new ValidBypass(bypass, false, false);
        } else if (directBbsBypass.matcher(bypass).matches()) {
            ret = new ValidBypass(bypass, false, true);
        } else {
            args = bypass.indexOf(" ") > 0;
            for (ValidBypass bp : bypasses) {
                if ((bp.bypass.equals(bypass)) || ((args == bp.args) && (bypass.startsWith(bp.bypass + " ")))) {
                    ret = bp;
                    break;
                }
            }
        }
        if (ret != null) {
            clear(ret.bbs);
        }
        return ret;
    }

    private void addBypass(ValidBypass bypass) {
        bypasses.add(bypass);
    }

    private void clear(boolean bbs) {
        for (ValidBypass bp : this.bypasses) {
            if (bp.bbs == bbs) {
                bypasses.remove(bp);
            }
        }
    }
}
