package org.alexjdev.parsim.parsers;

import org.alexjdev.parsim.FileParser;
import org.alexjdev.parsim.helper.ParserHelper;
import org.alexjdev.parsim.preference.ParserPreference;
import org.alexjdev.parsim.resolver.NamespaceContextResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Реализация парсера xml файлов
 */
public class XmlFileParser implements FileParser {

    public static final Log LOGGER = LogFactory.getFactory().getInstance(XmlFileParser.class);

    private List<ParserPreference> preferences;
    private String entitySearchPath;

    @Override
    public List<Map<String, Object>> parse(InputStream stream) throws Exception {
        Document doc = ParserHelper.createDOMByXMLStream(stream);
        return parseDocument(doc, entitySearchPath);
    }

    private List<Map<String, Object>> parseDocument(Document doc, String xpathUrl) throws Exception {
        List<Map<String, Object>> result = new LinkedList<>();
        XPath xpath = prepareXPath(doc);
        xpath.setNamespaceContext(new NamespaceContextResolver(doc));
        XPathExpression pathExpression = xpath.compile(xpathUrl);
        NodeList nodeList = (NodeList) pathExpression.evaluate(doc, XPathConstants.NODESET);
        for (int nodeIdx = 0; nodeIdx < nodeList.getLength(); nodeIdx++) {
            Node entityNode = nodeList.item(nodeIdx);
            result.add(fillEntity(entityNode));
        }
        return result;
    }

    protected XPath prepareXPath(Node doc) {
        return ParserHelper.createXPathInstance();
    }

    private Map<String, Object> fillEntity(Node entityNode) throws Exception {
        XPath xpath = prepareXPath(entityNode);
        Map<String, Object> map = new HashMap<>();
        for (ParserPreference preference : preferences) {
            String propertyPath = preference.getColumnName();
            String propertyName = preference.getPropertyName();

            XPathExpression pathExpression = xpath.compile(propertyPath);
            Node node = (Node) pathExpression.evaluate(entityNode, XPathConstants.NODE);
            if (node != null) {
                String nodeContent = node.getTextContent();
                map.put(propertyName, preference.getResultValue(nodeContent));
            } else {
                handleFieldNotFoundException(propertyPath);
            }
        }
        return map;
    }

    protected void handleFieldNotFoundException(String fieldPath) {
        throw new RuntimeException("Не найдено поле с XPATH " + fieldPath);
    }

    public void setEntitySearchPath(String entitySearchPath) {
        this.entitySearchPath = entitySearchPath;
    }

    public void setPreferences(List<ParserPreference> preferences) {
        this.preferences = preferences;
    }
}
