package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exchange")
public class ExchangeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exchange_id_seq")
    @SequenceGenerator(name = "exchange_id_seq", sequenceName = "exchange_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    @Order(1)
    @EqualsAndHashCode.Exclude
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expense_operation_id", nullable = false)
    @Order(2)
    @JsonProperty("expense")
    private OperationEntity expense;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "income_operation_id", nullable = false)
    @Order(3)
    @JsonProperty("income")
    private OperationEntity income;

    @Column(name = "description")
    @Order(4)
    @JsonProperty("description")
    private String description;

    @Column(name = "date", nullable = false)
    @Order(5)
    @JsonProperty("date")
    private OffsetDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_info_id", nullable = false)
    @Order(6)
    @JsonIgnore
    private UserInfoEntity userInfo;
}
