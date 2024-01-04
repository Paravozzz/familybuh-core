package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountBalanceUpdate {
    @JsonProperty("accountId")
    private Long accountId;
    @JsonProperty("amount")
    private String amount;
}
