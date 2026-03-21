package com.example.MachineCoding.Service.SplitWise;


import com.example.MachineCoding.DTO.SplitWise.BalanceEntryDTO;
import com.example.MachineCoding.DTO.SplitWise.TransactionReqDTO;
import com.example.MachineCoding.DTO.SplitWise.UserGroupReqDTO;
import com.example.MachineCoding.ErrorHandler.BadRequestException;
import com.example.MachineCoding.Models.SplitWise.BalanceSheet;
import com.example.MachineCoding.Models.SplitWise.Participant;
import com.example.MachineCoding.Models.SplitWise.Transaction;
import com.example.MachineCoding.Models.SplitWise.UserGroup;
import com.example.MachineCoding.Models.User;
import com.example.MachineCoding.Repository.SplitWise.BalanceSheetRepo;
import com.example.MachineCoding.Repository.SplitWise.ParticipantRepo;
import com.example.MachineCoding.Repository.SplitWise.TransactionRepo;
import com.example.MachineCoding.Repository.SplitWise.UserGroupRepo;
import com.example.MachineCoding.Repository.UserRepository;
import com.example.MachineCoding.Utils.AuthUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SplitService {
    private static final double MONEY_EPSILON = 0.01;

    @Autowired
    private UserRepository usersRepo;
    @Autowired
    private UserGroupRepo userGroupRepo;
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private ParticipantRepo participantRepo;
    @Autowired
    private BalanceSheetRepo balanceSheetRepo;
    @Autowired
    private AuthUtil authUtil;


    public ResponseEntity<UserGroup> createUserGroup(UserGroupReqDTO userGroupDto) {
        User user = authUtil.getCurrentUser();
        if (user == null) {
            throw new BadRequestException("Invalid User");
        }
        UserGroup userGroup = new UserGroup();
        if (userGroupDto.getGroupName() == null || userGroupDto.getGroupName().isEmpty()) {
            throw new BadRequestException("Group name is empty");
        }
        userGroup.setGroupName(userGroupDto.getGroupName());
        userGroup.setGroupDescription(userGroupDto.getGroupDescription());
        userGroup.setGroupImageUrl(userGroupDto.getGroupImageUrl());
        if (userGroupDto.getMembersId() == null || userGroupDto.getMembersId().isEmpty()) {
            throw new BadRequestException("At least one member is required");
        }
        List<User> users = new ArrayList<>();
        for (String s : userGroupDto.getMembersId()) {
            users.add(usersRepo.findById(Long.parseLong(s))
                    .orElseThrow(() -> new BadRequestException("Member not found: " + s)));
        }
        userGroup.setMembers(users);
        userGroup.setCreatedBy(user);
        UserGroup savedUserGroup = userGroupRepo.save(userGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUserGroup);
    }

    /**
     * Creates an expense split and updates {@link BalanceSheet} rows:
     * each participant (except payer) owes the payer their {@link Participant#getShareAmount()}.
     */
    @Transactional
    public ResponseEntity<?> createTransaction(TransactionReqDTO transactionReqDTO) {
        if (transactionReqDTO.getSpendingAmount() == null || transactionReqDTO.getSpendingAmount() <= 0) {
            throw new BadRequestException("Spending amount is empty");
        }
        if (transactionReqDTO.getCategory() == null || transactionReqDTO.getCategory().isEmpty()) {
            throw new BadRequestException("Category is empty");
        }
        if (transactionReqDTO.getParticipantsId() == null || transactionReqDTO.getParticipantsId().isEmpty()) {
            throw new BadRequestException("At least one participant is required");
        }

        User paidBy = usersRepo.findById(Long.parseLong(transactionReqDTO.getPaidBy()))
                .orElseThrow(() -> new BadRequestException("paidBy is not found"));
        UserGroup userGroup = userGroupRepo.findById(Long.parseLong(transactionReqDTO.getGroupId()))
                .orElseThrow(() -> new BadRequestException("group is not found"));

        double totalShare = 0;
        for (TransactionReqDTO.ParticipantDto s : transactionReqDTO.getParticipantsId()) {
            if (s.getShareAmount() == null || s.getShareAmount() < 0) {
                throw new BadRequestException("Invalid share for participant " + s.getParticipantId());
            }
            totalShare += s.getShareAmount();
        }
        if (Math.abs(transactionReqDTO.getSpendingAmount() - totalShare) > MONEY_EPSILON) {
            throw new BadRequestException("Spending amount and sum of shares must match");
        }

        Transaction transaction = new Transaction();
        transaction.setCategory(transactionReqDTO.getCategory());
        transaction.setSpendingAmount(transactionReqDTO.getSpendingAmount());
        transaction.setGroup(userGroup);
        transaction.setDescription(transactionReqDTO.getDescription());
        transaction.setPaidBy(paidBy);
        transaction.setStatus("Initiated");

        Transaction savedTransaction = transactionRepo.save(transaction);

        List<Participant> savedParticipants = new ArrayList<>();
        for (TransactionReqDTO.ParticipantDto s : transactionReqDTO.getParticipantsId()) {
            User participantUser = usersRepo.findById(Long.parseLong(s.getParticipantId()))
                    .orElseThrow(() -> new BadRequestException("participant not found: " + s.getParticipantId()));
            Participant p = new Participant();
            p.setParticipant(participantUser);
            p.setShareAmount(s.getShareAmount());
            p.setTransaction(savedTransaction);
            savedParticipants.add(participantRepo.save(p));
        }

        applyExpenseToBalanceSheet(userGroup, paidBy, savedParticipants);

        savedTransaction.setStatus("active");
        savedTransaction.setParticipants(savedParticipants);
        transactionRepo.save(savedTransaction);

        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction added successfully.");
    }

    /**
     * For each participant other than the payer: debtor = participant, creditor = payer, add share amount.
     */
    private void applyExpenseToBalanceSheet(UserGroup group, User paidBy, List<Participant> participants) {
        Long payerId = paidBy.getId();
        for (Participant p : participants) {
            User participant = p.getParticipant();
            if (participant.getId().equals(payerId)) {
                continue;
            }
            double share = p.getShareAmount();
            if (share <= 0) {
                continue;
            }
            addOrIncrementDebt(group, participant, paidBy, share);
        }
    }

    /**
     * Increments how much {@code debtor} owes {@code creditor} inside {@code group}.
     */
    private void addOrIncrementDebt(UserGroup group, User debtor, User creditor, double amount) {
        BalanceSheet row = balanceSheetRepo
                .findByUserGroup_IdAndGiver_IdAndTaker_Id(group.getId(), debtor.getId(), creditor.getId())
                .orElseGet(() -> {
                    BalanceSheet b = new BalanceSheet();
                    b.setUserGroup(group);
                    b.setGiver(debtor);
                    b.setTaker(creditor);
                    b.setBalance(0.0);
                    return b;
                });
        row.setBalance(roundMoney(row.getBalance() + amount));
        balanceSheetRepo.save(row);
    }

    private static double roundMoney(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    @Transactional()
    public List<BalanceEntryDTO> getGroupBalances(Long groupId) {
        userGroupRepo.findById(groupId)
                .orElseThrow(() -> new BadRequestException("group is not found"));
        return balanceSheetRepo.findByUserGroup_Id(groupId).stream()
                .filter(b -> b.getBalance() != null && Math.abs(b.getBalance()) > MONEY_EPSILON)
                .map(b -> new BalanceEntryDTO(
                        b.getGiver().getId(),
                        b.getGiver().getEmail(),
                        b.getTaker().getId(),
                        b.getTaker().getEmail(),
                        b.getBalance()
                ))
                .collect(Collectors.toList());
    }

    public ResponseEntity<User> getUser(Long userId) {
        User user = authUtil.getCurrentUser();
        System.out.println(user.getEmail() + " Email");
        User users = usersRepo.findById(userId)
                .orElseThrow(() -> new BadRequestException("user is not found"));
        users.setHashPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    /**
     * Optional: recompute group balances from all active transactions (use if you fix historical data).
     */
    @Transactional
    public void rebuildBalanceSheetFromTransactions(Long groupId) {
        UserGroup group = userGroupRepo.findById(groupId)
                .orElseThrow(() -> new BadRequestException("group is not found"));
        balanceSheetRepo.deleteAll(balanceSheetRepo.findByUserGroup_Id(groupId));
        List<Transaction> txs = transactionRepo.findByGroup_IdAndStatus(groupId, "active");
        for (Transaction t : txs) {
            List<Participant> parts = participantRepo.findByTransaction_Id(t.getId());
            applyExpenseToBalanceSheet(group, t.getPaidBy(), parts);
        }
    }
}
