package com.iptvrest.repository;

import com.iptvrest.entity.UdpBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UdpBlockRepository extends JpaRepository<UdpBlock, String> {
}
