package com.example.demo.repository;

import com.example.demo.DemoApplicationTests;
import com.example.demo.domain.AccountOperationsEntity;
import com.example.demo.domain.Operations;
import com.example.demo.domain.UserAccountEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class AccountOperationsRepositoryITest extends DemoApplicationTests {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountOperationsRepository accountOperationsRepository;

    @Test
    public void testFindOperationsByUserId() throws Exception {
        Long userId = 123L;
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.setPersonalId(userId);
        userAccountEntity.setAmount(1000L);

        createAccountOperation(userAccountEntity, "LV", Operations.DEPOSIT);
        accountRepository.save(userAccountEntity);

        List<AccountOperationsEntity> accountOperationsEntityList = accountOperationsRepository.findByUserId(userId);
        assertNotNull(accountOperationsEntityList);
        assertEquals(1, accountOperationsEntityList.size());
        assertEquals(Operations.DEPOSIT, accountOperationsEntityList.get(0).getOperation());
    }

    @Test
    public void testValidOperationsCountPerTime() throws Exception {
        Long userId = 123L;
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.setPersonalId(userId);
        userAccountEntity.setAmount(1000L);
        createAccountOperation(userAccountEntity, "UA", Operations.DEPOSIT);
        accountRepository.save(userAccountEntity);
        for(int i = 4; i > 0; i--) {
            accountOperationsRepository.save(
                    createAccountOperation(userAccountEntity, "UA", Operations.WITHDRAW));
        }
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.MINUTE, -2);
        Date startDate = calendar.getTime();
        assertFalse(
                accountOperationsRepository.isValidOperationsCountPerTime(userId, startDate, endDate, "UA"));
    }

    @Test
    public void testNotValidOperationsCountPerTime() throws Exception {
        Long userId = 123L;
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.setPersonalId(userId);
        userAccountEntity.setAmount(1000L);
        createAccountOperation(userAccountEntity, "UA", Operations.DEPOSIT);
        accountRepository.save(userAccountEntity);
        for(int i = 5; i > 0; i--) {
            accountOperationsRepository.save(
                    createAccountOperation(userAccountEntity, "UA", Operations.WITHDRAW));
        }
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.MINUTE, -2);
        Date startDate = calendar.getTime();
        assertFalse(
                accountOperationsRepository.isValidOperationsCountPerTime(userId, startDate, endDate, "UA"));
    }

    private AccountOperationsEntity createAccountOperation(UserAccountEntity userAccountEntity, String countryCode, Operations operation) {
        AccountOperationsEntity accountOperationsEntity = new AccountOperationsEntity();
        accountOperationsEntity.setAccount(userAccountEntity);
        accountOperationsEntity.setOperation(operation);
        accountOperationsEntity.setOperationTime(new Date());
        accountOperationsEntity.setCountryCode(countryCode);
        userAccountEntity.getAccountOperationsEntityList().add(accountOperationsEntity);
        return accountOperationsEntity;
    }

}