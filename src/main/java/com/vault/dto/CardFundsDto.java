package com.vault.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CardFundsDto {
    private Integer id;
    private Long cardNo;
    private BigDecimal ledgerBalance = BigDecimal.ZERO;
    private BigDecimal cardBalance = BigDecimal.ZERO;
    private BigDecimal cardBalanceOnHold = BigDecimal.ZERO;
    private LocalDateTime createdOn;
    private LocalDateTime updationTimeStamp;
}