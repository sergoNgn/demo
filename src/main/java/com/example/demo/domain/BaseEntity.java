package com.example.demo.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public abstract class BaseEntity implements Serializable{
    public abstract Long getId();
}
