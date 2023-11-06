package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.homebuh.core.controller.dto.CurrencyCreate;
import ru.homebuh.core.domain.CurrencyEntity;
import ru.homebuh.core.mapper.CurrencyMapper;
import ru.homebuh.core.repository.CurrencyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Transactional
    public CurrencyEntity create(CurrencyCreate currencyCreate) {
        return currencyRepository.save(currencyMapper.map(currencyCreate));
    }

    public List<CurrencyEntity> findAllByUserId(String id) {
        return currencyRepository.findAllByUserId(id);
    }

    public List<CurrencyEntity> findAll() {
        return currencyRepository.findAll();
    }
}
