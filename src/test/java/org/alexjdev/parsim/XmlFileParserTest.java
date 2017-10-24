package org.alexjdev.parsim;

import org.alexjdev.parsim.parsers.XmlFileParser;
import org.alexjdev.parsim.preference.ParserPreference;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тестирование xml парсера
 */
public class XmlFileParserTest {

    private XmlFileParser parser;

    @Before
    public void setUp() {
        parser = new XmlFileParser();
        parser.setEntitySearchPath("/report_info/ROW");
        parser.setPreferences(getParserPreferences());
    }

    @Test
    public void test_Xml_File_Parser() throws Exception {
        InputStream stream = this.getClass().getResourceAsStream("/xml/xml_data.xml");
        List<Map<String, Object>> result = parser.parse(stream);
        Map<String, Object> firstItem = result.get(0);

        assertEquals("Case status changed to RELEASED", firstItem.get("commentText"));
        assertEquals("agafonovda", firstItem.get("user"));
        assertEquals("22:55:09", firstItem.get("createTime"));
        assertEquals("10058682606", firstItem.get("caseId"));
        assertEquals("AUTOMATIC", firstItem.get("commentType"));
        assertEquals("bank", firstItem.get("institution"));
        assertNotNull(firstItem.get("createDate"));

        Map<String, Object> lastItem = result.get(2);

        assertEquals("Case status changed to RELEASED", lastItem.get("commentText"));
        assertEquals("aaaaaaaa", lastItem.get("user"));
        assertEquals("23:00:23", lastItem.get("createTime"));
        assertEquals("10058682655", lastItem.get("caseId"));
        assertEquals("AUTOMATIC", lastItem.get("commentType"));
        assertEquals("bank", lastItem.get("institution"));
        assertEquals(DateUtils.parseDate("15.02.27", new String[]{"yy.MM.dd"}), lastItem.get("createDate"));
    }

    private List<ParserPreference> getParserPreferences() {
        ParserPreference preference1 = new ParserPreference();
        preference1.setColumnName("./create_date");
        preference1.setPropertyName("createDate");
        preference1.setColumnType(Date.class);
        preference1.setDatePattern("yy.MM.dd");

        ParserPreference preference2 = new ParserPreference();
        preference2.setColumnName("./comment_text");
        preference2.setPropertyName("commentText");
        preference2.setColumnType(String.class);

        ParserPreference preference3 = new ParserPreference();
        preference3.setColumnName("./user_");
        preference3.setPropertyName("user");
        preference3.setColumnType(String.class);

        ParserPreference preference4 = new ParserPreference();
        preference4.setColumnName("./create_time");
        preference4.setPropertyName("createTime");
        preference4.setColumnType(String.class);

        ParserPreference preference5 = new ParserPreference();
        preference5.setColumnName("./case_id");
        preference5.setPropertyName("caseId");
        preference5.setColumnType(String.class);

        ParserPreference preference6 = new ParserPreference();
        preference6.setColumnName("./comment_type");
        preference6.setPropertyName("commentType");
        preference6.setColumnType(String.class);

        ParserPreference preference7 = new ParserPreference();
        preference7.setColumnName("./institution");
        preference7.setPropertyName("institution");
        preference7.setColumnType(String.class);

        return Arrays.asList(preference1, preference2, preference3,
                             preference4, preference5, preference6,
                             preference7);
    }
}
