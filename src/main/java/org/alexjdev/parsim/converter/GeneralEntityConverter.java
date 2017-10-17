package org.alexjdev.parsim.converter;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для универсального конвертера структуры List<Map<String, Object>> в список сущностей
 */
public interface GeneralEntityConverter<Entity> {

    /**
     * Получение списка сущностей из списка карт полей
     *
     * @param fieldMaps   коллекция карт, в которой ключ - название свойства бина, значение - значение поля в файле
     * @param entityClass тип сущности
     * @return список сущностей
     */
    List<Entity> convert(List<Map<String, Object>> fieldMaps, Class<Entity> entityClass) throws Exception;

    /**
     * Получение сущности из карты полей
     *
     * @param fieldMap    карта, в которой ключ - название свойства бина, значение - значение поля в файле
     * @param entityClass тип сущности
     * @return сущность
     */
    Entity convertInstance(Map<String, Object> fieldMap, Class<Entity> entityClass) throws Exception;

    /**
     * Заполнение сущности из карты полей
     *
     * @param fieldMap карта, в которой ключ - название свойства бина, значение - значение поля в файле
     * @param entity   сущность
     */
    void fillInstance(Map<String, Object> fieldMap, Entity entity) throws Exception;

}
