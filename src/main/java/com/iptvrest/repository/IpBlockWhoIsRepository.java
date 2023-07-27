package com.iptvrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpBlock_WhoIs extends JpaRepository<IpBlock_WhoIs, String> {
}
