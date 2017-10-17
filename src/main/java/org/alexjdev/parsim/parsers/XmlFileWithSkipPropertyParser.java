package org.alexjdev.parsim.parsers;

import org.slf4j.LoggerFactory;

public class XmlFileWithSkipPropertyParser extends XmlFileParser {
    @Override
    protected void handleFieldNotFoundException(String fieldPath) {
        LoggerFactory.getLogger(getClass()).warn("Не найдено поле с XPATH {}", fieldPath);
    }
}
