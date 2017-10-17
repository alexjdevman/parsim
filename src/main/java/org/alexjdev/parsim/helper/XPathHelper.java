package org.alexjdev.parsim.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.ByteArrayOutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Вспомогательный класс для работы с DOM XML
 */
public class XPathHelper {

    public static void putValueInDocumentByXpath(Object value, String xpathValue, Document document, XPath xpath) throws Exception {
        XPathExpression pathExpression = xpath.compile(xpathValue);
        Node node = (Node) pathExpression.evaluate(document, XPathConstants.NODE);
        checkNotNull(node, "В документе не найден тег по XPATH: " + xpathValue);
        if (value != null) {
            node.setTextContent(value.toString());
        }
    }

    public static void putRepeatedValuesInDocumentByNodeIndex(Object value,
                                                              String xpathValue,
                                                              Document document,
                                                              XPath xpath,
                                                              int nodeIndex) throws Exception {
        XPathExpression pathExpression = xpath.compile(xpathValue);
        NodeList nodeList = (NodeList) pathExpression.evaluate(document, XPathConstants.NODESET);
        if (value != null) {
            nodeList.item(nodeIndex).setTextContent(value.toString());
        }
    }

    public static String getValueFromDocumentByXpath(Document document, XPath xpath, String xpathValue) throws Exception {
        XPathExpression pathExpression = xpath.compile(xpathValue);
        Node node = (Node) pathExpression.evaluate(document, XPathConstants.NODE);
        checkNotNull(node, "В документе ничего не найдено по XPATH: " + xpathValue);
        return node.getTextContent();
    }

    public static int getRepeatedNodesCount(Document document,
                                            XPath xpath,
                                            String xpathValue) throws Exception {
        XPathExpression pathExpression = xpath.compile(xpathValue);
        NodeList nodeList = (NodeList) pathExpression.evaluate(document, XPathConstants.NODESET);
        return nodeList.getLength();
    }

    public static String getRepeatedValueFromDocumentByNodeIndex(Document document,
                                                                 XPath xpath,
                                                                 String xpathValue,
                                                                 int nodeIndex) throws Exception {
        XPathExpression pathExpression = xpath.compile(xpathValue);
        NodeList nodeList = (NodeList) pathExpression.evaluate(document, XPathConstants.NODESET);
        int nodeCount = nodeList.getLength();
        checkArgument(nodeCount > 0, "В документе ничего не найдено по XPATH: " + xpathValue);
        return nodeIndex < nodeCount ? nodeList.item(nodeIndex).getTextContent() : null;
    }

    public static byte[] getDocumentContent(Document doc) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(result);
        transformer.transform(source, streamResult);

        return result.toByteArray();
    }

    public static Boolean isElementExists(Document document, String tagName) {
        NodeList nodeList = document.getElementsByTagName(tagName);
        return nodeList.getLength() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Element createElement(Document document, String name, String value) {
        Element node = document.createElement(name);
        node.appendChild(document.createTextNode(value));
        return node;
    }

}
