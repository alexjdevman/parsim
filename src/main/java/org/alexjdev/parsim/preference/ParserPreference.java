package org.alexjdev.parsim.preference;

import org.alexjdev.parsim.FieldValueFormatter;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Бин настройки для парсеров файлов
 */
public class ParserPreference {

    private String columnName;
    private Integer columnNumber;
    private String propertyName;
    private Class<?> columnType;
    private Class<FieldValueFormatter> formatRule;
    private String datePattern;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Class<?> getColumnType() {
        return columnType;
    }

    public void setColumnType(Class<?> columnType) {
        this.columnType = columnType;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public Class<FieldValueFormatter> getFormatRule() {
        return formatRule;
    }

    public void setFormatRule(Class<FieldValueFormatter> formatRule) {
        this.formatRule = formatRule;
    }

    public Object getResultValue(String value) throws Exception {
        try {
            value = formatValue(value);
            if (Integer.class.isAssignableFrom(columnType)) {
                if (isNotBlank(value)) {
                    return Integer.parseInt(value);
                } else {
                    return null;
                }
            }
            if (Long.class.isAssignableFrom(columnType)) {
                if (isNotBlank(value)) {
                    return Long.parseLong(value);
                } else {
                    return null;
                }
            }
            if (Double.class.isAssignableFrom(columnType)) {
                if (isNotBlank(value)) {
                    return getDoubleValue(value);
                } else {
                    return null;
                }
            }
            if (Date.class.isAssignableFrom(columnType)) {
                return getDateValue(value, datePattern);
            }

            return value;
        } catch (Exception err) {
            LoggerFactory.getLogger(getClass()).error("Ошибка {} при разборе значения {} поля {}",
                                                      err.getMessage(),
                                                      value,
                                                      propertyName);
            throw err;
        }
    }

    public String formatValue(String value) throws Exception {
        if (formatRule != null) {
            FieldValueFormatter formatter = formatRule.newInstance();
            return formatter.formatValue(value);
        }
        return value;
    }

    private Double getDoubleValue(String value) {
        return isNotBlank(value) ? Double.parseDouble(value.replace(',', '.')) : null;
    }

    private Date getDateValue(String value,
                              String datePattern) throws ParseException {
        Locale.setDefault(Locale.ENGLISH);
        String[] patterns;
        if (datePattern.contains("|")) {
            patterns = datePattern.split("\\|");
        } else {
            patterns = new String[]{datePattern};
        }
        return isNotBlank(value) ? DateUtils.parseDate(value, patterns) : null;
    }

}
