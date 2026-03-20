package com.example.MachineCoding.Models.SplitWise;

import com.example.MachineCoding.Models.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    private String groupDescription;
    private String groupImageUrl;
    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    private Timestamp createdDate;
    private Timestamp updatedDate;

    @PrePersist
    public void prePersist() {
        createdDate = new Timestamp(System.currentTimeMillis());
        updatedDate = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = new Timestamp(System.currentTimeMillis());
    }

}
