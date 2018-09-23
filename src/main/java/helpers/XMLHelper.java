package helpers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XMLHelper {

    public static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

    public static String getTagValue(String tag, Node node) {
        NodeList nodeList = null;
        Node tmpNode = null;
        Node cNode = null;

        nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++){
            tmpNode = nodeList.item(i);
            if (tmpNode.getNodeName().equals(tag)){
                return tmpNode.getTextContent();
            }
        }
        return null;
    }

    public static void setTagValue(String tag, String value, Node node) {
        NodeList nodeList = null;
        Node tmpNode = null;
        Node cNode = null;

        nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++){
            tmpNode = nodeList.item(i);
            if (tmpNode.getNodeName().equals(tag)){
                tmpNode.setTextContent(value);
            }
        }
    }

}
