package com.example.MachineCoding.Models.SplitWise;

import lombok.Data;

@Data
public class ParticipantDto {
    private Long id;
    private String participantId;
    private Double shareAmount;
    private String transactionId;
}
