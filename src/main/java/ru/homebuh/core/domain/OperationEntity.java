package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operation")
public class OperationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Order(1)
    @EqualsAndHashCode.Exclude
    @JsonProperty("id")
    private Long id;

    @Column(name = "amount", nullable = false)
    @Order(2)
    @JsonProperty("amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "debit_account_id", nullable = false)
    @Order(3)
    @JsonProperty("debitAccount")
    private AccountEntity debitAccount;

    @ManyToOne
    @JoinColumn(name = "credit_account_id", nullable = false)
    @Order(4)
    @JsonProperty("creditAccount")
    private AccountEntity creditAccount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Order(5)
    @JsonProperty("category")
    private CategoryEntity category;

    @Column(name = "description")
    @Order(6)
    @JsonProperty("description")
    private String description;

    @Column(name = "date", nullable = false)
    @Order(7)
    @JsonProperty("date")
    private OffsetDateTime date;
}
