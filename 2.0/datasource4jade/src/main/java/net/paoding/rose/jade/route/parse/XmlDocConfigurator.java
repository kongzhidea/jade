package net.paoding.rose.jade.route.parse;

import net.paoding.rose.jade.route.RoutingDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 从: XML 文件中读取散表属性的配置器实现。
 *
 * @author han.liao
 */
public class XmlDocConfigurator implements RoutingConfigurator {

    // 连接配置服务器的超时
    public static final int CONNECT_TIMEOUT = 10000;

    // 读取配置服务器的超时
    public static final int READ_TIMEOUT = 10000;

    // 输出日志
    protected static final Log logger = LogFactory.getLog(XmlDocConfigurator.class);


    // 解析的配置项
    protected ConcurrentHashMap<String, RoutingDescriptor> map = new ConcurrentHashMap<String, RoutingDescriptor>();

    // 加锁保护配置信息
    protected ReadWriteLock rwLock = new ReentrantReadWriteLock();

    // 配置文件路径
    protected File file;

    // 配置文件路径
    protected URL url;

    // 是否成功加载
    protected boolean inited;

    // 配置文件名称
    private static final String JADE_CONFIG_XML = "jade-config.xml";

    /**
     * 配置: XmlDocXceConfigurator 对象。
     */
    public XmlDocConfigurator() {

        this(getDefault());
    }

    /**
     * 配置: XmlDocXceConfigurator 对象。
     *
     * @param file - 配置文件
     */
    public XmlDocConfigurator(File file) {

        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Loading config from: " + file.getPath());
        }

        this.file = file;

