package com.example.MachineCoding.Controller.SplitWise;


import com.example.MachineCoding.DTO.SplitWise.BalanceEntryDTO;
import com.example.MachineCoding.DTO.SplitWise.TransactionReqDTO;
import com.example.MachineCoding.DTO.SplitWise.UserGroupReqDTO;
import com.example.MachineCoding.Models.SplitWise.UserGroup;
import com.example.MachineCoding.Models.User;
import com.example.MachineCoding.Service.SplitWise.SplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/splitwise")
public class SplitController {
    @Autowired
    private SplitService splitService;


    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        System.out.println("calling to get User : "+userId);
        return splitService.getUser(userId);
    }

    @PostMapping("/group")
    public ResponseEntity<UserGroup> createUserGroup(@RequestBody UserGroupReqDTO userGroup) {
        System.out.println("Request is received");
        return splitService.createUserGroup(userGroup);
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionReqDTO transactionReqDTO) {
        return splitService.createTransaction(transactionReqDTO);
    }

    /**
     * Who owes whom in a group (from accumulated {@link com.example.MachineCoding.Models.SplitWise.BalanceSheet} rows).
     */
    @GetMapping("/group/{groupId}/balances")
    public ResponseEntity<List<BalanceEntryDTO>> getGroupBalances(@PathVariable Long groupId) {
        return ResponseEntity.ok(splitService.getGroupBalances(groupId));
    }

}
