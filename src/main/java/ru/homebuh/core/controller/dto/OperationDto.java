package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.homebuh.core.domain.enums.OperationTypeEnum;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OperationDto {

    @JsonProperty("operationId")
    private Long operationId;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("currency")
    private CurrencyDto currency;

    @JsonProperty("accountId")
    private Long accountId;

    @JsonProperty("accountName")
    private String accountName;

    @JsonProperty("operationType")
    private OperationTypeEnum operationType;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("category")
    private String category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("date")
    private OffsetDateTime date;
}
