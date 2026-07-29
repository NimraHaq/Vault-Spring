package com.vault.controller;

import com.vault.dao.UserDao;
import com.vault.dto.UserDto;
import com.vault.entity.User;
import com.vault.service.UserService;
import com.vault.utils.Constants;
import com.vault.utils.Utils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
public class HomeController {

    UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    //seeds a default admin on startup so the app is usable on a fresh database
    @Bean
    public CommandLineRunner loadDemoData(UserDao userDao) {
        return args -> {
            if (Objects.isNull(userDao.findUserByUsername("admin"))) {
                User admin = User.builder()
                        .username("admin")
                        .password(Constants.PASSWORD_HASHING_NOOP + "1234")
                        .role(Constants.ADMIN_ROLE)
                        .firstName("admin")
                        .lastName("admin")
                        .email("nimra@gmail.com")
                        .cnic("3453234535")
                        .isActive(Constants.OPTION_YES)
                        .build();
                userDao.save(admin);
            }
        };
    }

    @GetMapping("/login")
    public String logInPage(){
        return "login";
    }


    @GetMapping("/showFormToRegisterUser")
    public String showFormToRegister(){
        //generate card when user registers
        return "RegisterationForm";
    }


    // Process the form
    @PostMapping("/processFormToRegisterUser")
    public String processFormToRegisterUser(@RequestParam("username") String username,
                                            @RequestParam("cnic") String cnic,
                                            @RequestParam("password") String password,
                                            Model model) {
        // basic guard against empty values
        if (Utils.isNullOrEmptyString(username)
                || Utils.isNullOrEmptyString(cnic)
                || Utils.isNullOrEmptyString(password)) {
            return "redirect:/showFormToRegisterUser?incompleteData";
        }else if (password.length() < 8 || password.length() > 12) {
            return "redirect:/register?passwordError";
        }else{
            UserDto user = userService.findUserByUsernameAndCnic(username, cnic);
            if(Objects.isNull(user)){
                return "redirect:/showFormToRegisterUser?notfound";
            }
            if(user.getIsActive().equalsIgnoreCase(Constants.OPTION_YES)){
                return "redirect:/showFormToRegisterUser?alreadyRegistered";
            }
            userService.registerUser(user, password);
            model.addAttribute("confirmationMsg", "User registered successfully.");
            model.addAttribute("goBackLink", "/admin/dashboard");
            return "ConfirmationPage";
        }
    }
}
