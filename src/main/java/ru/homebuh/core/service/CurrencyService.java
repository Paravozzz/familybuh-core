package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.repository.CurrencyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public List<CurrencyEntity> findAllByUserId(String id) {
        return currencyRepository.findAllByUserId(id);
    }

    public List<CurrencyEntity> findAll() {
        return currencyRepository.findAll();
    }
}
