package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "family")
public class FamilyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "family_id_seq")
    @SequenceGenerator(name = "family_id_seq", sequenceName = "family_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    @Order(1)
    @JsonProperty("id")
    private Long id;

}
