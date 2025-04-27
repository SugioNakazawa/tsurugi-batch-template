package jp.gr.java_conf.nkzw.tbt.tools.common.util;

public class TgStringUtil {

    /**
     * 文字列をキャメルケースに変換する
     *
     * @param org 変換前の文字列
     * @return キャメルケースに変換された文字列
     */
    static public String toCamelCase(String org) {
        return toCamelCase(org, false);
    }

    static public String toCamelCaseTopUpper(String org) {
        return toCamelCase(org, true);
    }

    static public String toCamelCase(String org, boolean isUpper) {
        String[] sp = org.split("_");
        // if (sp.length < 2)
        //     return org;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sp.length; i++) {
            if (isUpper || i != 0) {
                sb.append(
                        sp[i].substring(0, 1).toUpperCase());
            } else {
                sb.append(
                        sp[i].substring(0, 1).toLowerCase());
            }
            sb.append(
                    sp[i].substring(1).toLowerCase());
        }
        return sb.toString();
    }

}
