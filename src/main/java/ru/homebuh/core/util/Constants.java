package ru.homebuh.core.util;

import java.util.List;

public class Constants {
    public static final String NOT_FOUND_BY_PARAM_TEMPLATE = "{0} not found by {1}({2})";
    public static final String DUPLICATE_BY_PARAM_TEMPLATE = "{0} with {1}({2}) already exists";

    public static final String CATEGORY = "Category";
    public static final String USER = "User";
    public static final String CURRENCY = "Currency";

    public static final List<String> INITIAL_EXPENSE_CATEGORIES = List.of("Продукты", "Транспорт", "Развлечения");
    public static final List<String> INITIAL_INCOME_CATEGORIES = List.of("Зарплата", "Подработка", "Дивиденды");
    public static final List<String> INITIAL_ACCOUNTS = List.of("Наличные", "Банковская карта", "Вклад в банке");

    private Constants() {
    }
}
