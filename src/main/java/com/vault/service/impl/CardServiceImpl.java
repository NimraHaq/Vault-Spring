package com.vault.service.impl;

import com.vault.dao.CardDao;
import com.vault.dto.CardDto;
import com.vault.dto.TransactionDto;
import com.vault.dto.UserDto;
import com.vault.entity.Card;
import com.vault.entity.Customer;
import com.vault.enums.CardStatus;
import com.vault.enums.ServiceIds;
import com.vault.exceptions.CardCouldNotBeGeneratedException;
import com.vault.exceptions.CardNotFoundException;
import com.vault.service.CardFundsService;
import com.vault.service.CardService;
import com.vault.service.TransactionService;
import com.vault.utils.Constants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl extends CardService {
    private CardDao cardDao;
    private TransactionService transactionService;
    private CardFundsService cardFundsService;

    public CardServiceImpl(CardDao cardDao, TransactionService transactionService, CardFundsService cardFundsService) {
        this.cardDao = cardDao;
        this.transactionService = transactionService;
        this.cardFundsService = cardFundsService;
    }
    @Override
    public CardDto addCard(UserDto user) {
        CardDto card = generateCard(user);
        int attempts = 0;
        while (attempts < 5) {
            try {
                card.setCardNo(generateRandomNumber());
                Optional<Card> cardEntity = Optional.ofNullable(cardDao.saveAndFlush(mapToCardEntity(card)));
                cardEntity.ifPresent(c -> {
                    cardFundsService.addCardFunds(c);   // funds row with zero balances
                    transactionService.createTransaction(c, BigDecimal.ZERO, ServiceIds.ADD_CARD);
                });
                return cardEntity.isPresent() ? mapToCardDto(cardEntity.get()) : null;
            } catch (DataIntegrityViolationException e) {
                attempts++;   // if not a unique card number → try a new number
            }
        }
        throw new CardCouldNotBeGeneratedException("Could not generate a unique card number");
    }

    private CardDto generateCard(UserDto user){
        CardDto cardDto = new CardDto();
        cardDto.setIsMainCard(Constants.OPTION_YES);
        cardDto.setCategory(Constants.CARD_CATEGORY_DEBIT);
        cardDto.setCardStatus(Constants.CARD_ACTIVE_STATUS);
        cardDto.setCustomerDto(user.getCustomerDto());
        return cardDto;

    }

    @Override
    public void deleteByCardNo(long cardNo) {
        int deletedRows = cardDao.deleteByCardNo(cardNo);
        if(deletedRows == 0){
            throw new CardNotFoundException("Card not found.");
        }
    }

    @Override
    public CardDto updateCard(CardDto card) {
        return mapToCardDto(cardDao.save(mapToCardEntity(card)));
    }

    @Override
    public List<CardDto> getCardsByChId(Customer customer) {
        List<CardDto> cards = cardDao.findCardsByCustomer(customer).stream().map(this::mapToCardDto).collect(Collectors.toList());
        return cards;
    }

    @Override
    public CardDto getCardByCardNo(long cardNo) {
        Card card = cardDao.findByCardNo(cardNo);
        return Objects.isNull(card) ? null : mapToCardDto(card);
    }

    @Override
    public List<CardDto> getAllCards() {
        return cardDao.findAll().stream().map(this::mapToCardDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardDto blockCard(long cardNo) {
        Card card = cardDao.findByCardNo(cardNo);
        if(Objects.isNull(card)){
            throw new CardNotFoundException("Card not found.");
        }
        //loaded inside a transaction, so the status change is flushed by dirty checking
        card.setCardStatus(CardStatus.BLOCKED.getStatus());
        return mapToCardDto(card);
    }

    @Override
    public List<TransactionDto> getAllCardsTransactions(List<Long> cardNumbers) {
        List<Card> cardsList = cardDao.findByCardNoIn(cardNumbers);
        return transactionService.getAllCardsTransactions(cardsList);
    }

    private Long generateRandomNumber() {
        return ThreadLocalRandom.current().nextLong(10_000_000_000L, 100_000_000_000L);
    }

    private CardDto mapToCardDto(Card card){
        CardDto cardDto = CardDto.builder().cardSrno(card.getCardSrno())
                .cardNo(card.getCardNo()).category(card.getCategory()).isMainCard(card.getIsMainCard())
                .primaryCardNo(card.getPrimaryCardNo()).customerDto(CustomerServiceImpl.customerEntityToDtoMapping(card.getCustomer()))
                .isActive(card.getIsActive()).cardStatus(card.getCardStatus())
                .createdOn(card.getCreatedOn()).lastUpdatedOn(card.getLastUpdatedOn())
                .build();
        return  cardDto;
    }
    private Card mapToCardEntity(CardDto cardDto){
        Card card = Card.builder().category(cardDto.getCategory()).cardNo(cardDto.getCardNo())
                .cardSrno(cardDto.getCardSrno()).primaryCardNo(cardDto.getPrimaryCardNo()).isActive(cardDto.getIsActive())
                .isMainCard(cardDto.getIsMainCard()).createdOn(cardDto.getCreatedOn()).lastUpdatedOn(cardDto.getLastUpdatedOn())
                .cardStatus(cardDto.getCardStatus())
                .customer(CustomerServiceImpl.customerDtoToEntityMapping(cardDto.getCustomerDto()))
                .build();
        return card;
    }
}
