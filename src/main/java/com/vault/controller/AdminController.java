package com.vault.controller;

import com.vault.dto.UserDto;
import com.vault.enums.CardStatus;
import com.vault.service.CardFundsService;
import com.vault.service.CardService;
import com.vault.service.CustomerService;
import com.vault.service.UserService;
import com.vault.utils.Constants;
import com.vault.utils.Utils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Objects;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private CustomerService customerService;
    private CardService cardService;
    private CardFundsService cardFundsService;

    @Autowired
    public AdminController(UserService userService, CustomerService customerService, CardService cardService,
                           CardFundsService cardFundsService) {
        this.userService = userService;
        this.customerService = customerService;
        this.cardService = cardService;
        this.cardFundsService = cardFundsService;
    }

    //instead of using StringTrimmerEditor, use @NotBlank annotion
    //string trimmer editor removes starting and trailing white spaces
    //this function pre-processes all controller request
    //so this implementation pre-processes all Strings
    //but we can use @NotBlank for DTO only, if we are taking input without DTO, we should use InitBinder
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder){
        //null in constructor -> if a string of all white spaces -> return null
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard(Authentication authentication, Model model){
        String username = authentication.getName();
        UserDto user =userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "admin/admin-dashboard";
    }

    //one row per card, so a customer with multiple cards appears multiple times
    @GetMapping("/showCustomers")
    public String showCustomers(Model model){
        model.addAttribute("cardsList", cardService.getAllCards());
        model.addAttribute("blockedStatus", CardStatus.BLOCKED.getStatus());
        return "admin/ShowCustomers";
    }

    @PostMapping("/processBlockCard")
    public String processBlockCard(@RequestParam("cardNo") long cardNo, Model model){
        cardService.blockCard(cardNo);
        model.addAttribute("confirmationMsg", "Card " + cardNo + " blocked successfully.");
        model.addAttribute("goBackLink", "/admin/showCustomers");
        return "ConfirmationPage";
    }

    @GetMapping("/showFormToDeposit")
    public String showFormToDeposit(@RequestParam("cardNo") long cardNo, Model model){
        model.addAttribute("cardNo", cardNo);
        model.addAttribute("maxAmount", Constants.MAX_DEPOSIT_AMOUNT);
        return "admin/DepositForm";
    }

    @PostMapping("/processAdminDeposit")
    public String processAdminDeposit(@RequestParam("cardNo") long cardNo,
                                      @RequestParam(value = "amount", required = false) BigDecimal amount,
                                      Model model){
        //browser validation can be bypassed, so the amount is checked here as well
        if(Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0){
            return "redirect:/admin/showFormToDeposit?cardNo=" + cardNo + "&invalidAmount";
        }
        if(amount.compareTo(Constants.MAX_DEPOSIT_AMOUNT) > 0){
            return "redirect:/admin/showFormToDeposit?cardNo=" + cardNo + "&amountTooLarge";
        }

        cardFundsService.depositFundsInCard(cardNo, amount);

        model.addAttribute("confirmationMsg", "Amount " + amount + " deposited successfully.");
        model.addAttribute("goBackLink", "/admin/showCustomers");
        return "ConfirmationPage";
    }

    @GetMapping("/showFormToAddCustomer")
    public String showFormToAddCustomer(Model model){

        UserDto user = new UserDto();
        model.addAttribute("user", user);
        model.addAttribute("role", Constants.CUSTOMER_ROLE);
        model.addAttribute("isAdmin", false);

        return "user/AddUserForm";
    }

    @PostMapping("/processFormToAddCustomer")
    public String processFormToAddCustomer(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "user/AddUserForm";
        }else{
            customerService.addCustomer(userDto);
            model.addAttribute("confirmationMsg", "Customer added successfully.");
            model.addAttribute("goBackLink", "/admin/dashboard");
            return "ConfirmationPage";
        }
    }


    @GetMapping("/showFormToAddAdmin")
    public String showFormToAddAdmin(Model model){

        UserDto user = new UserDto();
        model.addAttribute("user", user);
        model.addAttribute("role", Constants.ADMIN_ROLE);
        model.addAttribute("isAdmin", true);
        return "user/AddUserForm";
    }

    @PostMapping("/processFormToAddAdmin")
    public String processFormToAddAdmin(@Valid @ModelAttribute("user") UserDto user, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "user/AddUserForm";
        }else{
            userService.addAdmin(user);
            model.addAttribute("confirmationMsg", "Admin added successfully.");
            model.addAttribute("goBackLink", "/admin/dashboard");
            return "ConfirmationPage";
        }
    }


    @GetMapping("/showFormToDeleteUser")
    public String showFormToDeleteUser(Model model){
        return "user/DeleteUser";
    }

    @PostMapping("/processFormToDeleteUser")
    public String processFormToDeleteUser(@RequestParam("username") String username, @RequestParam("cnic") String cnic, Model model,
                                          Authentication authentication){
        if(!(Utils.isNullOrEmptyString(username) || Utils.isNullOrEmptyString(cnic))){
            if(username.equals(authentication.getName())){
                return "redirect:/admin/showFormToDeleteUser?loggedInUserCannotDeleted";
            }
            //cannot delete the login user.
            UserDto user = userService.deleteUserByUsernameAndCnic(username, cnic);
            if(Objects.nonNull(user)){
                model.addAttribute("confirmationMsg", "User deleted successfully.");
                model.addAttribute("goBackLink", "/admin/dashboard");
                return "ConfirmationPage";
            }else{
                return "redirect:/admin/showFormToDeleteUser?notfound";
            }
        }
        return "redirect:/admin/showFormToDeleteUser?incompleteData";
    }
}
