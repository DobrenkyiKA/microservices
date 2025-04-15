package com.kdob.resourceservice.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "resources")
public class ResourceDao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resources_seq")
    @SequenceGenerator(name = "resources_seq", sequenceName = "resources_id_seq", allocationSize = 1
    )
    private Long id;

    @Column(name = "resource")
    private byte[] resource;
}
