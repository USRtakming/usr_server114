package com.usr_server.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.usr_server.Entity.CasesEntity;
import com.usr_server.Entity.CountryEntity;
import com.usr_server.Entity.RegionEntity;
import com.usr_server.Repository.CaseRepository;
import com.usr_server.Repository.CountryRepository;
import com.usr_server.dome.dto.CasesDto;
import com.usr_server.dome.dto.CountryDto;
import com.usr_server.dome.dto.RegionDto;

@Service
public class CountryService {

	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
    private CaseRepository caseRepository;
	
//	取得全部資料
	public List<CountryEntity> getAllConutry(){
		try {
			return countryRepository.findAll();			
		}catch(Exception e) {
			throw new RuntimeException("抓取全部國家資訊錯誤: ", e);
		}
	}
//	根據ID查詢資料
	public CountryEntity getCountryById(Long id) {
	    return countryRepository.findById(id).orElseThrow(() -> 
	        new RuntimeException("未找到 ID 所在國家/地區: " + id));
	}
	
//	更新、保存
	public CountryEntity saveCountry(CountryEntity country) {
	    try {
	        return countryRepository.save(country);
	    } catch (DataAccessException e) {
	        // 記錄異常 (optional)
	        System.err.println("資料庫操作失敗: " + e.getMessage());
	        // 拋出自定義異常或傳回預設值
	        throw new RuntimeException("保存國家資料失敗: ", e);
	    }
	}
	
	
//	刪除
	public void deleteCountry(Long id) {
	    try {
	    	countryRepository.deleteById(id);
	    } catch (EmptyResultDataAccessException e) {
	        // 處理嘗試刪除不存在的實體的情況
	        System.err.println("找不到ID為 " + id + " 的國家");
	        // 可以選擇記錄日誌或拋出自訂例外
	        throw new RuntimeException("刪除國家失敗。ID為 " + id + " 的實體不存在", e);
	    } catch (DataAccessException e) {
	        // 處理其他資料訪問例外
	        System.err.println("資料庫操作失敗：" + e.getMessage());
	        throw new RuntimeException("刪除國家資料失敗", e);
	    }
	}
	
	public List<CountryDto> getCountriesWithRegionsAndCases() {
	    List<CountryDto> countryDtos = new ArrayList<>();
	    List<CountryEntity> countries = countryRepository.findAll();

	    for (CountryEntity countryEntity : countries) {
	        CountryDto countryDto = new CountryDto();
	        countryDto.setId(countryEntity.getId());
	        countryDto.setName(countryEntity.getName());

	        List<RegionDto> regionDtos = new ArrayList<>(); // 新建 regionDtos 列表
	        if (countryEntity.getRegions() != null) { // 注意這裡改為 getRegions()
	            for (RegionEntity regionEntity : countryEntity.getRegions()) {
	                RegionDto regionDto = new RegionDto();
	                regionDto.setId(regionEntity.getId());
	                regionDto.setName(regionEntity.getName());

	                // 加載 Cases
	                List<CasesDto> casesDtos = new ArrayList<>();
	                for (CasesEntity caseEntity : regionEntity.getCases()) {
	                    CasesDto casesDto = new CasesDto();
	                    casesDto.setId(caseEntity.getId());
	                    casesDto.setTitle(caseEntity.getTitle());
	                    casesDto.setImage(caseEntity.getImage());
	                    casesDto.setPdfLink(caseEntity.getPdfLink());
	                    casesDto.setContentDate(caseEntity.getContentDate());
	                    casesDto.setDate(caseEntity.getDate());
	                    casesDtos.add(casesDto);
	                }
	                regionDto.setCases(casesDtos);
	                regionDtos.add(regionDto); // 加入 regionDtos 列表
	            }
	        }

	        countryDto.setRegions(regionDtos); // 設置所有 regions
	        countryDtos.add(countryDto);
	    }

	    return countryDtos;
	}


	
}
