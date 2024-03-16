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
public class ExchangeDto {
    @JsonProperty("exchangeId")
    private Long exchangeId;
    @JsonProperty("expenseAmount")
    private String expenseAmount;

    @JsonProperty("incomeAmount")
    private String incomeAmount;

    @JsonProperty("accountName")
    private String accountName;

    @JsonProperty("expenseCurrencyCode")
    private String expenseCurrencyCode;

    @JsonProperty("incomeCurrencyCode")
    private String incomeCurrencyCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("date")
    private OffsetDateTime date;
}
