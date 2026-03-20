package com.example.MachineCoding.DTO.SplitWise;

import lombok.Data;

import java.util.List;

@Data
public class UserGroupReqDTO {
    private Long id;
    private String groupName;
    private String groupDescription;
    private String groupImageUrl;
    private String createdBy;
    private List<String> membersId;
    private List<String> transactionsId;
}
