package org.alexjdev.parsim;

import org.alexjdev.parsim.parsers.ExcelFileParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Тестирование разбора файла Excel
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/parser-preference.xml"})
public class ExcelFileParserTest {

    @Autowired
    private ExcelFileParser excelParser;

    @Test
    public void test_Parse_Excel() throws Exception {
        InputStream stream = this.getClass().getResourceAsStream("/xlsx/HostFraud.xlsx");
        List<Map<String, Object>> result = excelParser.parse(stream);
        Map<String, Object> firstRecord = result.get(0);
        Map<String, Object> lastRecord =  result.get(result.size() - 1);

        assertEquals(263, result.size());

        assertEquals("11111111", firstRecord.get("corrTermId"));
        assertEquals("Merchant", firstRecord.get("corrMerch"));
        assertEquals(388, firstRecord.get("countOp"));
        assertEquals(3232334.0, firstRecord.get("sumRur"));
        assertEquals(17.0, firstRecord.get("percent"));

        assertEquals("22222222", lastRecord.get("corrTermId"));
        assertEquals("Merchant", lastRecord.get("corrMerch"));
        assertEquals(1, lastRecord.get("countOp"));
        assertEquals(25.07, lastRecord.get("sumRur"));
        assertEquals(0.0, lastRecord.get("percent"));

    }
}
