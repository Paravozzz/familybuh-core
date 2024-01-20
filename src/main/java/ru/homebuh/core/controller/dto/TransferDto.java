package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferDto {
    @JsonProperty("id")
    private Long transferId;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("expenseAccountName")
    private String expenseAccountName;

    @JsonProperty("incomeAccountName")
    private String incomeAccountName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("date")
    private OffsetDateTime date;
}
