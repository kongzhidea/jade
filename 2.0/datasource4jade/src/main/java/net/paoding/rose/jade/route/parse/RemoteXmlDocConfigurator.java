package net.paoding.rose.jade.route.parse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 实现加载远程服务器的配置文件，并且可以监听文件改变。
 *
 * 加载远程文件后，会保存到本地， 注意文件执行权限
 *
 * @author han.liao
 */
public class RemoteXmlDocConfigurator extends XmlDocConfigurator {

    // 本地配置文件 - Linux
    public static final String FAILLOADING_PATH_LINUX = "/etc/jade-config/";

    // 本地配置文件 - Windows
    public static final String FAILLOADING_PATH_WINDOWS = "C:\\jade-config\\";

    // 标记文件名称
    public static final String MARKUP_FILE = "use-local-config";

    // 配置文件名称
    public static final String CONFIG_FILE = "jade-config.xml";

    // 订阅的唯一名称
    public static final String SUBSCRIBE_NAME = "XmlJadeConfig";

    // 命令参数名称
    protected static final String COMMAND = "#COMMAND";

    /**
     * 创建: RemoteXmlDocXceConfigurator 配置。
     */
    public RemoteXmlDocConfigurator() {
        super(getLoadingURL(), getLoadingFile(), getPriority());
    }

    /**
     * 创建: RemoteXmlDocXceConfigurator 配置。
     *
     * @param url - 配置网络路径
     */
    public RemoteXmlDocConfigurator(URL url) {
        super(url);
    }

    /**
     * 配置: RemoteXmlDocXceConfigurator 对象。
     *
     * @param file - 配置本地路径
     */
    public RemoteXmlDocConfigurator(File file) {
        super(file);
    }

    /**
     * 返回从网络加载的配置路径。
     *
     * @return 从网络加载的配置路径
     */
    private static URL getLoadingURL() {

        // 从环境变量中获取配置信息
        String urlPath = System.getenv("JadeConfigUrl");

        if (urlPath == null) {
            urlPath = "http://www.kk.com/jade-config.xml";
        }

        try {
            return new URL(urlPath);

        } catch (MalformedURLException e) {

            if (logger.isErrorEnabled()) {
                logger.error("Malformed URL [" + urlPath + ']', e);
            }
        }

        return null;
    }

    /**
     * 返回从本地加载的文件路径。
     *
     * @return 从本地加载的文件路径
     */
    private static File getLoadingFile() {

        String filePath;

        if (File.separatorChar == '\\') {

            filePath = FAILLOADING_PATH_WINDOWS + CONFIG_FILE;

        } else {

            filePath = FAILLOADING_PATH_LINUX + CONFIG_FILE;
        }

        return new File(filePath);
    }

    /**
     * 返回优先加载选项: <code>true</code> 优先从网络加载, <code>false</code> 优先从本地加载。
     *
     * @return 优先加载选项
     */
    private static boolean getPriority() {

        // 获得目录标记文件
        String filePath;

        if (File.separatorChar == '\\') {

            filePath = FAILLOADING_PATH_WINDOWS + MARKUP_FILE;

        } else {

            filePath = FAILLOADING_PATH_LINUX + MARKUP_FILE;
        }

        File markup = new File(filePath);

        // 检查目录标记：是否从服务器获取配置信息
        return !markup.exists();
    }
}
