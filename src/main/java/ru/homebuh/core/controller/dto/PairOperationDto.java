package ru.homebuh.core.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.homebuh.core.domain.OperationEntity;

@AllArgsConstructor
@Getter
@Setter
public class PairOperationDto {
    private OperationEntity expense;
    private OperationEntity income;
}
