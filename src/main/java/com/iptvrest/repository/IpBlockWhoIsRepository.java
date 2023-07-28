package com.iptvrest.repository;

import com.iptvrest.entity.IpBlockWhoIs;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpBlockWhoIsRepository extends JpaRepository<IpBlockWhoIs, String> {

    List<IpBlockWhoIs> findAllByProviderCode(String code);
    void deleteAllByProviderCode(String code);

}
