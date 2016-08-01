package net.paoding.rose.jade.route.parse;

import java.util.Map;

import net.paoding.rose.jade.route.DirectRoutingDescriptor;
import net.paoding.rose.jade.route.RouterDescriptor;
import net.paoding.rose.jade.route.RoutingDescriptor;
import net.paoding.rose.jade.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 负责从配置项生成对应的配置描述对象。
 *
 * @author han.liao
 */
public class RoutingDescriptorLoader {

    // 输出日志
    protected static final Log logger = LogFactory.getLog(RoutingDescriptorLoader.class);

    public static RoutingDescriptor create(String dbname) {

        return new DirectRoutingDescriptor(dbname);
    }

    public static void loadXMLDoc(Map<String, RoutingDescriptor> map, Document document) {

        Element element = document.getDocumentElement();

        if (!"jade-config".equalsIgnoreCase(element.getTagName())) {

            // 输出日志
            if (logger.isErrorEnabled()) {
                logger.error("XML ROOT must be <jade-config>.");
            }

            return;
        }

        for (Element child : XMLUtils.getChildren(element)) {

            if (!"catalog".equalsIgnoreCase(child.getTagName())) {
                continue;
            }

            loadCatalog(map, child);
        }
    }

    public static void loadCatalog(Map<String, RoutingDescriptor> map, Element element) {

        String catalog = element.getAttribute("id");

        if (catalog == null) {

            // 输出日志
            if (logger.isErrorEnabled()) {
                logger.error("XML node <catalog> must have 'id' attribute.");
            }

            return;
        }

        String defaultDbName = null; // 配置的  <default-dbname> 

        for (Element child : XMLUtils.getChildren(element)) {

            if ("default-dbname".equalsIgnoreCase(child.getTagName())) {

                defaultDbName = XMLUtils.getText(child);

            } else if ("table".equalsIgnoreCase(child.getTagName())) {

                String name = child.getAttribute("name");

                if (name == null) {

                    // 输出日志
                    if (logger.isErrorEnabled()) {
                        logger.error("XML node <table> must have 'name' attribute.");
                    }

                } else {

                    // 保存数据表的配置信息
                    RoutingDescriptor descriptor = fromXML(child, defaultDbName);

                    // 输出日志
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found config [catalog = " + catalog + ", name = " + name
                                + "] dbname = " + descriptor.getDbName());
                    }

                    map.put(catalog + '.' + name, descriptor);
                }
            }
        }

        if (defaultDbName != null) {

            // 输出日志
            if (logger.isDebugEnabled()) {
                logger.debug("Found config [catalog = " + catalog + "] dbname = " + defaultDbName);
            }

            // 保存模块的配置信息
            map.put(catalog, new DirectRoutingDescriptor(defaultDbName));
        }
    }

    public static RoutingDescriptor fromXML(Element element, String defaultDbname) {

        RouterDescriptor descriptor = new RouterDescriptor(defaultDbname);

        for (Element child : XMLUtils.getChildren(element)) {

            if ("dbname".equalsIgnoreCase(child.getTagName())) {

                descriptor.setDbName(XMLUtils.getText(child));

            } else if ("db-partitions".equalsIgnoreCase(child.getTagName())) {

                descriptor.setDbRouter(RouterLoader.fromXML(child));

            } else if ("table-partitions".equalsIgnoreCase(child.getTagName())) {

                descriptor.setTableRouter(RouterLoader.fromXML(child));
            }
        }

        return descriptor;
    }
}
