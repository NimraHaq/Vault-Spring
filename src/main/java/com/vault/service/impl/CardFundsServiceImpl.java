package com.vault.service.impl;

import com.vault.dao.CardFundsDao;
import com.vault.dto.CardFundsDto;
import com.vault.entity.Card;
import com.vault.entity.CardFunds;
import com.vault.enums.ServiceIds;
import com.vault.exceptions.CardNotFoundException;
import com.vault.service.CardFundsService;
import com.vault.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CardFundsServiceImpl extends CardFundsService {
    private CardFundsDao cardFundsDao;
    private TransactionService transactionService;

    public CardFundsServiceImpl(CardFundsDao cardFundsDao, TransactionService transactionService) {
        this.cardFundsDao = cardFundsDao;
        this.transactionService = transactionService;
    }

    //a freshly generated card starts with zero balances
    @Override
    public CardFundsDto addCardFunds(Card card) {
        CardFunds cardFunds = CardFunds.builder().card(card).build();
        return mapToCardFundsDto(cardFundsDao.save(cardFunds));
    }

    @Override
    public CardFundsDto getCardFundsByCardNo(Long cardNo) {
        CardFunds cardFunds = cardFundsDao.findByCardCardNo(cardNo);
        return Objects.isNull(cardFunds) ? null : mapToCardFundsDto(cardFunds);
    }

    @Override
    @Transactional
    public CardFundsDto depositFundsInCard(Long cardNo, BigDecimal amount) {
        CardFunds cardFunds = updateCardFunds(cardNo, amount);
        transactionService.createTransaction(cardFunds.getCard(), amount, ServiceIds.DEPOSIT_FUNDS);
        return mapToCardFundsDto(cardFunds);
    }

    @Override
    @Transactional
    public CardFundsDto withdrawFundsFromCard(Long cardNo, BigDecimal amount) {
        CardFunds cardFunds = updateCardFunds(cardNo, amount.negate());
        transactionService.createTransaction(cardFunds.getCard(), amount, ServiceIds.WITHDRAW_FUNDS);
        return mapToCardFundsDto(cardFunds);
    }

    @Override
    @Transactional
    public List<CardFundsDto> fundsTransfer(Long fromCardNo, Long toCardNo, BigDecimal amount) {
        List<CardFundsDto> fundsTransferList = new ArrayList<>();
        fundsTransferList.add(0, withdrawFundsFromCard(fromCardNo, amount));
        fundsTransferList.add(1, depositFundsInCard(toCardNo, amount));
        return fundsTransferList;
    }

    @Override
    protected CardFunds updateCardFunds(Long cardNo, BigDecimal amount) {
        CardFunds cardFunds = cardFundsDao.findByCardCardNo(cardNo);
        if(Objects.isNull(cardFunds)){
            throw new CardNotFoundException("Card funds not found for card " + cardNo);
        }
        cardFunds.setLedgerBalance(cardFunds.getLedgerBalance().add(amount));
        cardFunds.setCardBalance(cardFunds.getCardBalance().add(amount));
        return cardFunds;
    }

    private CardFundsDto mapToCardFundsDto(CardFunds cardFunds){
        CardFundsDto cardFundsDto = CardFundsDto.builder().id(cardFunds.getId())
                .cardNo(Objects.nonNull(cardFunds.getCard()) ? cardFunds.getCard().getCardNo() : null)
                .ledgerBalance(cardFunds.getLedgerBalance()).cardBalance(cardFunds.getCardBalance())
                .cardBalanceOnHold(cardFunds.getCardBalanceOnHold())
                .createdOn(cardFunds.getCreatedOn()).updationTimeStamp(cardFunds.getUpdationTimeStamp())
                .build();
        return cardFundsDto;
    }
}
