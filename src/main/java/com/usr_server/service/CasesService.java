package com.usr_server.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.usr_server.Entity.CasesEntity;
import com.usr_server.Repository.CaseRepository;
import com.usr_server.controller.CasesController;

@Service
public class CasesService {

	@Autowired
	private CaseRepository caseRepository;
	
//	取得全部資料
	public List<CasesEntity> getAllCases(){
		try {
			return caseRepository.findAll();
		}catch(Exception e){
			throw new RuntimeException("抓取全部足跡案例資訊錯誤: ", e);
		}
	}
	
//	根據ID查詢資料
	public CasesEntity getCasesById(Long id) {
		return caseRepository.findById(id).orElseThrow(() -> 
        new RuntimeException("未找到足跡案例 ID : " + id));
	}
	
//  創建
	public CasesEntity saveCases(CasesEntity Cases) {
		Logger logger = LoggerFactory.getLogger(CasesController.class);
		logger.info("save保存成果展現: " + Cases.toString());
		return caseRepository.save(Cases);
	}
	
//	更新、保存
//	public CasesEntity saveCases(CasesEntity cases) {
//	    try {
//	        return caseRepository.save(cases);
//	    } catch (DataAccessException e) {
//	        // 記錄異常 (optional)
//	        System.err.println("資料庫操作失敗: " + e.getMessage());
//	        // 拋出自定義異常或傳回預設值
//	        throw new RuntimeException("保存足跡資料失敗", e);
//	    }
//	}
	
//	刪除
	public void deleteCases(Long id) {
		if(caseRepository.existsById(id)) {
			caseRepository.deleteById(id);
		}else {
			throw new RuntimeException("沒有這則案例");
		}
	}
}
