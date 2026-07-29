package com.vault.service;

import com.vault.dto.CardDto;
import com.vault.dto.CustomerDto;
import com.vault.dto.TransactionDto;
import com.vault.dto.UserDto;
import com.vault.entity.Card;
import com.vault.entity.Customer;

import java.math.BigDecimal;
import java.util.List;

public abstract class CardService {
    abstract public CardDto addCard(UserDto user);
    abstract public void deleteByCardNo(long cardNo);

    abstract public CardDto updateCard(CardDto card);

    abstract public List<CardDto> getCardsByChId(Customer customer);

    //null when no such card exists in the DB
    abstract public CardDto getCardByCardNo(long cardNo);

    abstract public List<CardDto> getAllCards();

    abstract public CardDto blockCard(long cardNo);

    abstract public List<TransactionDto> getAllCardsTransactions(List<Long> cardNumbers);
}
