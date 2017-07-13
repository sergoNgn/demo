package com.example.demo.dto;

import com.example.demo.domain.Operations;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AccountOperationsDto implements Serializable {
    private String userName;
    private String countryCode;
    private Operations operation;
    private String operationDate;
}
