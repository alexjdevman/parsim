package org.alexjdev.parsim.preference;

/**
 * Валюта
 */
public enum Currency {
    RUB(643), RUR(810), USD(840), EUR(978);

    private Integer code;

    Currency(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static Currency getCurrencyByCode(Integer code) {
        for (Currency currency : Currency.values()) {
            if (currency.code.equals(code)) {
                return currency;
            }
        }
        return null;
    }
}
