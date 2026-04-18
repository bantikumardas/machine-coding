package com.example.MachineCoding.Repository.SplitWise;

import com.example.MachineCoding.Models.SplitWise.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepo extends JpaRepository<UserGroup,Long> {
    @Query("SELECT g FROM UserGroup g JOIN g.members m WHERE m.id = :userId")
    List<UserGroup> findGroupsByMemberId(@Param("userId") Long userId);
}
