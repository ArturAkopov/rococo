package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.repository.CountryRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryDatabaseService {

    private final CountryRepository countryRepository;

    public Page<CountryEntity> getAllCountries(@Nonnull PageRequest pageable) {
        return countryRepository.findAll(pageable);
    }

}
