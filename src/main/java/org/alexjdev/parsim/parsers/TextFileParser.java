package org.alexjdev.parsim.parsers;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.alexjdev.parsim.FileParser;
import org.alexjdev.parsim.preference.ParserPreference;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Реализация парсера текстовых файлов
 */
public class TextFileParser implements FileParser {

    public static final Log LOGGER = LogFactory.getFactory().getInstance(FileParser.class);

    private String separator;
    private List<ParserPreference> preferences;

    @Override
    public List<Map<String, Object>> parse(InputStream stream) {
        List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
        try {
            final List<String> lines = IOUtils.readLines(stream);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Разбирается файл\n\n %s \n\n", Joiner.on("\n").join(lines)));
            }
            if (lines.isEmpty()) {
                return Collections.emptyList();
            }
            Map<String, Integer> columns = parseFileHeader(lines.get(0));
            final List<String> linesWithData = getLinesWithData(lines);
            for (String line : linesWithData) {
                if (isNotBlank(line)) {
                    result.add(parseLine(columns, line));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return ImmutableList.copyOf(result);
    }

    private List<String> getLinesWithData(List<String> lines) {
        return copyOf(lines).subList(1, lines.size());
    }

    private Map<String, Object> parseLine(Map<String, Integer> columnMap, String line) throws ParseException {
        final List<String> values = ImmutableList.copyOf(on(separator).trimResults().split(line));
        Map<String, Object> result = new HashMap<String, Object>();
        for (ParserPreference preference : preferences) {
            final Property property = fillEntity(preference, columnMap, values);
            result.put(property.name, property.value);
        }
        return result;
    }

    private Property fillEntity(ParserPreference preference,
                                Map<String, Integer> columnMap,
                                List<String> values) throws ParseException {

        String columnName = preference.getColumnName();
        String propertyName = preference.getPropertyName();
        Integer index = columnMap.get(columnName);
        if (index != null) {
            String value = values.get(index);
            Property property = new Property();
            property.name = propertyName;
            property.value = getObjectByPreference(preference, value);
            return property;
        } else {
            throw new RuntimeException("Не найдено поле с названием " + columnName);
        }
    }

    private Object getObjectByPreference(ParserPreference preference, String value) throws ParseException {
        Class<?> columnType = preference.getColumnType();
        String datePattern = preference.getDatePattern();
        if (Integer.class.isAssignableFrom(columnType)) {
            return Integer.parseInt(value);
        }
        if (Long.class.isAssignableFrom(columnType)) {
            return Long.parseLong(value);
        }
        if (Double.class.isAssignableFrom(columnType)) {
            return getDoubleValue(value);
        }
        if (Date.class.isAssignableFrom(columnType)) {
            return getDateValue(value, datePattern);
        }

        return value;
    }

    private Double getDoubleValue(String value) {
        return isNotBlank(value) ? Double.parseDouble(value.replace(',', '.')) : null;
    }

    private Date getDateValue(String value,
                              String datePattern) throws ParseException {
        return isNotBlank(value) ? DateUtils.parseDate(value, new String[]{datePattern}) : null;
    }

    private Map<String, Integer> parseFileHeader(String header) {
        if (StringUtils.isBlank(header)) {
            return ImmutableMap.of();
        }
        final HashMap<String, Integer> result = newHashMap();
        int idx = 0;
        for (String columnName : on(separator).omitEmptyStrings().split(header)) {
            result.put(columnName.replace("\"", ""), idx++);
        }
        return ImmutableMap.copyOf(result);
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setPreferences(List<ParserPreference> preferences) {
        this.preferences = preferences;
    }

}
