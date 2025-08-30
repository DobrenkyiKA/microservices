package com.kdob.resourceservice.dao;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "resource_info")
public class ResourceInfoDao {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resource_info_seq")
    @SequenceGenerator(name = "resource_info_seq", sequenceName = "resource_info_id_seq", allocationSize = 1)
    private Long id;

    @Column
    private String key;
    @Column
    private String bucket;
}
