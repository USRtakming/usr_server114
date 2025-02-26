package com.usr_server.Repository;

import org.springframework.stereotype.Repository;
import com.usr_server.Entity.ImportantLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ImportantLinkRepository extends JpaRepository<ImportantLinkEntity,Long> {

}
