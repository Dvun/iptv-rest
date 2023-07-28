package com.iptvrest.repository;

import com.iptvrest.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {

    Provider findByProviderCode(String code);
    void deleteByProviderCode(String code);

}
