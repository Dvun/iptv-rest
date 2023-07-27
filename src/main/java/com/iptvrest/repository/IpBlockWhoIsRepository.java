package com.iptvrest.repository;

import com.iptvrest.entity.IpBlockWhoIs;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpBlockWhoIsRepository extends JpaRepository<IpBlockWhoIs, String> {
}
