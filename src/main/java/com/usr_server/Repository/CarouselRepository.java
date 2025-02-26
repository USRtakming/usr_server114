package com.usr_server.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.usr_server.Entity.CarouselEntity;

@Repository
public interface CarouselRepository extends JpaRepository<CarouselEntity, Long>{
	
}