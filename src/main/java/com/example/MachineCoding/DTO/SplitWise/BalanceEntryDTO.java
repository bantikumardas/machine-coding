package com.example.MachineCoding.DTO.SplitWise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API view: {@code debtor} owes {@code creditor} {@code amount} in this group.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceEntryDTO {
    private Long debtorId;
    private String debtorEmail;
    private Long creditorId;
    private String creditorEmail;
    private Double amount;
}
