package org.alexjdev.parsim;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс парсера файлов
 */
public interface FileParser {

    /**
     * Разбор содержимого файла
     *
     * @param stream входной поток
     * @return коллекция карт, в которой ключ - название свойства бина, значение - значение поля в файле
     */
    List<Map<String, Object>> parse(InputStream stream) throws Exception;
}
