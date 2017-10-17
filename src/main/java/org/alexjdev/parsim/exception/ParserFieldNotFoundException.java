package org.alexjdev.parsim.exception;

/**
 * Исключение, генерируемое в случае, если парсер при разборе не находит необходимых полей
 */
public class ParserFieldNotFoundException extends RuntimeException {

    public ParserFieldNotFoundException(String message) {
        super(message);
    }
}
