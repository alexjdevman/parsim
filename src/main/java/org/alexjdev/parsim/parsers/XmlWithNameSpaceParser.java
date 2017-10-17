package org.alexjdev.parsim.parsers;

import org.alexjdev.parsim.helper.ParserHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;

public class XmlWithNameSpaceParser extends XmlFileParser {
    protected XPath prepareXPath(Node doc) {
        final XPath xPath = ParserHelper.createXPathInstance();
        if (doc instanceof Document) {
            xPath.setNamespaceContext(new UniversalNamespaceResolver((Document) doc));
        }
        return xPath;
    }
}
