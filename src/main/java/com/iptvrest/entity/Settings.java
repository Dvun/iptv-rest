package com.iptvrest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String botName;
    private Integer sleep;
    private Integer flag;
    private String ftpFolder;
    private String ftpHost;
    private String ftpLogin;
    private String ftpPass;
    private String fileName;
    private String localhost;
    private String version;

}
