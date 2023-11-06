package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operation")
public class OperationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Order(1)
    @EqualsAndHashCode.Exclude
    @JsonProperty("id")
    private Long id;

    @Column(name = "debit", nullable = false)
    @Order(2)
    @JsonProperty("debit")
    private BigDecimal debit;

    @Column(name = "credit", nullable = false)
    @Order(3)
    @JsonProperty("credit")
    private BigDecimal credit;

    @ManyToOne
    @JoinColumn(name = "debit_account_id", nullable = false)
    @Order(4)
    @JsonProperty("debitAccount")
    private AccountEntity debitAccount;

    @ManyToOne
    @JoinColumn(name = "credit_account_id", nullable = false)
    @Order(5)
    @JsonProperty("creditAccount")
    private AccountEntity creditAccount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Order(6)
    @JsonProperty("category")
    private CategoryEntity category;

    @Column(name = "time", nullable = false)
    @Order(7)
    @JsonProperty("time")
    private Timestamp time;
}
