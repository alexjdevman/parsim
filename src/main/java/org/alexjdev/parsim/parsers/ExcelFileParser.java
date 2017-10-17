package org.alexjdev.parsim.parsers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.alexjdev.parsim.FileParser;
import org.alexjdev.parsim.preference.ParserPreference;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Реализация парсера excel файлов
 */
public class ExcelFileParser implements FileParser {

    public static final Log LOGGER = LogFactory.getFactory().getInstance(ExcelFileParser.class);

    /**
     * Список настроек полей для парсера
     */
    private List<ParserPreference> filePreferenceList;

    /**
     * Номер таблицы в excel документе (нумерация с 0)
     */
    private Integer workSheetNumber;

    /**
     * Номер строки, где расположены названия полей (нумерация с 0, если данной строки нет - устанавливать -1)
     */
    private Integer fieldInfoRowNumber;

    /**
     * Номер строки, где расположены данные для разбора
     */
    private Integer dataRowStartNumber;

    @Override
    public List<Map<String, Object>> parse(InputStream stream) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(stream);
        return parseRows(workbook);
    }

    protected List<Map<String, Object>> parseRows(Workbook workbook) {
        List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
        try {
            Sheet sheet = workbook.getSheetAt(workSheetNumber);
            Map<String, Integer> fieldMap = buildFieldMap(sheet.getRow(fieldInfoRowNumber));
            for (int rowIndex = dataRowStartNumber; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                final Row row = sheet.getRow(rowIndex);
                if (!isRowEmpty(row)) {
                    result.add(parseRow(row, fieldMap));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return ImmutableList.copyOf(result);
    }

    private boolean isRowEmpty(Row r) {
        int lastColumn = Math.max(r.getLastCellNum(), 0);

        for (int cn = 0; cn < lastColumn; cn++) {
            Cell c = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
            if (c != null) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Object> parseRow(Row row, Map<String, Integer> fieldMap) throws ParseException {
        Map<String, Object> result = new HashMap<String, Object>();
        for (ParserPreference preference : filePreferenceList) {
            final Property property = fillEntity(preference, fieldMap, row);
            result.put(property.name, property.value);
        }
        return result;
    }

    private Map<String, Integer> buildFieldMap(Row row) {
        Map<String, Integer> result = Maps.newHashMap();
        if (fieldInfoRowNumber == -1) {
            return result;
        }
        for (int column = 0; column < row.getLastCellNum(); column++) {
            final String columnName = formatColumnName(getStringCellValue(row, column));
            if (!result.keySet().contains(columnName)) {
                result.put(columnName, column);
            }
        }
        return result;
    }

    private Property fillEntity(ParserPreference preference,
                                Map<String, Integer> fieldMap,
                                Row row) throws ParseException {

        String columnName = preference.getColumnName();
        Integer columnNumber = preference.getColumnNumber();
        String propertyName = preference.getPropertyName();
        Integer index = fieldMap.isEmpty() ? columnNumber : fieldMap.get(columnName);
        if (index != null) {
            Property property = new Property();
            property.name = propertyName;
            property.value = getValue(preference, row, index);
            return property;
        } else {
            throw new RuntimeException("Не найдено поле с названием " + columnName);
        }
    }

    private Object getValue(ParserPreference preference, Row row, Integer index) throws ParseException {
        Class<?> columnType = preference.getColumnType();
        String datePattern = preference.getDatePattern();
        if (Integer.class.isAssignableFrom(columnType)) {
            return parseIntValue(getStringCellValue(row, index));
        }
        if (Double.class.isAssignableFrom(columnType)) {
            return parseDoubleValue(getStringCellValue(row, index));
        }
        if (Date.class.isAssignableFrom(columnType)) {
            return getDateCellValue(row, index, datePattern);
        }

        return getStringCellValue(row, index);
    }

    private Date getDateCellValue(Row row, int columnNumber, String datePattern) {
        Date result = null;
        try {
            final Cell cell = row.getCell(columnNumber);
            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_STRING) {
                String cellValue = cell.getRichStringCellValue().getString();
                return isNotBlank(cellValue) ? DateUtils.parseDate(cellValue, new String[]{datePattern}) : null;
            }
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                Double cellValue = cell.getNumericCellValue();
                if (HSSFDateUtil.isValidExcelDate(cell.getNumericCellValue())) {
                    result = HSSFDateUtil.getJavaDate(cellValue);
                }
            }
        } catch (ParseException err) {
            throw new RuntimeException(err.getMessage());
        }
        return result;
    }

    private String getStringCellValue(Row row, int cellNumber) {
        final Cell cell = row.getCell(cellNumber);
        String result;
        if (cell != null) {
            int cellType = cell.getCellType();
            switch (cellType) {
                case Cell.CELL_TYPE_BLANK:
                    result = "";
                    break;
                case Cell.CELL_TYPE_STRING:
                    result = cell.getRichStringCellValue().getString();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    NumberFormat numberFormat = DecimalFormat.getInstance();
                    numberFormat.setGroupingUsed(Boolean.FALSE);
                    result = (numberFormat.format(cell.getNumericCellValue()));
                    break;
                default:
                    result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    private String formatColumnName(String columnName) {
        return columnName.replaceAll("\n", " ").
                replaceAll("\\s+", " ").trim();
    }

    private Double parseDoubleValue(String value) {
        return isNotBlank(value) ? Double.parseDouble(value.replace(',', '.')) : null;
    }

    private Integer parseIntValue(String value) {
        return isNotBlank(value) ? Integer.parseInt(value) : null;
    }

    public void setFilePreferenceList(List<ParserPreference> filePreferenceList) {
        this.filePreferenceList = filePreferenceList;
    }

    public void setWorkSheetNumber(Integer workSheetNumber) {
        this.workSheetNumber = workSheetNumber;
    }

    public void setFieldInfoRowNumber(Integer fieldInfoRowNumber) {
        this.fieldInfoRowNumber = fieldInfoRowNumber;
    }

    public void setDataRowStartNumber(Integer dataRowStartNumber) {
        this.dataRowStartNumber = dataRowStartNumber;
    }
}
