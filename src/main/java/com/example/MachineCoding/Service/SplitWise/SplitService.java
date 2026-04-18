package com.example.MachineCoding.Service.SplitWise;


import com.example.MachineCoding.DTO.SplitWise.BalanceEntryDTO;
import com.example.MachineCoding.DTO.SplitWise.TransactionReqDTO;
import com.example.MachineCoding.DTO.SplitWise.UserGroupReqDTO;
import com.example.MachineCoding.DTO.SplitWise.UserGroupResponseDTO;
import com.example.MachineCoding.DTO.UserDto;
import com.example.MachineCoding.ErrorHandler.BadRequestException;
import com.example.MachineCoding.ErrorHandler.ResourceNotFoundException;
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

import java.util.*;
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
    @Autowired
    private jakarta.persistence.EntityManager entityManager;


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
        simplifyBalances(userGroup.getId());

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

    @Transactional
    public void simplifyBalances(Long groupId) {
        UserGroup group = userGroupRepo.findById(groupId)
                .orElseThrow(() -> new BadRequestException("group not found"));

        List<BalanceSheet> rows = balanceSheetRepo.findByUserGroup_Id(groupId);

        // Calculate net balances BEFORE deleting
        Map<Long, Double> netBalance = new HashMap<>();
        Map<Long, User> userMap = new HashMap<>();

        for (BalanceSheet b : rows) {
            Long giverId = b.getGiver().getId();
            Long takerId = b.getTaker().getId();
            double amount = b.getBalance();

            netBalance.merge(giverId, -amount, Double::sum);
            netBalance.merge(takerId, +amount, Double::sum);
            userMap.put(giverId, b.getGiver());
            userMap.put(takerId, b.getTaker());
        }

        // Delete and FLUSH immediately
        balanceSheetRepo.deleteAll(rows);
        balanceSheetRepo.flush();          // ← forces DELETE to hit DB now
        entityManager.clear();             // ← clears JPA cache

        // Now build simplified list
        PriorityQueue<long[]> creditors = new PriorityQueue<>((a, b) -> Double.compare(
                Double.longBitsToDouble(b[1]), Double.longBitsToDouble(a[1])));
        PriorityQueue<long[]> debtors = new PriorityQueue<>((a, b) -> Double.compare(
                Double.longBitsToDouble(a[1]), Double.longBitsToDouble(b[1])));

        for (Map.Entry<Long, Double> entry : netBalance.entrySet()) {
            double net = roundMoney(entry.getValue());
            if (net > MONEY_EPSILON) {
                creditors.offer(new long[]{entry.getKey(), Double.doubleToLongBits(net)});
            } else if (net < -MONEY_EPSILON) {
                debtors.offer(new long[]{entry.getKey(), Double.doubleToLongBits(net)});
            }
        }

        List<BalanceSheet> simplified = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            long[] creditor = creditors.poll();
            long[] debtor   = debtors.poll();

            double credit = Double.longBitsToDouble(creditor[1]);
            double debt   = -Double.longBitsToDouble(debtor[1]);

            double settled = roundMoney(Math.min(credit, debt));

            BalanceSheet b = new BalanceSheet();
            b.setUserGroup(group);
            b.setGiver(userMap.get(debtor[0]));
            b.setTaker(userMap.get(creditor[0]));
            b.setBalance(settled);
            simplified.add(b);

            double remainCredit = roundMoney(credit - settled);
            double remainDebt   = roundMoney(debt - settled);

            if (remainCredit > MONEY_EPSILON)
                creditors.offer(new long[]{creditor[0], Double.doubleToLongBits(remainCredit)});
            if (remainDebt > MONEY_EPSILON)
                debtors.offer(new long[]{debtor[0], Double.doubleToLongBits(-remainDebt)});
        }

        balanceSheetRepo.saveAll(simplified);
    }

    public ResponseEntity<List<UserGroupResponseDTO>> getGroupOfCurrentUser() {
        User user=authUtil.getCurrentUser();
        if(user==null)
            throw new BadRequestException("Unauthorize access");
        List<UserGroup> userGroups=userGroupRepo.findGroupsByMemberId(user.getId());
        if(userGroups.isEmpty())
            throw new ResourceNotFoundException("No UserGroup found");
        List<UserGroupResponseDTO> userGroupResponseDTO=new ArrayList<>();
        for(UserGroup userGroup:userGroups){
            UserGroupResponseDTO u=new UserGroupResponseDTO();
            u.setId(userGroup.getId());
            u.setGroupName(userGroup.getGroupName());
            u.setGroupDescription(userGroup.getGroupDescription());

            UserDto userDto=new UserDto();
            userDto.setId(userGroup.getCreatedBy().getId());
            userDto.setUserName(userGroup.getCreatedBy().getUsername());
            userDto.setEmail(userGroup.getCreatedBy().getEmail());
            userDto.setGender(userGroup.getCreatedBy().getGender());
            userDto.setEmail(userGroup.getCreatedBy().getEmail());

            u.setCreatedBy(userDto);
            u.setGroupImageUrl(userGroup.getGroupImageUrl());
            u.setCreatedDate(userGroup.getCreatedDate());
            u.setUpdatedDate(userGroup.getUpdatedDate());
            userGroupResponseDTO.add(u);
        }
        return ResponseEntity.ok(userGroupResponseDTO);
    }
}
