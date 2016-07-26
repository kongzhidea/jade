package net.paoding.rose.jade.util;

public class Consts {
    public static String driverClassName = WebPropertiesUtil.getInstance().getValue("jdbc.driver");
    public static String url = WebPropertiesUtil.getInstance().getValue("jdbc.url");
    public static String username = WebPropertiesUtil.getInstance().getValue("jdbc.username");
    public static String password = WebPropertiesUtil.getInstance().getValue("jdbc.password");

    public static String url_stat = WebPropertiesUtil.getInstance().getValue("jdbc.stat.url");
}
