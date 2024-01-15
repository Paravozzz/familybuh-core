package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;
import ru.homebuh.core.domain.enums.OperationTypeEnum;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operation")
public class OperationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_id_seq")
    @SequenceGenerator(name = "operation_id_seq", sequenceName = "operation_id_seq", allocationSize = 1)
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
    @JoinColumn(name = "account_id", nullable = false)
    @Order(3)
    @JsonProperty("account")
    private AccountEntity account;

    @Order(4)
    @Column(name = "operation_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @JsonProperty("operationType")
    private OperationTypeEnum operationType;

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

    @ManyToOne
    @JoinColumn(name = "user_info_id", nullable = false)
    @Order(8)
    @JsonIgnore
    private UserInfoEntity userInfo;
}
