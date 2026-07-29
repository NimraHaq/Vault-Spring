package com.vault.service.impl;

import com.vault.dao.TransactionDao;
import com.vault.dto.TransactionDto;
import com.vault.entity.Card;
import com.vault.entity.Transaction;
import com.vault.enums.ServiceIds;
import com.vault.service.CardService;
import com.vault.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl extends TransactionService {
    TransactionDao transactionDao;

    public TransactionServiceImpl(TransactionDao transactionDao){
        this.transactionDao = transactionDao;
    }

    @Override
    public List<TransactionDto> getAllCardsTransactions(List<Card> cards) {
        return transactionDao.findTransactionByCardIn(cards).stream()
                .map(TransactionServiceImpl::transactionEntityToDtoMapping).collect(Collectors.toList());
    }

    @Override
    public TransactionDto addTransaction(TransactionDto transaction, Card card) {
        Transaction transactionEntity = transactionDtoToEntityMapping(transaction);
        transactionEntity.setCard(card);
        return transactionEntityToDtoMapping(transactionDao.save(transactionEntity));
    }

    @Override
    public TransactionDto createTransaction(Card card, BigDecimal amount, ServiceIds serviceId) {
        TransactionDto transactionDto = TransactionDto.builder().cardNo(card.getCardNo())
                .serviceId(serviceId.getServiceId())
                .amountProcessed(Objects.isNull(amount) ? BigDecimal.ZERO : amount)
                .serviceCharges(BigDecimal.ZERO)
                .build();
        return addTransaction(transactionDto, card);
    }

    protected static Transaction transactionDtoToEntityMapping(TransactionDto transactionDto){
        Transaction transaction = Transaction.builder().transId(transactionDto.getTransId())
                .serviceId(transactionDto.getServiceId()).serviceCharges(transactionDto.getServiceCharges())
                .amountProcessed(transactionDto.getAmountProcessed())
                .card(Card.builder().cardNo(transactionDto.getCardNo()).build())
                .createdOn(transactionDto.getCreatedOn()).lastUpdatedOn(transactionDto.getLastUpdatedOn())
                .build();
        return transaction;
    }

    protected static TransactionDto transactionEntityToDtoMapping(Transaction transaction){
        TransactionDto transactionDto = TransactionDto.builder().transId(transaction.getTransId())
                .serviceId(transaction.getServiceId()).serviceCharges(transaction.getServiceCharges())
                .amountProcessed(transaction.getAmountProcessed())
                .cardNo(transaction.getCard().getCardNo())
                .createdOn(transaction.getCreatedOn()).lastUpdatedOn(transaction.getLastUpdatedOn())
                .build();
        return transactionDto;
    }
}
