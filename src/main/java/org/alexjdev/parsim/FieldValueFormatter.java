package org.alexjdev.parsim;

/**
 * Интерфейс для дополнительного форматирования полей в парсерах
 */
public interface FieldValueFormatter {

    /**
     * Форматирование значения поля
     *
     * @param value исходное значение
     * @return результат
     */
    String formatValue(String value);
}
