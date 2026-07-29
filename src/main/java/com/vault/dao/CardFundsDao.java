package com.vault.dao;

import com.vault.entity.CardFunds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "card-funds")
public interface CardFundsDao extends JpaRepository<CardFunds, Integer> {
    CardFunds findByCardCardNo(long cardNo);
}