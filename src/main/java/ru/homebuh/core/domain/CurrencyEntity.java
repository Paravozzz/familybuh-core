package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "currency")
public class CurrencyEntity {

    /**
     * Цифровой код валюты
     */
    @Id
    @Column(name = "id", columnDefinition = "varchar(3)", nullable = false)
    @JsonProperty("id")
    @Order(1)
    private String id;

    /**
     * Буквенный код валюты
     */
    @Column(name = "code", columnDefinition = "varchar(3)", nullable = false, unique = true)
    @JsonProperty("code")
    @Order(2)
    private String code;

    /**
     * Наименование валюты
     */
    @Column(name = "name", columnDefinition = "varchar(255)", nullable = false)
    @JsonProperty("name")
    @Order(3)
    private String name;

}
