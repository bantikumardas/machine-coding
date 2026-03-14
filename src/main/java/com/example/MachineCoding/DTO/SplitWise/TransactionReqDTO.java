package com.example.MachineCoding.DTO.SplitWise;
import com.example.MachineCoding.Models.SplitWise.ParticipantDto;
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

}

