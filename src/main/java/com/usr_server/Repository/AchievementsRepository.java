package com.usr_server.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.usr_server.Entity.AchievementsEntity;

@Repository
public interface AchievementsRepository extends JpaRepository<AchievementsEntity, Long> {

}
