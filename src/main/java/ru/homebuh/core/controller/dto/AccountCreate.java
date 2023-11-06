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
public class AccountCreate {

    @JsonProperty("name")
    private String name;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("userInfoId")
    private String userInfoId;

}
