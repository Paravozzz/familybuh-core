package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OperationCreate {
    @JsonProperty("amount")
    private String amount;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("accountId")
    private Long accountId;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("description")
    private Long description;

    @JsonProperty("time")
    private Timestamp time;
}
