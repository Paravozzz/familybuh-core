package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {

    /**
     * Цифровой код валюты
     */
    @JsonProperty("id")
    private String id;

    /**
     * Буквенный код валюты
     */
    @JsonProperty("code")
    private String code;

    /**
     * Наименование валюты
     */
    @JsonProperty("name")
    private String name;

    /**
     * Количество знаков дробной части
     */
    @JsonProperty("decimal")
    private Integer decimal;

}
