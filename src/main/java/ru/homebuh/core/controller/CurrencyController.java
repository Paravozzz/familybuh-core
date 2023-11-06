package ru.homebuh.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.service.CurrencyService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("currencies")
    Collection<CurrencyEntity> getAll() {
        return currencyService.findAll();
    }
}
