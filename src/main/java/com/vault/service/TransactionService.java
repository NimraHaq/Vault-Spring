package com.vault.service;

import com.vault.dto.TransactionDto;
import com.vault.entity.Card;
import com.vault.enums.ServiceIds;

import java.math.BigDecimal;
import java.util.List;

public abstract class TransactionService {

    abstract public List<TransactionDto> getAllCardsTransactions(List<Card> card);

    abstract public TransactionDto addTransaction(TransactionDto transaction, Card card);

    //shared by every service that has to log a transaction against a card
    abstract public TransactionDto createTransaction(Card card, BigDecimal amount, ServiceIds serviceId);
}
