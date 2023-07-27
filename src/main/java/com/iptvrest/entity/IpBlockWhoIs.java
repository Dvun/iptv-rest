package com.iptvrest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class IpBlock_WhoIs {

    @Id
    public String ipBlock;
    public String providerCode;

}
