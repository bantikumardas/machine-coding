package com.example.MachineCoding.DTO.SplitWise;

import com.example.MachineCoding.DTO.UserDto;
import com.example.MachineCoding.Models.SplitWise.Transaction;
import com.example.MachineCoding.Models.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class UserGroupResponseDTO {
    private Long id;
    private String groupName;
    private String groupDescription;
    private String groupImageUrl;
    private List<UserDto> members;
    private List<Transaction> transactions;
    private UserDto createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;
}
