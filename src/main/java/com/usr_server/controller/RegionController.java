package com.usr_server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.usr_server.Entity.CasesEntity;
import com.usr_server.Entity.CountryEntity;
import com.usr_server.Entity.RegionEntity;
import com.usr_server.dome.dto.RegionDto;
import com.usr_server.service.CasesService;
import com.usr_server.service.CountryService;
import com.usr_server.service.RegionService;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

	@Autowired
    private CountryService countryService;
	
	@Autowired
    private RegionService regionService;
	
	@Autowired
    private CasesService casesService;
	
	// 取得全部資料
	@GetMapping("/allRegions")
	public ResponseEntity<List<RegionDto>> getAllRegions() {
	    List<RegionEntity> regions = regionService.getAllRegions();
	    List<RegionDto> regionDtos = regions.stream()
	        .map(region -> {
	            RegionDto dto = new RegionDto();
	            dto.setId(region.getId());
	            dto.setName(region.getName());
	            return dto;
	        })
	        .collect(Collectors.toList());
	    return new ResponseEntity<>(regionDtos, HttpStatus.OK);
	}


	// 取得全部資料(包含Country)
    @GetMapping("/allRegionsCountries")
    public ResponseEntity<List<RegionEntity>> getAllRegionsCountries() {
        List<RegionEntity> regions = regionService.getAllRegions();
        return new ResponseEntity<>(regions, HttpStatus.OK);
    }

    // 根據ID查詢資料
    @GetMapping("/{id}")
    public ResponseEntity<RegionEntity> getRegionCountryById(@PathVariable Long id) {
        RegionEntity region = regionService.getRegionById(id);
        if (region == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(region, HttpStatus.OK);
    }


    // 創建
    @PostMapping("/create")
    public ResponseEntity<RegionEntity> saveRegion(
            @RequestParam String name,
            @RequestParam Long countryId) {
        RegionEntity region = new RegionEntity();
        region.setName(name);

        CountryEntity country = countryService.getCountryById(countryId);
        if (country == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        region.setCountry(country); // 將 RegionEntity 設定為對應的 CountryEntity

        RegionEntity savedRegion = regionService.saveRegionCountry(region);
        return new ResponseEntity<>(savedRegion, HttpStatus.CREATED);
    }

    
    // 更新資料
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRegion(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "casesId", required = false) Long casesId) {
        
        Logger logger = LoggerFactory.getLogger(RegionController.class);
        
        try {
            RegionEntity existingRegion = regionService.getRegionById(id);
            
            if (existingRegion == null) {
                logger.info("沒有這個子地區ID: {}", id);
                return new ResponseEntity<>("子地區不存在", HttpStatus.NOT_FOUND);
            }

            // 更新名稱
            if (name != null) {
                existingRegion.setName(name);
            }
            
            // 更新案例
            if (casesId != null) {
                CasesEntity cases = casesService.getCasesById(casesId);
                if (cases != null) {
                    Set<CasesEntity> casesSet = new HashSet<>(existingRegion.getCases());
                    casesSet.add(cases);
                    existingRegion.setCases(casesSet);
                } else {
                    logger.info("沒有這個子地區ID: {}", casesId);
                    return new ResponseEntity<>("子地區不存在", HttpStatus.NOT_FOUND);
                }
            }

            RegionEntity updatedRegion = regionService.saveRegionCountry(existingRegion);
            logger.info("子地區資料已更新: {}", updatedRegion.toString());

            return new ResponseEntity<>(updatedRegion, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("更新子地區資料失敗", e);
            return new ResponseEntity<>("更新子地區資料失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    // 刪除資料
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRegion(@PathVariable Long id) {
        Logger logger = LoggerFactory.getLogger(RegionController.class);
        try {
            regionService.deleteRgionCases(id); // 確保方法名稱和實現正確
            logger.info("子地區資料已刪除，ID：" + id);
            return new ResponseEntity<>("子地區資料已成功刪除", HttpStatus.OK); // 回傳成功狀態和消息
        } catch (Exception e) {
            logger.error("刪除子地區資料失敗", e);
            return new ResponseEntity<>("刪除子地區資料失敗", HttpStatus.INTERNAL_SERVER_ERROR); // 回傳錯誤狀態和消息
        }
    }
}
