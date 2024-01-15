package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.Order;

@Entity
@Table(name = "setting")
@Getter
@Setter
@NoArgsConstructor
public class SettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "setting_id_seq")
    @SequenceGenerator(name = "setting_id_seq", sequenceName = "setting_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    @Order(1)
    @EqualsAndHashCode.Exclude
    @JsonProperty("id")
    private Long id;

    @Column(name = "name", columnDefinition = "varchar(255)", nullable = false)
    @Order(2)
    @JsonProperty("name")
    private String name;

    @Column(name = "value", columnDefinition = "varchar(255)")
    @Order(3)
    @JsonProperty("value")
    private String value = "";

}
