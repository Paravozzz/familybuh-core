package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MasterAccountCreate {

    /**
     * Буквенный код валюты
     */
    @JsonProperty("currencyCode")
    private String currencyCode;

    /**
     * Идентификатор пользователя
     */
    @JsonProperty("userInfoId")
    private String userInfoId;

}
