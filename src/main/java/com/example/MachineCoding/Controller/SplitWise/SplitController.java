package com.example.MachineCoding.Controller.SplitWise;


import com.example.MachineCoding.DTO.SplitWise.TransactionReqDTO;
import com.example.MachineCoding.DTO.SplitWise.UserGroupReqDTO;
import com.example.MachineCoding.Models.SplitWise.UserGroup;
import com.example.MachineCoding.Models.User;
import com.example.MachineCoding.Service.SplitWise.SplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/splitwise")
public class SplitController {
    @Autowired
    private SplitService splitService;


    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User users) {
        // Implementation for creating a user
        System.out.println("Calling for create user : "+users.toString());
        return splitService.createUser(users);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId) {
        System.out.println("calling to get User : "+userId);
        return splitService.getUser(userId);
    }

    @PostMapping("/group")
    public ResponseEntity<UserGroup> createUserGroup(@RequestBody UserGroupReqDTO userGroup) {
        return splitService.createUserGroup(userGroup);
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> getAllUsers(@RequestBody TransactionReqDTO transactionReqDTO) {
        return splitService.createTransaction(transactionReqDTO);
    }

}
