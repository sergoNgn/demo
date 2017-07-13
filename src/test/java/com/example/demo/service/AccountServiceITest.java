package com.example.demo.service;

import com.example.demo.DemoApplicationTests;
import com.example.demo.domain.AccountOperationsEntity;
import com.example.demo.domain.Operations;
import com.example.demo.domain.UserAccountEntity;
import com.example.demo.dto.AccountOperationsDto;
import com.example.demo.dto.CountryDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.messages.Messages;
import com.example.demo.repository.AccountRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountServiceITest extends DemoApplicationTests {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    private Long userId = 123L;
    private Long amount = 150L;
    private String name = "Test name";
    private String surname = "Surname";
    private String countryCode = "LV";

    @Test
    public void testDepositForNewUser() throws Exception {
        String result = depositForUser(userId, name, surname, amount, countryCode);
        assertEquals(Messages.success(), result);
        UserAccountEntity userAccountEntity = accountRepository.findOne(userId);
        assertEquals(userId, userAccountEntity.getPersonalId());
        assertEquals(amount, userAccountEntity.getAmount());
        assertEquals(Operations.DEPOSIT, userAccountEntity.getAccountOperationsEntityList().get(0).getOperation());
    }

    @Test
    public void testDepositForExistingUser() throws Exception {
        depositForUser(userId, name, surname, amount, countryCode);
        String result = depositForUser(userId, name, surname, amount, countryCode);
        assertEquals(Messages.success(), result);

        UserAccountEntity userAccountEntity = accountRepository.findOne(userId);
        assertEquals(userId, userAccountEntity.getPersonalId());
        assertEquals(Long.valueOf(300L), userAccountEntity.getAmount());

        assertEquals(Operations.DEPOSIT, userAccountEntity.getAccountOperationsEntityList().get(0).getOperation());
        assertEquals(Operations.DEPOSIT, userAccountEntity.getAccountOperationsEntityList().get(1).getOperation());
    }

    @Test
    public void testSuccessWithdraw() {
        Long withdraw = 100L;
        depositForUser(userId, name, surname, amount, countryCode);
        String result = accountService.withdraw(withdraw, userId, new CountryDto(countryCode));
        UserAccountEntity userAccountEntity = accountRepository.findOne(userId);
        assertEquals(Messages.success(), result);
        assertEquals(Long.valueOf(amount - withdraw), userAccountEntity.getAmount());
        assertTrue(userAccountEntity
                .getAccountOperationsEntityList()
                .stream()
                .map(AccountOperationsEntity::getOperation)
                .collect(toList()).contains(Operations.WITHDRAW));
    }

    @Test
    public void testWithdrawWhenNotEnoughAmount() {
        Long withdraw = 160L;
        depositForUser(userId, name, surname, amount, countryCode);
        String result = accountService.withdraw(withdraw, userId, new CountryDto(countryCode));
        UserAccountEntity userAccountEntity = accountRepository.findOne(userId);
        assertEquals(Messages.notEnoughAmount(), result);
        assertEquals(amount, userAccountEntity.getAmount());
    }

    @Test
    public void testNotValidNumbersOfWithdrawals() {
        Long withdraw = 10L;
        depositForUser(userId, name, surname, amount, countryCode);
        String result = Messages.success();
        for(int i = 5; i >= 0; i--) {
            result = accountService.withdraw(withdraw, userId, new CountryDto(countryCode));
        }
        assertEquals(Messages.countOfOperationsExceeds(), result);
    }

    @Test
    public void getAccountOperations() {
        Long withdraw = 10L;
        Long userId1 = 1234L;
        Long userId2 = 12345L;

        depositForUser(userId, name, surname, amount, countryCode);
        depositForUser(userId1, name, surname, amount, countryCode);
        depositForUser(userId2, name, surname, amount, countryCode);
        accountService.withdraw(withdraw, userId, new CountryDto(countryCode));
        accountService.withdraw(withdraw, userId1, new CountryDto(countryCode));
        accountService.withdraw(withdraw, userId2, new CountryDto(countryCode));

        List<AccountOperationsDto> accountOperationsList = accountService.getAccountOperationsDto();
        assertEquals(6, accountOperationsList.size());
    }

    @Test
    public void getAccountOperationsByUserId() {
        Long withdraw = 10L;
        Long userId1 = 1234L;
        Long userId2 = 12345L;

        depositForUser(userId, name, surname, amount, countryCode);
        depositForUser(userId1, name, surname, amount, countryCode);
        depositForUser(userId2, name, surname, amount, countryCode);
        accountService.withdraw(withdraw, userId1, new CountryDto(countryCode));
        accountService.withdraw(withdraw, userId2, new CountryDto(countryCode));

        List<AccountOperationsDto> accountOperationsByUser = accountService.getAccountOperationsDtoByUserAccountId(userId);
        assertEquals(1, accountOperationsByUser.size());
        assertEquals(surname + " " + name, accountOperationsByUser.get(0).getUserName());
        assertEquals(Operations.DEPOSIT, accountOperationsByUser.get(0).getOperation());
    }

    private String depositForUser(Long userId, String name, String surname, Long amount, String countryCode) {
        return accountService.deposit(new UserAccountDto(userId, name, surname, amount), new CountryDto(countryCode));
    }

}