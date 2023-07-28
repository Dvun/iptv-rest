package com.iptvrest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UdpBlock {

    @Id
    public String providerCode;
    public String block;
    public String blockBegin;
    public String blockEnd;
    public String ports;

}
