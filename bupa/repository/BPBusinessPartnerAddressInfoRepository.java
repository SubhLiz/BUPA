package com.incture.bupa.repository;

import com.incture.bupa.entities.BPBusinessPartnerAddressInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BPBusinessPartnerAddressInfoRepository extends JpaRepository<BPBusinessPartnerAddressInfo,Integer> {

}
