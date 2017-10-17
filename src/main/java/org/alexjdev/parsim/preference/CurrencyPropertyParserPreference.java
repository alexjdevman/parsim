package org.alexjdev.parsim.preference;

public class CurrencyPropertyParserPreference extends ParserPreference {

    @Override
    public Object getResultValue(String value) throws Exception {
        int currencyCode = Integer.parseInt(value);
        return Currency.getCurrencyByCode(currencyCode);
    }
}
