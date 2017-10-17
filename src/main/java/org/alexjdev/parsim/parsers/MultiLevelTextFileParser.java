package org.alexjdev.parsim.parsers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.copyOf;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Универсальный разбор текстовых файлов, записи в котором имеют иерархическую структуру
 */
public abstract class MultiLevelTextFileParser extends PlainTextFileParser {

    /**
     * Определяет содержит ли строка данные сущности
     *
     * @param line строка файла
     * @return true - строка относится к самому низкому уровню
     */
    protected abstract boolean isTargetLevel(String line);

    protected List<Map<String, Object>> parseData(Map<String, Integer> columns, List<String> linesWithData) throws Exception {
        String dataFromHighLevel = "";
        List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
        for (String line : linesWithData) {
            if (isNotBlank(line) && isNeedParseLine(line)) {
                if (isTargetLevel(line)) {
                    final String data = appendSep(line) + dataFromHighLevel;
                    result.add(parseLine(columns, data));
                } else {
                    dataFromHighLevel = appendSep(dataFromHighLevel);
                    dataFromHighLevel = appendSep(line) + dataFromHighLevel;
                }
            }
        }
        return copyOf(result);
    }

    /**
     * Добавляет разделитель в конец строки, если его там нет
     *
     * @param data строка
     * @return строка с разделителем на конце
     */
    private String appendSep(String data) {
        if (isNotEmpty(data) && !endsWith(data, separator)) {
            data = data.concat(separator);
        }
        return data;
    }
}

