package com.example.MachineCoding.DTO.SplitWise;
import lombok.Data;
import java.util.List;

@Data
public class TransactionReqDTO {
    private Long id;
    private Double spendingAmount;
    private String description;
    private String category;
    private String paidBy;
    private String status;//active, settled, cancelled
    private String groupId;
    private List<ParticipantDto> participantsId;

    @Data
    public static class ParticipantDto {
        private Long id;
        private String participantId;
        private Double shareAmount;
        private String transactionId;
    }
}

