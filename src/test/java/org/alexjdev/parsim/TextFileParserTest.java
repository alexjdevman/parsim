package org.alexjdev.parsim;

import org.alexjdev.parsim.converter.EntityConverter;
import org.alexjdev.parsim.parsers.PlainTextFileParser;
import org.alexjdev.parsim.preference.Currency;
import org.alexjdev.parsim.preference.CurrencyPropertyParserPreference;
import org.alexjdev.parsim.preference.ParserPreference;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Тестирование разбора текстовых файлов
 */
public class TextFileParserTest {

    private PlainTextFileParser parser;

    @Before
    public void setUp() {
       parser = new PlainTextFileParser();
    }

    @Test
    public void test_Parse_Plain_Text_File() throws Exception {
        InputStream data = this.getClass().getResourceAsStream("/text/text_file_1.txt");
        parser.setSeparator("\t");
        parser.setPreferences(getPreference());

        List<Map<String, Object>> result = parser.parse(data);
        assertEquals(3, result.size());
        assertEquals(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("23.12.2016 13:07:24"),
                result.get(0).get("dateField"));
        assertEquals("Transaction_1", result.get(0).get("strField"));
        assertEquals(100, result.get(0).get("intField"));
        assertEquals(1000.25, result.get(0).get("doubleField"));
        assertEquals(Currency.RUR, result.get(0).get("currency"));

        assertEquals(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("21.12.2016 13:55:00"),
                result.get(1).get("dateField"));
        assertEquals("Transaction_2", result.get(1).get("strField"));
        assertEquals(200, result.get(1).get("intField"));
        assertEquals(2200.15, result.get(1).get("doubleField"));
        assertEquals(Currency.RUB, result.get(1).get("currency"));

        assertEquals(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("21.12.2016 10:45:24"),
                result.get(2).get("dateField"));
        assertEquals("Transaction_3", result.get(2).get("strField"));
        assertEquals(300, result.get(2).get("intField"));
        assertEquals(3000.0, result.get(2).get("doubleField"));
        assertEquals(Currency.EUR, result.get(2).get("currency"));

        //преобразование в список сущностей
        List<ParseModel> entityList = new EntityConverter<ParseModel>().convert(result, ParseModel.class);
        assertEquals(3, entityList.size());
        assertEquals(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("23.12.2016 13:07:24"),
                entityList.get(0).getDateField());
        assertEquals("Transaction_1", entityList.get(0).getStrField());
        assertEquals(100, entityList.get(0).getIntField().intValue());
        assertEquals(1000.25, entityList.get(0).getDoubleField());
        assertEquals(Currency.RUR, entityList.get(0).getCurrency());

        assertEquals(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("21.12.2016 13:55:00"),
                entityList.get(1).getDateField());
        assertEquals("Transaction_2", entityList.get(1).getStrField());
        assertEquals(200, entityList.get(1).getIntField().intValue());
        assertEquals(2200.15, entityList.get(1).getDoubleField());
        assertEquals(Currency.RUB, entityList.get(1).getCurrency());

        assertEquals(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("21.12.2016 10:45:24"),
                entityList.get(2).getDateField());
        assertEquals("Transaction_3", entityList.get(2).getStrField());
        assertEquals(300, entityList.get(2).getIntField().intValue());
        assertEquals(3000.0, entityList.get(2).getDoubleField());
        assertEquals(Currency.EUR, entityList.get(2).getCurrency());

    }

    public List<ParserPreference> getPreference() {
        // настройка для поля даты
        ParserPreference pref1 = new ParserPreference();
        pref1.setColumnName("Date Field");
        pref1.setPropertyName("dateField");
        pref1.setColumnType(Date.class);
        pref1.setDatePattern("dd.MM.yyyy HH:mm:ss");

        // настройка для строкового поля
        ParserPreference pref2 = new ParserPreference();
        pref2.setColumnName("String Field");
        pref2.setPropertyName("strField");
        pref2.setColumnType(String.class);

        // настройка для int поля
        ParserPreference pref3 = new ParserPreference();
        pref3.setColumnName("Integer Field");
        pref3.setPropertyName("intField");
        pref3.setColumnType(Integer.class);

        ParserPreference pref4 = new ParserPreference();
        pref4.setColumnName("Double Field");
        pref4.setPropertyName("doubleField");
        pref4.setColumnType(Double.class);

        CurrencyPropertyParserPreference pref5 = new CurrencyPropertyParserPreference();
        pref5.setColumnName("Currency Field");
        pref5.setPropertyName("currency");
        pref5.setColumnType(Currency.class);

        return Arrays.asList(pref1, pref2, pref3, pref4, pref5);
    }
}

