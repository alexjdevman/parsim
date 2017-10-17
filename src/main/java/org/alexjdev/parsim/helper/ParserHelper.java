package org.alexjdev.parsim.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Код, который помогает разбирать XML
 */
public class ParserHelper {

    /**
     * Creating document builder
     *
     * @return document builder
     */
    public static DocumentBuilder getDocumentBuilder() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        try {
            return dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Create XPath instance
     *
     * @return XPath instance
     */
    public static XPath createXPathInstance() {
        XPathFactory factory = XPathFactory.newInstance();
        return factory.newXPath();
    }

    /**
     * Parse xmlString to DOM-tree document
     *
     * @param xmlString xml text
     * @return DOM Document instance
     * @throws ParserConfigurationException then XML parser is not configured in jvm
     * @throws SAXException                 on parsing error
     * @throws IOException                  on error in IO system
     */
    public static Document createDOMByXMLString(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        return parseDocument(xmlString, dbf);
    }

    private static Document parseDocument(String reply, DocumentBuilderFactory dbf) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(org.apache.commons.io.IOUtils.toInputStream(reply, "UTF-8"));
    }

    public static Document createDOMByXMLStream(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(inputStream);
    }

    public static String getAttributeValue(Node node, String attributeName) {
        Node attribute = node.getAttributes().getNamedItem(attributeName);
        if (attribute != null) {
            return attribute.getTextContent();
        } else {
            return null;
        }
    }
}