        inited = loadXmlDoc(file);
    }

    /**
     * 配置: XmlDocXceConfigurator 对象。
     *
     * @param url - 配置网络路径
     */
    public XmlDocConfigurator(URL url) {

        if (url == null) {
            throw new IllegalArgumentException("url is null");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Loading config from: " + url);
        }

        this.url = url;

        inited = loadXmlDoc(url);
    }

    /**
     * 配置: XmlDocXceConfigurator 对象。
     *
     * @param url  - 配置网络路径
     * @param file - 如果从网络路径读取失败, 加载的默认路径, 可以是: <code>null</code>.
     */
    public XmlDocConfigurator(URL url, File file, boolean priority) {

        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }

        this.url = url;
        this.file = file;

        inited = loadXmlDoc(url, file, priority);
    }

    /**
     * 返回配置文件。
     *
     * @return 配置文件
     */
    public File getFile() {
        return file;
    }

    /**
     * 设置配置文件。
     *
     * @param file - 配置文件
     */
    public void setFile(File file) {

        // 加载配置文件。
        if (!loadXmlDoc(file)) {
            throw new IllegalArgumentException("Can't load xml: " + file.getPath());
        }

        this.url = null;
        this.file = file;

        inited = true;
    }

    /**
     * 设置配置文件的路径。
     *
     * @param filePath - 配置文件的路径
     */
    public void setFile(String filePath) {

        File file = new File(filePath);

        // 加载配置文件。
        if (!loadXmlDoc(file)) {
            throw new IllegalArgumentException("Can't load xml: " + filePath);
        }

        this.url = null;
        this.file = file;

        inited = true;
    }

    /**
     * 返回配置网络路径。
     *
     * @return - 配置网络路径
     */
    public URL getURL() {
        return url;
    }

    /**
     * 设置配置网络路径。
     *
     * @param url - 配置网络路径
     */
    public void setURL(URL url) {

        // 加载配置文件。
        if (!loadXmlDoc(url)) {
            throw new IllegalArgumentException("Can't load xml: " + url);
        }

        this.url = url;
        this.file = null;

        inited = true;
    }

    /**
     * 设置配置网络路径。
     *
     * @param urlPath - 配置网络路径
     */
    public void setURL(String urlPath) {

        try {
            URL url = new URL(urlPath);

            // 加载配置文件。
            if (!loadXmlDoc(new URL(urlPath))) {
                throw new IllegalArgumentException("Can't load xml: " + urlPath);
            }

            this.file = null;
            this.url = url;

            inited = true;

        } catch (MalformedURLException e) {

            throw new IllegalArgumentException("Malformed url: " + urlPath, e);
        }
    }

    @Override
    public RoutingDescriptor getDescriptor(String catalog, String name) {

        if (!inited) {
            throw new IllegalStateException("XceConfigurator is not initialized.");
        }

        String keyword = catalog + '.' + name;

        // 加锁保护配置信息的完整性
        Lock lock = rwLock.readLock();

        try {
            lock.lock();

            RoutingDescriptor descriptor = map.get(keyword);

            if (descriptor == null) {

                descriptor = map.get(catalog); // 获取全局设置
            }

            return descriptor;

        } finally {

            lock.unlock();
        }
    }

    /**
     * 从指定的网络路径, 本地文件加载配置。
     *
     * @param url      - 指定的网络路径
     * @param file     - 指定的本地文件
     * @param priority - <code>true</code> 优先从网络加载, <code>false</code>
     *                 优先从本地加载
     * @return 加载成功返回 <code>true</code>, 否则返回 <code>false</code>.
     */
    protected boolean loadXmlDoc(URL url, File file, boolean priority) {

        if (url == null) {

            if (logger.isInfoEnabled()) {
                logger.info("Loading config from: " + file.getPath());
            }

            if (file != null) {

                return loadXmlDoc(file);
            }

        } else if (priority) {

            if (logger.isInfoEnabled()) {
                logger.info("Loading config from: " + url);
            }

            if (loadXmlDoc(url)) {

                if (logger.isInfoEnabled()) {
                    logger.info("Saving config to: " + file.getPath());
                }

                // 保存配置信息到本地
                saveToFile(url, file);

                return true;

            } else if (file != null) {

                if (logger.isInfoEnabled()) {
                    logger.info("Loading config from: " + file.getPath());
                }

                return loadXmlDoc(file);
            }

        } else if (file != null) {

            if (logger.isInfoEnabled()) {
                logger.info("Loading config from: " + file.getPath());
            }

            if (loadXmlDoc(file)) {

                return true;

            } else if (url != null) {

                if (logger.isInfoEnabled()) {
                    logger.info("Loading config from: " + url);
                }

                if (loadXmlDoc(url)) {

                    if (logger.isInfoEnabled()) {
                        logger.info("Saving config to: " + file.getPath());
                    }

                    // 保存配置信息到本地
                    saveToFile(url, file);
                }
            }
        }

        return false;
    }

    /**
     * 从指定的网络路径读取配置。
     *
     * @param url - 指定的网络路径
     * @return 加载成功返回 <code>true</code>, 否则返回 <code>false</code>.
     */
    private boolean loadXmlDoc(URL url) {

        Lock lock = null;

        try {
            // 打开  SAX DocumentBuilder
            DocumentBuilder db = createDocumentBuilder();

            // 打开网络连接
            URLConnection connect = url.openConnection();

            // 设置连接超时
            connect.setConnectTimeout(CONNECT_TIMEOUT);

            // 设置读取超时
            connect.setReadTimeout(READ_TIMEOUT);

            // 打开文件流
            Document doc = db.parse(new InputSource(connect.getInputStream()));

            // 加锁保护配置信息的完整性
            lock = rwLock.writeLock();

            lock.lock();

            // 清除配置信息
            map.clear();

            // 加载配置信息
            RoutingDescriptorLoader.loadXMLDoc(map, doc);

            return true;

        } catch (SAXException e) {

            // 输出日志
            if (logger.isWarnEnabled()) {
                logger.warn("Can't parse [" + url + ']', e);
            }

        } catch (IOException e) {

            // 输出日志
            if (logger.isWarnEnabled()) {
                logger.warn("Can't load [" + url + ']', e);
            }

        } finally {

            if (lock != null) {
                lock.unlock();
            }
        }

        return false;
    }

    /**
     * 从指定的文件读取配置。
     *
     * @param file - 指定的文件
     * @return 加载成功返回 <code>true</code>, 否则返回 <code>false</code>.
     */
    private boolean loadXmlDoc(File file) {

        if (!file.exists()) {

            return false; // 指定的文件不存在
        }

        Lock lock = null;

        try {
            // 打开  SAX DocumentBuilder
            DocumentBuilder db = createDocumentBuilder();

            // 打开文件流
            Document doc = db.parse(file);

            // 加锁保护配置信息的完整性
            lock = rwLock.writeLock();

            lock.lock();

            // 清除配置信息
            map.clear();

            // 加载配置信息
            RoutingDescriptorLoader.loadXMLDoc(map, doc);

            return true;

        } catch (SAXException e) {

            // 输出日志
            if (logger.isWarnEnabled()) {
                logger.warn("Can't parse [" + file.getPath() + ']', e);
            }

        } catch (IOException e) {

            // 输出日志
            if (logger.isWarnEnabled()) {
                logger.warn("Can't load [" + file.getPath() + ']', e);
            }

        } finally {

            if (lock != null) {
                lock.unlock();
            }
        }

        return false;
    }

    /**
     * 返回新建的: SAX DocumentBuilder 对象。
     *
     * @return SAX DocumentBuilder 对象
     */
    private static DocumentBuilder createDocumentBuilder() {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回默认加载的配置路径。
     *
     * @return 默认的配置路径
     */
    private static URL getDefault() {

        ClassLoader classLoader = XmlDocConfigurator.class.getClassLoader();

        URL url = classLoader.getResource(JADE_CONFIG_XML);

        if (url == null) {

            if (logger.isWarnEnabled()) {
                logger.warn( // NL
                        "Can't load [" + JADE_CONFIG_XML + "] from [" + classLoader + ']');
            }
        }

        return url;
    }

    /**
     * 读取网址内容存入文件。
     *
     * @param url  - 读取的网址
     * @param file - 存入的文件
     */
    private static void saveToFile(URL url, File file) {

        // 创建文件的目录
        File dir = file.getParentFile();

        if (dir != null) {

            if (!dir.mkdirs()) {

                // 输出日志
                if (logger.isWarnEnabled()) {
                    logger.warn("Can't make dir: " + dir.getPath());
                }
            }
        }

        InputStream fin = null;

        FileOutputStream fout = null;

        try {
            // 读取输入流内容
            URLConnection connect = url.openConnection();

            fin = connect.getInputStream();

            // 将内容写入文件
            fout = new FileOutputStream(file);

            byte[] buffer = new byte[4096];

            int read = fin.read(buffer);

            while (read >= 0) {

                fout.write(buffer, 0, read);

                read = fin.read(buffer);
            }

            fout.flush();

        } catch (IOException e) {

            // 输出日志
            if (logger.isWarnEnabled()) {
                logger.warn("Can't save InputStream to: " + file.getPath());
            }

        } finally {

            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException e) {

                // 输出日志
                if (logger.isWarnEnabled()) {
                    logger.warn("Can't close FileOutputStream");
                }
            }

            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {

                // 输出日志
                if (logger.isWarnEnabled()) {
                    logger.warn("Can't close InputStream");
                }
            }
        }
    }
}
