package com.example.MachineCoding.Controller.Loan;

import com.example.MachineCoding.Models.Loan.User;
import com.example.MachineCoding.Service.Loan.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user){
        // Logic to create user
        return userService.createUser(user);

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String,String> map) {
        String email = map.get("email");
        String password = map.get("password");
        return userService.loginUser(email, password);
    }

}
