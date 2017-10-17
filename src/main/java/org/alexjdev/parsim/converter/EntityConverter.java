package org.alexjdev.parsim.converter;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Реализация параметризованного конвертера
 */
public class EntityConverter<Entity> implements GeneralEntityConverter<Entity> {

    @Override
    public List<Entity> convert(List<Map<String, Object>> fieldMaps, Class<Entity> entityClass) throws Exception {
        List<Entity> result = new LinkedList<Entity>();
        for (Map<String, Object> map : fieldMaps) {
            result.add(convertInstance(map, entityClass));
        }
        return result;
    }

    @Override
    public Entity convertInstance(Map<String, Object> fieldMap, Class<Entity> entityClass) throws Exception {
        Entity entity = entityClass.newInstance();
        prepareFieldMap(fieldMap);
        fillInstance(fieldMap, entity);
        return entity;
    }

    @Override
    public void fillInstance(Map<String, Object> fieldMap, Entity entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = fieldMap.get(fieldName);

            if (value != null) {
                field.set(entity, value);
            }
        }
    }

    /**
     * Переопределив этот метод, можно подправить карту значенией перед тем как на её основе создавать новую сущность
     *
     * @param fieldMap карта значений
     */
    public void prepareFieldMap(Map<String, Object> fieldMap) {
    }
}
