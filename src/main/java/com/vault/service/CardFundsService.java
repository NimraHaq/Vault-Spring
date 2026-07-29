package com.vault.service;

import com.vault.dto.CardFundsDto;
import com.vault.entity.Card;
import com.vault.entity.CardFunds;

import java.math.BigDecimal;
import java.util.List;

public abstract class CardFundsService {

    abstract public CardFundsDto addCardFunds(Card card);

    abstract public CardFundsDto getCardFundsByCardNo(Long cardNo);

    abstract public CardFundsDto depositFundsInCard(Long cardNo, BigDecimal amount);

    abstract public CardFundsDto withdrawFundsFromCard(Long cardNo, BigDecimal amount);

    abstract public List<CardFundsDto> fundsTransfer(Long fromCardNo, Long toCardNo, BigDecimal amount);

    abstract protected CardFunds updateCardFunds(Long cardNo, BigDecimal amount);
}