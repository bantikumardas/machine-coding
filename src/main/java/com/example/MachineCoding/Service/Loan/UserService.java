package com.example.MachineCoding.Service.Loan;

import com.example.MachineCoding.Models.Loan.Login;
import com.example.MachineCoding.Models.User;
import com.example.MachineCoding.Repository.Loan.AddressRepo;
import com.example.MachineCoding.Repository.Loan.LoginRepo;
import com.example.MachineCoding.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AddressRepo addressRepo;
    @Autowired
    private LoginRepo loginRepo;

    @Transactional
    public ResponseEntity<?> createUser(User user){
        // email validation
        String email = user.getEmail();
        if(email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
            return ResponseEntity.badRequest().body("Invalid email format");
        }
        //phone number validation
        String phoneNumber = user.getPhoneNumber();
        if(phoneNumber == null || !phoneNumber.matches("^[0-9]{10}$")){
            return ResponseEntity.badRequest().body("Invalid phone number format");
        }
        // password validation
        String password = user.getPassword();
        if(password == null || password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*") || !password.matches(".*[@$!%*?&].*")) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long");
        }
        if(userRepo.findByEmail(email).isPresent()){
            return ResponseEntity.badRequest().body("Email already exists");
        }
//        if(userRepo.findByPhoneNumber(phoneNumber).isPresent()){
//            return ResponseEntity.badRequest().body("Phone number already exists");
//        }
//        addressRepo.save(user.getAddress());
        User createdUser=userRepo.save(user);
        return ResponseEntity.ok(createdUser);
    }

    public ResponseEntity<?> loginUser(String email, String password) {
        Optional<User> user=userRepo.findByEmail(email);
        if(user.isEmpty()){
            return ResponseEntity.badRequest().body("Email not found");
        }
        if(!user.get().getPassword().equals(password)){
            return ResponseEntity.badRequest().body("Invalid password");
        }
        Random rand = new Random();
        long randomNumber = System.currentTimeMillis() + rand.nextInt(10000);
        String token=user.get().getId() +""+randomNumber;
        Login login=new Login();
        login.setToken(token);
        login.setEmail(email);
        login.setLoginTime(System.currentTimeMillis());
//        login.setRole(user.get().getRole());
        login.setTokenExpiryTime(System.currentTimeMillis()+1000*60*60);
        loginRepo.save(login);
        return ResponseEntity.ok("Login successful. Your token is: "+token);
    }
}
