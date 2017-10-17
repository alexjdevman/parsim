package org.alexjdev.parsim.parsers;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.alexjdev.parsim.FileParser;
import org.alexjdev.parsim.exception.ParserFieldNotFoundException;
import org.alexjdev.parsim.preference.ParserPreference;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Реализация парсера текстовых файлов с плоской структурой
 */
public class PlainTextFileParser implements FileParser {

    public static final Logger LOGGER = LoggerFactory.getLogger(PlainTextFileParser.class);
    protected String separator;
    private String encoding;
    private int fileHeaderIndex;
    private List<ParserPreference> preferences;

    @Override
    public List<Map<String, Object>> parse(InputStream stream) {

        try {
            final List<String> lines = IOUtils.readLines(stream, StringUtils.defaultString(encoding, "UTF-8"));
            LOGGER.debug("Разбирается файл\n\n {} \n\n", Joiner.on("\n").join(lines));
            if (lines.isEmpty()) {
                return emptyList();
            }
            Map<String, Integer> columns = prepareColumnMap(lines);
            final List<String> linesWithData = getLinesWithData(lines);
            return parseData(columns, linesWithData);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    protected List<Map<String, Object>> parseData(Map<String, Integer> columns, List<String> linesWithData) throws Exception {
        List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
        for (String line : linesWithData) {
            if (isNotBlank(line) && isNeedParseLine(line)) {
                result.add(parseLine(columns, line));
            }
        }
        return ImmutableList.copyOf(result);
    }

    private Map<String, Integer> prepareColumnMap(List<String> lines) {
        Map<String, Integer> columns;
        if (isNeedParseFileHeader()) {
            columns = parseFileHeader(lines.get(getFileHeaderIndex(lines)));
        } else {
            columns = emptyMap();
        }
        return columns;
    }

    /**
     * Метод, определяющий, нужен ли разбор заголовка файла (Header)
     *
     * @return по умолчанию true
     */
    protected boolean isNeedParseFileHeader() {
        return Boolean.TRUE;
    }

    /**
     * Метод, возвращающий номер заголовка (Header) в файле
     *
     * @param lines содержимое файла в виде списка строк
     * @return по умолчанию 0 (первая строка файла)
     */
    protected int getFileHeaderIndex(List<String> lines) {
        return fileHeaderIndex;
    }

    /**
     * Метод, определяющий, нужен ли разбор строки файла
     *
     * @param line строка
     * @return по умолчанию true
     */
    protected boolean isNeedParseLine(String line) {
        return Boolean.TRUE;
    }

    private List<String> getLinesWithData(List<String> lines) {
        ImmutableList<String> immutableLines = copyOf(lines);
        if (isNeedParseFileHeader()) {
            return immutableLines.subList(getFileHeaderIndex(lines) + 1, lines.size());
        }
        return immutableLines;
    }

    Map<String, Object> parseLine(Map<String, Integer> columnMap, String line) throws Exception {
        try {
            final List<String> values = ImmutableList.copyOf(on(separator).trimResults().split(line));
            Map<String, Object> result = new HashMap<String, Object>();
            for (ParserPreference preference : preferences) {
                final Property property = fillEntity(preference, columnMap, values);
                result.put(property.name, property.value);
            }
            return result;
        } catch (Exception err) {
            LoggerFactory.getLogger(getClass()).error("Возникла ошибка разбора строки {}", line);
            throw err;
        }
    }

    private Property fillEntity(ParserPreference preference,
                                Map<String, Integer> columnMap,
                                List<String> values) throws Exception {

        String columnName = preference.getColumnName();
        String propertyName = preference.getPropertyName();
        Integer index = columnMap.isEmpty() ? preference.getColumnNumber() : columnMap.get(columnName);
        if (index != null) {
            String value = values.get(index);
            Property property = new Property();
            property.name = propertyName;
            property.value = preference.getResultValue(value);
            return property;
        } else {
            throw new ParserFieldNotFoundException("Не найдено поле с названием " + columnName);
        }
    }

    private Map<String, Integer> parseFileHeader(String header) {
        if (StringUtils.isBlank(header)) {
            return ImmutableMap.of();
        }
        final HashMap<String, Integer> result = newHashMap();
        int idx = 0;
        for (String columnName : on(separator).omitEmptyStrings().split(header)) {
            result.put(columnName.replace("\"", "").trim(), idx++);
        }
        return ImmutableMap.copyOf(result);
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setPreferences(List<ParserPreference> preferences) {
        this.preferences = preferences;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setFileHeaderIndex(int fileHeaderIndex) {
        this.fileHeaderIndex = fileHeaderIndex;
    }
}
