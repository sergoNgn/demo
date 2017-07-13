package com.example.demo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.Date;

@Getter
@Setter
@Entity
public class AccountOperationsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String countryCode;
    @Enumerated(EnumType.STRING)
    private Operations operation;
    @ManyToOne
    private UserAccountEntity account;
    @Temporal(TemporalType.TIMESTAMP)
    private Date operationTime;

    public String getOperationTime() {
        return DateFormat.getDateTimeInstance().format(operationTime);
    }
}
