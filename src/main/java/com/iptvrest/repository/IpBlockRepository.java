package com.iptvrest.repository;

import com.iptvrest.entity.IpBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpBlockRepository extends JpaRepository<IpBlock, String> {

    void deleteAllByProviderCode(String code);

}
