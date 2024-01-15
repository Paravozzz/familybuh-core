package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferCreate {
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("expenseAccountId")
    private Long expenseAccountId;

    @JsonProperty("incomeAccountId")
    private Long incomeAccountId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("date")
    private OffsetDateTime date;
}
