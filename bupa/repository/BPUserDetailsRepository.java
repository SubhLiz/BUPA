package com.incture.bupa.repository;

import com.incture.bupa.entities.BPUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BPUserDetailsRepository extends JpaRepository<BPUserDetails, Integer> {

    @Query(value = "select distinct m.userEmail from BPUserDetails m")
    Set<String> getAllUserEmail();


    @Query(value = "select distinct m.userEmail,m.userName from BPUserDetails m")
    List<Object[]> getAllUserDetails();
}
