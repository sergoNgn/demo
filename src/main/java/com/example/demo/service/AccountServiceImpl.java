package com.example.demo.service;

import com.example.demo.domain.AccountOperationsEntity;
import com.example.demo.domain.Operations;
import com.example.demo.domain.UserAccountEntity;
import com.example.demo.dto.AccountOperationsDto;
import com.example.demo.dto.CountryDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.messages.Messages;
import com.example.demo.repository.AccountOperationsRepository;
import com.example.demo.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountOperationsRepository accountOperationsRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final int MINUTES_FOR_WITHDRAWAL_NUMBERS = 2;

    @PostConstruct
    public void postConstruct() {
        configureMapping();
    }

    @Override
    public String deposit(UserAccountDto userAccountDto, CountryDto countryDto) {
        if(accountRepository.exists(userAccountDto.getPersonalId())) {
            UserAccountEntity userAccountEntity = accountRepository.findOne(userAccountDto.getPersonalId());
            createAccountOperation(userAccountEntity, Operations.DEPOSIT, countryDto.getCountryCode());
            userAccountEntity.setAmount(userAccountEntity.getAmount() + userAccountDto.getAmount());
        } else {
            UserAccountEntity userAccountEntity = modelMapper.map(userAccountDto, UserAccountEntity.class);
            createAccountOperation(userAccountEntity, Operations.DEPOSIT, countryDto.getCountryCode());
            accountRepository.save(userAccountEntity);
        }
        return Messages.success();
    }

    @Override
    public String withdraw(Long amount, Long personalId, CountryDto countryDto) {
        UserAccountEntity userAccountEntity = accountRepository.findOne(personalId);
        if(userAccountEntity == null) {
            return Messages.userNotFound(personalId);
        }
        if(userAccountEntity.getAmount() < amount) {
            return Messages.notEnoughAmount();
        }

        Date toDate = new Date();
        Date startDate = getStartDate(toDate);

        if(!accountOperationsRepository.isValidOperationsCountPerTime(personalId,
                                                                      startDate,
                                                                      toDate,
                                                                      countryDto.getCountryCode())) {
            return Messages.countOfOperationsExceeds();
        }

        createAccountOperation(userAccountEntity, Operations.WITHDRAW, countryDto.getCountryCode());
        userAccountEntity.setAmount(userAccountEntity.getAmount() - amount);

        return Messages.success();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountOperationsDto> getAccountOperationsDto() {
        List<AccountOperationsEntity> accountOperationsEntities = accountOperationsRepository.findAll();
        return accountOperationsEntities
               .stream()
               .map(e -> modelMapper.map(e, AccountOperationsDto.class))
               .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountOperationsDto> getAccountOperationsDtoByUserAccountId(Long personalId) {
        List<AccountOperationsEntity> accountOperationsEntities = accountOperationsRepository.findByUserId(personalId);
        return accountOperationsEntities
               .stream()
               .map(e -> modelMapper.map(e, AccountOperationsDto.class))
               .collect(Collectors.toList());
    }

    private void configureMapping() {
        PropertyMap<AccountOperationsEntity, AccountOperationsDto> orderMap =
                new PropertyMap<AccountOperationsEntity, AccountOperationsDto>() {
                    protected void configure() {
                        map()
                        .setUserName(source.getAccount().getFullName());
                        map()
                        .setCountryCode(source.getCountryCode());
                        map()
                        .setOperationDate(source.getOperationTime());
                    }
                };
        modelMapper.addMappings(orderMap);
    }

    private static void createAccountOperation(UserAccountEntity userAccountEntity, Operations operation, String countryCode) {
        AccountOperationsEntity accountOperationsEntity = new AccountOperationsEntity();
        accountOperationsEntity.setOperation(operation);
        accountOperationsEntity.setCountryCode(countryCode);
        accountOperationsEntity.setOperationTime(new Date());
        accountOperationsEntity.setAccount(userAccountEntity);
        userAccountEntity.getAccountOperationsEntityList().add(accountOperationsEntity);
    }

    private static Date getStartDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -MINUTES_FOR_WITHDRAWAL_NUMBERS);
        return cal.getTime();
    }
}
