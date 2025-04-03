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
    @GeneratedValue
    private Long id;

    @Column(name = "resource")
    private byte[] resource;
}
