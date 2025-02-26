package com.usr_server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usr_server.Entity.AchievementsEntity;
import com.usr_server.Repository.AchievementsRepository;
import com.usr_server.controller.AchievementsController;

@Service
public class AchievementsService {
	
	@Autowired
	private AchievementsRepository achievementsRepository;

	public List<AchievementsEntity> getAllachievements() {
		return achievementsRepository.findAll();
	}

	public AchievementsEntity getAchievementsById(Long id) throws Exception{
		return achievementsRepository.findById(id)
	            .orElseThrow(() -> new Exception("找不到這則成果展現"));
	}

	public AchievementsEntity saveAchievements(AchievementsEntity achievemants) {
		Logger logger = LoggerFactory.getLogger(AchievementsController.class);
		logger.info("save保存成果展現: " + achievemants.toString());
		return achievementsRepository.save(achievemants);
	}
	
	public void deleteAchievements(Long id) {
        if (achievementsRepository.existsById(id)) {
        	achievementsRepository.deleteById(id);
        } else {
            throw new RuntimeException("成果展現不存在");
        }
    }

}
