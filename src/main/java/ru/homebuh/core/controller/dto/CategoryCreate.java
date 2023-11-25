package ru.homebuh.core.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryCreate {
    @JsonProperty("name")
    private String name;

    @JsonProperty("isIncome")
    private boolean isIncome;
}
