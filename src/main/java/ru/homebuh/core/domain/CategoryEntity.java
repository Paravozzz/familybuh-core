package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.core.annotation.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Order(1)
    @EqualsAndHashCode.Exclude
    @JsonProperty("id")
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(255)", nullable = false)
    @Order(2)
    @JsonProperty("name")
    private String name;

    @Column(name = "is_income", nullable = false)
    @Order(4)
    @JsonProperty("isIncome")
    private boolean isIncome;
}
