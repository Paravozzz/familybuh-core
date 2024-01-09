package ru.homebuh.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_info")
public class UserInfoEntity {

    @Id
    @Column(name = "id", columnDefinition = "varchar(36) NOT NULL")
    @Order(1)
    @JsonProperty("id")
    private String id;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_info_currency",
            joinColumns = {@JoinColumn(name = "user_info_id")},
            inverseJoinColumns = {@JoinColumn(name = "currency_id")}
    )
    @JsonIgnore
    private List<CurrencyEntity> currencies = new ArrayList<>(0);

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_info_category",
            joinColumns = {@JoinColumn(name = "user_info_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")}
    )
    @JsonIgnore
    private List<CategoryEntity> categories = new ArrayList<>(0);

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_info_setting",
            joinColumns = {@JoinColumn(name = "user_info_id")},
            inverseJoinColumns = {@JoinColumn(name = "setting_id")}
    )
    @JsonIgnore
    private List<SettingEntity> settings = new ArrayList<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = true)
    @JsonIgnore
    private FamilyEntity family;
}
