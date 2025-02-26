package com.usr_server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usr_server.controller.HomeMapController;
import com.usr_server.Entity.HomeMapsEntity;
import com.usr_server.Repository.HomeMapsRepository;

@Service
public class HomeMapsService{
	
	@Autowired
	private HomeMapsRepository homemapsRepository;
	
	public List<HomeMapsEntity> getAllHomeMaps(){
		return homemapsRepository.findAll();
	}
	
	public HomeMapsEntity getHomeMapsById(Long id)throws Exception{
		return homemapsRepository.findById(id)
				.orElseThrow(()-> new Exception("目前尚無服務資料"));
	}
	
	public HomeMapsEntity saveHomeMaps(HomeMapsEntity homemaps) {
		Logger logger = LoggerFactory.getLogger(HomeMapController.class);
		logger.info("save保存服務資料: " + homemaps.toString());
		return homemapsRepository.save(homemaps);
	}
	
	public void deleteHomemaps(Long id) {
		if(homemapsRepository.existsById(id)) {
		   homemapsRepository.deleteById(id);
		}else {
			throw new RuntimeException("服務資料不存在");
		}
	}
}