package com.example.demo.controllers;

import com.example.demo.dto.AccountOperationsDto;
import com.example.demo.dto.CountryDto;
import com.example.demo.dto.UserAccountDto;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String COUNTRY_IP_RESOLVER_ADDRESS = "http://ip-api.com/json/";
    private static final String DEFAULT_COUNTRY_CODE = "LV";

    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public String deposit(HttpServletRequest request, @RequestBody UserAccountDto userAccountDto) {
        return accountService.deposit(userAccountDto, getCountryDto(request.getRemoteAddr()));
    }

    @RequestMapping(value = "/withdraw", params = {"amount", "personalId"})
    public String withdraw(HttpServletRequest request, Long amount, Long personalId) {
        return accountService.withdraw(amount, personalId, getCountryDto(request.getRemoteAddr()));
    }

    @RequestMapping(value = "/account/operations")
    public List<AccountOperationsDto> getAccountOperations() {
        return accountService.getAccountOperationsDto();
    }

    @RequestMapping(value = "/account/operations", params = {"personalId"})
    public List<AccountOperationsDto> getAccountOperationsByUserId(@RequestParam() Long personalId) {
        return accountService.getAccountOperationsDtoByUserAccountId(personalId);
    }

    private CountryDto getCountryDto(String ipAddress) {
        CountryDto countryDto = REST_TEMPLATE.getForObject(COUNTRY_IP_RESOLVER_ADDRESS.concat(ipAddress), CountryDto.class);
        return countryDto.getCountryCode() == null ? new CountryDto(DEFAULT_COUNTRY_CODE) : countryDto;
    }
}