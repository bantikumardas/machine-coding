package com.example.MachineCoding.Repository.SplitWise;

import com.example.MachineCoding.Models.SplitWise.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepo extends JpaRepository<Participant, Long> {

    List<Participant> findByTransaction_Id(Long transactionId);
}
