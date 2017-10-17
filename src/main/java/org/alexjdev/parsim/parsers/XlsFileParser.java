package org.alexjdev.parsim.parsers;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Реализация парсера excel файлов в формате xls
 */
public class XlsFileParser extends ExcelFileParser {

    @Override
    public List<Map<String, Object>> parse(InputStream stream) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook(stream);
        return parseRows(workbook);
    }
}
