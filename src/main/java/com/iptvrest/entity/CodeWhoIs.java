package com.iptvrest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CodeWhoIs {

    @Id
    public String providerCode;
    public String providerName;

}
