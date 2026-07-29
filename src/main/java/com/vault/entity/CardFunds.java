package com.vault.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "card_funds")
public class CardFunds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ledger_balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal ledgerBalance  = BigDecimal.ZERO;

    @Column(name = "card_balance", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal cardBalance = BigDecimal.ZERO;

    @Column(name = "card_blnc_onhold", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal cardBalanceOnHold = BigDecimal.ZERO;

    //not cascading persist/remove, because the card is not created or deleted by its funds row
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "card_srno", referencedColumnName = "card_srno", foreignKey = @ForeignKey(name = "FK_cards"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Card card;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updationTimeStamp;
}
