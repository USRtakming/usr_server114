package com.usr_server.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.usr_server.Entity.CasesEntity;

@Repository
public interface CaseRepository extends JpaRepository<CasesEntity, Long> {

}