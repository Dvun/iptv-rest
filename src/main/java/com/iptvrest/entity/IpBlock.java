package com.iptvrest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
public class IpBlock {

    @Id
    public String ipBlock;
    public String providerCode;

}
