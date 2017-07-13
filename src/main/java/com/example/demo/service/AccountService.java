package com.example.demo.service;

import com.example.demo.dto.AccountOperationsDto;
import com.example.demo.dto.CountryDto;
import com.example.demo.dto.UserAccountDto;

import java.util.List;

public interface AccountService {
    String deposit(UserAccountDto userAccountDto, CountryDto countryDto);
    String withdraw(Long amount, Long personalId, CountryDto countryDto);
    List<AccountOperationsDto> getAccountOperationsDto();
    List<AccountOperationsDto> getAccountOperationsDtoByUserAccountId(Long personalId);
}
