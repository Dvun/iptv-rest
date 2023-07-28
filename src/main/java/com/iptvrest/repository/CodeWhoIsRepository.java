package com.iptvrest.repository;

import com.iptvrest.entity.CodeWhoIs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeWhoIsRepository extends JpaRepository<CodeWhoIs, String> {

    CodeWhoIs findByProviderCode(String code);

    void deleteByProviderCode(String code);

}
