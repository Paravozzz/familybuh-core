package ru.homebuh.core.controller.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInfoCreate {

    private String id;
    private Long familyId;

}
