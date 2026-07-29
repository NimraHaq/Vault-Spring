package com.vault.dao;

import com.vault.entity.Card;
import com.vault.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "cards")
public interface CardDao extends JpaRepository<Card, Long> {
    Card findByCardNo(Long cardNo);
    List<Card> findByCardNoIn(List<Long> cardNo);
    int deleteByCardNo(Long cardNo);
    List<Card> findCardsByCustomer(Customer customer);
}
