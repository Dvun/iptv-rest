package com.iptvrest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Provider {

    @Id
    public String providerCode;
    public String mCast;
    public String providerName;
    public String city;
    public String country;
    public String ports;
    public Boolean udpxy;
    public Boolean msDlt;
    public double timeOut;
    public double timer;
    public Integer minCount;

}
