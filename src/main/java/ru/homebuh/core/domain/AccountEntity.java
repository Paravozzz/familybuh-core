package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.Order;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", nullable = false)
    @Order(1)
    @EqualsAndHashCode.Exclude
    @JsonProperty("id")
    private Long id;

    @Column(name="name", columnDefinition = "varchar(255)", nullable = false)
    @Order(2)
    @JsonProperty("name")
    private String name;

    @ManyToOne
    @JoinColumn(name="currency_id", nullable = false)
    @Order(3)
    @JsonProperty("currency")
    private CurrencyEntity currency;

    @Column(name="description", columnDefinition = "varchar(255)")
    @Order(4)
    @JsonProperty("description")
    private String description = "";

    @ManyToOne
    @JoinColumn(name="user_info_id", nullable = false)
    @Order(5)
    @JsonIgnore
    private UserInfoEntity userInfo;
}
