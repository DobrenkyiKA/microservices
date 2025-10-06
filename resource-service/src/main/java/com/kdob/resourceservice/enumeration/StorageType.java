package com.kdob.resourceservice.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StorageType {

    STAGING("STAGING"),
    PERMANENT("PERMANENT");

    private final String storageType;
}