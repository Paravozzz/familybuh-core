package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.homebuh.core.domain.enums.OperationType;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OperationDto {

    @JsonProperty("operationId")
    private Long operationId;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("accountId")
    private Long accountId;

    @JsonProperty("operationType")
    private OperationType operationType;

    @JsonProperty("category")
    private String category;

    @JsonProperty("categoryUuid")
    private UUID categoryUuid;

    @JsonProperty("description")
    private String description;

    @JsonProperty("date")
    private OffsetDateTime date;
}
