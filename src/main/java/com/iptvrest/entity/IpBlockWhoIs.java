package com.iptvrest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class IpBlockWhoIs {

    @Id
    public String ipBlock;
    public String providerCode;

}
