package com.vault.service;

import com.vault.dto.CardDto;
import com.vault.dto.CustomerDto;
import com.vault.dto.TransactionDto;
import com.vault.dto.UserDto;

import java.util.List;

public abstract class CustomerService {
    abstract public UserDto addCustomer(UserDto userDto);
    abstract public CustomerDto getCustomerByChId(int chId);
    abstract public List<CardDto> getCardByChId(int chId);
}

