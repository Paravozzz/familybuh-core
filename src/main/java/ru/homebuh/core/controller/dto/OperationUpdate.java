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
public class OperationUpdate {
    @JsonProperty("amount")
    private String amount;

    @JsonProperty("accountId")
    private Long accountId;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("date")
    private OffsetDateTime date;

}
