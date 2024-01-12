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
import java.util.UUID;

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

    @Column(name = "pair_id")
    @Order(6)
    @JsonProperty("pairId")
    private UUID pairId;

    @Column(name = "description")
    @Order(7)
    @JsonProperty("description")
    private String description;

    @Column(name = "date", nullable = false)
    @Order(8)
    @JsonProperty("date")
    private OffsetDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_info_id", nullable = false)
    @Order(9)
    @JsonIgnore
    private UserInfoEntity userInfo;
}
