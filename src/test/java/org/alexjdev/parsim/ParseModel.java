package org.alexjdev.parsim;

import org.alexjdev.parsim.preference.Currency;

import java.util.Date;

/**
 * Тестовая модель для разбора данных из файла
 */
public class ParseModel {

    private Date dateField;
    private String strField;
    private Integer intField;
    private Double doubleField;
    private Currency currency;

    public Date getDateField() {
        return dateField;
    }

    public void setDateField(Date dateField) {
        this.dateField = dateField;
    }

    public String getStrField() {
        return strField;
    }

    public void setStrField(String strField) {
        this.strField = strField;
    }

    public Integer getIntField() {
        return intField;
    }

    public void setIntField(Integer intField) {
        this.intField = intField;
    }

    public Double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(Double doubleField) {
        this.doubleField = doubleField;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

}
