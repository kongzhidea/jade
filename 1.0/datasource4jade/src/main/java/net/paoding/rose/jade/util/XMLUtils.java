package net.paoding.rose.jade.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 定义公共的 XML 处理方法。
 *
 * @author han.liao
 */
public class XMLUtils {

    /**
     * 从 XML 元素读取文本。
     *
     * @param node - XML 节点
     * @return XML 节点的文本
     */
    public static String getText(Element node) {

        NodeList children = node.getChildNodes();

        if (0 < children.getLength()) {

            String value = children.item(0).getNodeValue();

            if (value != null) {
                return value.trim();
            }
        }

        return null;
    }

    /**
     * 从 XML 父元素返回所有子元素。
     *
     * @param parent - XML 父元素
     * @return 所有子元素列表
     */
    public static List<Element> getChildren(Element parent) {

        NodeList children = parent.getChildNodes();

        final int length = children.getLength();

        ArrayList<Element> elements = new ArrayList<Element>();

        for (int index = 0; index < length; index++) {

            Node node = children.item(index);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            elements.add((Element) node);
        }

        return elements;
    }

    /**
     * 从 XML 父元素返回指定名称的子元素。
     *
     * @param parent - XML 父元素
     * @param name   - 子元素的名称
     * @return 符合的子元素
     */
    public static Element getChild(Element parent, String name) {

        NodeList children = parent.getChildNodes();

        final int length = children.getLength();

        for (int index = 0; index < length; index++) {

            Node node = children.item(index);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) node;

            if (name.equalsIgnoreCase(element.getNodeName())) {
                return element;
            }
        }

        return null;
    }

    /**
     * 从 XML 父元素返回指定名称的子元素内容。
     *
     * @param parent - XML 父元素
     * @param name   - 子元素的名称
     * @return 符合的子元素内容
     */
    public static String getChildText(Element parent, String name) {

        Element child = getChild(parent, name);

        if (child != null) {
            return getText(child);
        }

        return null;
    }
}
