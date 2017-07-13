package com.example.demo.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserAccountEntity extends BaseEntity {
    @Id
    private Long personalId;
    private String name;
    private String surname;
    private Long amount;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<AccountOperationsEntity> accountOperationsEntityList;

    @Override
    public Long getId() {
        return personalId;
    }

    public List<AccountOperationsEntity> getAccountOperationsEntityList() {
        if(accountOperationsEntityList == null) {
            accountOperationsEntityList = new ArrayList<>();
        }
        return accountOperationsEntityList;
    }

    public String getFullName() {
        return surname.concat(" ").concat(name);
    }
}
