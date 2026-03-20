package com.example.MachineCoding.Service.SplitWise;


import com.example.MachineCoding.DTO.SplitWise.TransactionReqDTO;
import com.example.MachineCoding.DTO.SplitWise.UserGroupReqDTO;
import com.example.MachineCoding.ErrorHandler.BadRequestException;
import com.example.MachineCoding.Models.SplitWise.*;
import com.example.MachineCoding.Models.User;
import com.example.MachineCoding.Repository.SplitWise.ParticipantRepo;
import com.example.MachineCoding.Repository.SplitWise.TransactionRepo;
import com.example.MachineCoding.Repository.SplitWise.UserGroupRepo;
import com.example.MachineCoding.Repository.UserRepository;
import com.example.MachineCoding.Utils.PasswordUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SplitService {
    @Autowired
    private UserRepository usersRepo;
    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private UserGroupRepo userGroupRepo;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private ParticipantRepo participantRepo;

    public ResponseEntity<User> createUser(User users) {
            String email= users.getEmail();
            String password= users.getHashPassword();
            if(!isValidEmail(email)){
                throw new BadRequestException("Email is not correct");
            }
            if (usersRepo.findByEmail(email).isPresent()) {
                throw new BadRequestException("Email is already in use");
            }
            if(!validatePassword(password)){
                throw  new BadRequestException("Password should be atleast 6 length and has atleast 1 Big Character, 1 small character, 1 numbers, 1 special character");
            }
            String hashedPassword = passwordUtil.hashPassword(password);
            users.setHashPassword(hashedPassword);
            User savedUser=usersRepo.save(users);
            savedUser.setHashPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

    }

    private boolean validatePassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        return password.matches(regex);
    }

    private boolean isValidEmail(String email){
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public ResponseEntity<UserGroup> createUserGroup(UserGroupReqDTO userGroupDto){
            UserGroup userGroup=new UserGroup();
            if(userGroupDto.getGroupName()==null || userGroupDto.getGroupName().isEmpty()){
                throw  new BadRequestException("Group name is empty");
            }
            userGroup.setGroupName(userGroupDto.getGroupName());
            userGroup.setGroupDescription(userGroupDto.getGroupDescription());
            userGroup.setGroupImageUrl(userGroupDto.getGroupImageUrl());
            List<User> users=new ArrayList<>();
            for(String s: userGroupDto.getMembersId()){
                users.add(usersRepo.findById(Long.parseLong(s)).get());
            }
            userGroup.setMembers(users);
            User admin=usersRepo.findById(Long.parseLong(userGroupDto.getCreatedBy())).get();
            if(admin==null){
                throw  new BadRequestException("groud admin id is not found");
            }
            userGroup.setCreatedBy(admin);
            UserGroup savedUserGroup=userGroupRepo.save(userGroup);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUserGroup);
    }

    @Transactional
    public ResponseEntity<?> createTransaction(TransactionReqDTO transactionReqDTO) {
        Transaction transaction=new Transaction();
        if(transactionReqDTO.getSpendingAmount()==null || transactionReqDTO.getSpendingAmount()<=0){
            throw  new BadRequestException("Spending amount is empty");
        }
        if(transactionReqDTO.getCategory()==null || transactionReqDTO.getCategory()==""){
            throw  new BadRequestException("Category is empty");
        }
        User padiBy=usersRepo.findById(Long.parseLong(transactionReqDTO.getPaidBy())).get();
        if(padiBy==null){
            throw  new BadRequestException("paidBy is not found");
        }
        UserGroup userGroup=userGroupRepo.findById(Long.parseLong(transactionReqDTO.getGroupId())).get();
        if(userGroup==null){
            throw  new BadRequestException("group is not found");
        }
        transaction.setCategory(transactionReqDTO.getCategory());
        transaction.setSpendingAmount(transactionReqDTO.getSpendingAmount());
        transaction.setGroup(userGroup);
        transaction.setDescription(transactionReqDTO.getDescription());
        transaction.setPaidBy(padiBy);
        transaction.setStatus("Initiated");
        Transaction savedTransaction=transactionRepo.save(transaction);
        List<Participant> participant=new ArrayList<>();
        double totalAmount=0;
        for(ParticipantDto s: transactionReqDTO.getParticipantsId()){
            User users=usersRepo.findById(Long.parseLong(s.getParticipantId())).get();
            if(users==null){
                throw  new BadRequestException("participants id is not found");
            }
            Participant p=new Participant();
            totalAmount+=s.getShareAmount();
            p.setParticipant(users);
            p.setShareAmount(s.getShareAmount());
            p.setTransaction(savedTransaction);
            participantRepo.save(p);
        }
        if(Math.abs(transaction.getSpendingAmount()-totalAmount)>0.0001){
            throw  new BadRequestException("Spending amount and share amount did not match");
        }
        savedTransaction.setStatus("active");
        savedTransaction.setParticipants(participant);
        transactionRepo.save(savedTransaction);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction added successfully.");
    }

    public ResponseEntity<User> getUser(Long userId) {
        User users=usersRepo.findById(userId).get();
        if(users==null){
            throw  new BadRequestException("user is not found");
        }
        users.setHashPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
