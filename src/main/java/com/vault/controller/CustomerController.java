package com.vault.controller;


import com.vault.dto.CardDto;
import com.vault.dto.CardFundsDto;
import com.vault.dto.TransactionDto;
import com.vault.dto.UserDto;
import com.vault.service.CardFundsService;
import com.vault.service.CardService;
import com.vault.service.CustomerService;
import com.vault.service.UserService;
import com.vault.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.yaml.snakeyaml.scanner.Constant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    UserService userService;
    CustomerService customerService;
    CardService cardService;
    CardFundsService cardFundsService;

    @Autowired
    public CustomerController(UserService userService, CustomerService customerService, CardService cardService,
                              CardFundsService cardFundsService){
        this.userService = userService;
        this.customerService = customerService;
        this.cardService = cardService;
        this.cardFundsService = cardFundsService;
    }

    @GetMapping("/dashboard")
    public String showCustomerDashboard(Authentication authentication, Model model){
        String username = authentication.getName();
        UserDto user =userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "customer/customer-dashboard";
    }

    @GetMapping("/showCustomerCards")
    public String showCustomerCards(@RequestParam("id") int id, Model model){
        System.out.println("CH id is : " + id);
        List<CardDto> cards = customerService.getCardByChId(id);
        cards.stream().filter(c -> c.getCategory().equalsIgnoreCase(Constants.CARD_CATEGORY_DEBIT)).map(c -> {
            c.setCategory("DEBIT");
            return c;
            }).toList();
        model.addAttribute("cardsList", cards);
        return "customer/ShowCards";
    }

    @GetMapping("/showCardTransactions")
    public String showCardTransactions(@RequestParam("cardNo") long cardNo, Model model){
        List<TransactionDto> transactions = cardService.getAllCardsTransactions(List.of(cardNo));
        model.addAttribute("transactionsList", transactions);
        return "customer/ShowTransactions";
    }

    @GetMapping("/showFundsTransferForm")
    public String showFundsTransferForm(@RequestParam("cardNo") long cardNo, Model model){
        model.addAttribute("fromCardNo", cardNo);
        return "customer/FundsTransfer";
    }

    @PostMapping("/processFundsTransfer")
    public String processFundsTransfer(@RequestParam("fromCardNo") long fromCardNo,
                                       @RequestParam("toCardNo") long toCardNo,
                                       @RequestParam(value = "amount", required = false) BigDecimal amount,
                                       Model model){
        String backToForm = "redirect:/customer/showFundsTransferForm?cardNo=" + fromCardNo;

        //browser validation can be bypassed, so the amount is checked here as well
        if(Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0){
            return backToForm + "&invalidAmount";
        }

        //funds cannot be moved onto the same card
        if(fromCardNo == toCardNo){
            return backToForm + "&sameCard";
        }

        //the beneficiary card must really exist in the DB
        if(Objects.isNull(cardService.getCardByCardNo(toCardNo))){
            return backToForm + "&cardNotFound";
        }

        //the transferring card must be able to cover the amount
        CardFundsDto fromCardFunds = cardFundsService.getCardFundsByCardNo(fromCardNo);
        if(Objects.isNull(fromCardFunds)){
            return backToForm + "&fundsNotFound";
        }
        if(amount.compareTo(fromCardFunds.getCardBalance()) >= 0){
            return backToForm + "&insufficientFunds";
        }

        cardFundsService.fundsTransfer(fromCardNo, toCardNo, amount);

        model.addAttribute("confirmationMsg", "Amount " + amount + " transferred successfully. " );
        model.addAttribute("goBackLink", "/customer/dashboard");
        return "ConfirmationPage";
    }
}
