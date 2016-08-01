package net.paoding.rose.jade.route.parse;

import java.util.Map;

import net.paoding.rose.jade.route.instance.HashRouter;
import net.paoding.rose.jade.route.instance.Router;
import net.paoding.rose.jade.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;


/**
 * 负责从配置项生成对应的路由对象。
 *
 * @author han.liao
 */
public class RouterLoader {

    // 输出日志
    protected static final Log logger = LogFactory.getLog(RouterLoader.class);

    // 路由的名称
    public static final String DIRECT = "direct";

    public static final String ROUND = "round";

    public static final String RANGE = "range";

    public static final String HASH = "hash";

    public static final String HEX_HASH = "hex-hash";

    public static final String HASHCODE = "hashcode";

    public static final String DATE = "date-hash";

    public static Router fromXML(Element element) {

        for (Element child : XMLUtils.getChildren(element)) {

            // <by-column>
            String column = XMLUtils.getChildText(child, "by-column");

            // <partitions>
            String partitions = XMLUtils.getChildText(child, "partitions");

            // <target-pattern>
            String pattern = XMLUtils.getChildText(child, "target-pattern");

            if (logger.isDebugEnabled()) {
                logger.debug("try to create router " + child.getTagName());
            }

            if (HASH.equalsIgnoreCase(child.getTagName())) {
                return createHashRouter(column, pattern, partitions);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("router '" + child.getTagName() + "' is not supported");
                }
            }
        }

        return null;
    }

    private static HashRouter createHashRouter(String column, String pattern, String partitions) {

        // 检查所需的属性
        if (column == null) {

            // 输出日志
            if (logger.isErrorEnabled()) {
                logger.error("Router 'hash' must have 'by-column' property.");
            }

            return null;
        }

        if (partitions == null) {

            // 输出日志
            if (logger.isErrorEnabled()) {
                logger.error("Router 'hash' must have 'partitions' property.");
            }

            return null;
        }

        if (pattern == null) {

            // 输出日志
            if (logger.isErrorEnabled()) {
                logger.error("Router 'hash' must have 'target-pattern' property.");
            }

            return null;
        }

        try {
            int count = Integer.parseInt(partitions);

            // 输出日志
            if (logger.isDebugEnabled()) {
                logger.debug("Creating router 'hash' [by-column = " + column + ", partitions = "
                        + count + ", target-pattern = " + pattern + ']');
            }

            return new HashRouter(column, pattern, count);

        } catch (NumberFormatException e) {

            // 输出日志
            if (logger.isErrorEnabled()) {
                logger.error("Router 'hash' property 'partitions' must be number.");
            }

            return null;
        }
    }

}
