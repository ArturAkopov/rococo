package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.repository.CountryRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryDatabaseService {

    private final CountryRepository countryRepository;

    public Page<CountryEntity> getAllCountries(@Nonnull PageRequest pageable) {
        log.debug("Запрос всех стран. Страница: {}, Размер: {}", pageable.getPageNumber(), pageable.getPageSize());
        return countryRepository.findAll(pageable);
    }

    public Page<CountryEntity> findByNameContainingIgnoreCase(@Nonnull String name, @Nonnull PageRequest pageable) {
        log.debug("Поиск стран по имени. Имя: {}, Страница: {}, Размер: {}",
                name, pageable.getPageNumber(), pageable.getPageSize());
        return countryRepository.findByNameIgnoreCase(name, pageable);
    }
}
