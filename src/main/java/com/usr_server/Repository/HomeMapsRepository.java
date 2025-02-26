package com.usr_server.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.usr_server.Entity.HomeMapsEntity;

@Repository
public interface HomeMapsRepository extends JpaRepository<HomeMapsEntity, Long> {

}
