package com.usr_server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usr_server.Repository.ImportantLinkRepository;
import com.usr_server.Entity.ImportantLinkEntity;

@Service
public class ImportLinkService {
	@Autowired
	private ImportantLinkRepository importantLinkRepository;
	
	//取得全部資料
	public List<ImportantLinkEntity> getAllImportantLink(){
		return importantLinkRepository.findAll();
	}
	
	//根據ID查詢資料
	public ImportantLinkEntity getImportLinkById(Long id) throws Exception{
			return importantLinkRepository.findById(id)
					.orElseThrow(()-> new Exception("找不到此重要聯結"));
	}

	//更新資料
	public ImportantLinkEntity saveImportLinks(ImportantLinkEntity importlink) {
		Logger logger = LoggerFactory.getLogger(ImportLinkService.class);
		logger.info("save保存重要連結：" + importlink.toString());
		return importantLinkRepository.save(importlink);
	}
	
	//刪除
	public void deleteImportantLinkS(Long id) {
		//幾查是否存在該連結
		if(importantLinkRepository.existsById(id)) {
			importantLinkRepository.deleteById(id);
		}else {
			throw new RuntimeException("重要連結不存在");
		}
	}
	
	 
}
