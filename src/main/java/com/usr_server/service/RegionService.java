package com.usr_server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.usr_server.Entity.CountryEntity;
import com.usr_server.Entity.RegionEntity;
import com.usr_server.Repository.RegionRepository;

@Service
public class RegionService {

	@Autowired
	private RegionRepository regionRepository;
//	取得全部資料
	public List<RegionEntity> getAllRegions() {
	    try {
	        return regionRepository.findAll();
	    } catch (Exception e) {
	        // 日誌記錄錯誤
	        System.err.println("getAllRegions時發生錯誤: " + e.getMessage());
	        // 拋出異常
	        throw new RuntimeException("getAllRegions失敗", e); 
	    }
	}
	
//	根據ID查詢資料	
	public RegionEntity getRegionById(Long id) {
			return regionRepository.findById(id).orElseThrow(()->
			new RuntimeException("未找到此子地區ID:" + id));			
	}
	
//	更新、保存
	public RegionEntity saveRegionCountry(RegionEntity region) {
	    try {
	        return regionRepository.save(region);
	    } catch (DataAccessException e) {
	        // 記錄異常 (optional)
	        System.err.println("資料庫操作失敗: " + e.getMessage());
	        // 拋出自定義異常或傳回預設值
	        throw new RuntimeException("保存子地區資料失敗", e);
	    }
	}
	
//	刪除
	public void deleteRgionCases(Long id) {
	    try {
	    	regionRepository.deleteById(id);
	    } catch (EmptyResultDataAccessException e) {
	        // 處理嘗試刪除不存在的實體的情況
	        System.err.println("找不到ID為 " + id + " 的子地區");
	        // 可以選擇記錄日誌或拋出自訂例外
	        throw new RuntimeException("刪除子地區失敗。ID為 " + id + " 的實體不存在", e);
	    } catch (DataAccessException e) {
	        // 處理其他資料訪問例外
	        System.err.println("資料庫操作失敗：" + e.getMessage());
	        throw new RuntimeException("刪除子地區資料失敗", e);
	    }
	}

}
