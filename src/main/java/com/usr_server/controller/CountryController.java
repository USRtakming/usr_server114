package com.usr_server.controller;


import java.util.HashSet;
import java.util.List;

import java.util.Set;

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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.usr_server.Entity.CountryEntity;
import com.usr_server.Entity.RegionEntity;
import com.usr_server.dome.dto.CountryDto;
import com.usr_server.service.CountryService;
import com.usr_server.service.RegionService;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
	@Autowired
    private CountryService countryService;

	@Autowired
    private RegionService regionService;
	
	
	
    // 取得全部資料
    @GetMapping("/allCountry")
    public ResponseEntity<List<CountryEntity>> getAllCountries() {
        List<CountryEntity> countries = countryService.getAllConutry();
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }

    // 根據ID查詢資料
    @GetMapping("/{id}")
    public ResponseEntity<CountryEntity> getCountryById(@PathVariable Long id) {
        CountryEntity country = countryService.getCountryById(id);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    // 創建
    @PostMapping("/create")
    public ResponseEntity<CountryEntity> saveCountry(
            @RequestParam String name) {
        CountryEntity country = new CountryEntity();
        country.setName(name);

        // 儲存國家並返回結果
        CountryEntity savedCountry = countryService.saveCountry(country);
        return new ResponseEntity<>(savedCountry, HttpStatus.CREATED);
    }
    
 // 更新資料
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNews(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "regionId", required = false) Long regionId) {
        Logger logger = LoggerFactory.getLogger(CountryController.class);
        try {
            CountryEntity existingcountry = countryService.getCountryById(id);

            // 更新國家名稱
            if (name != null) {
                existingcountry.setName(name);
            }

         // 更新地區關聯
            Set<RegionEntity> regions = existingcountry.getRegions();
            if (regionId != null) {
                // 獲取要關聯的 RegionEntity
                RegionEntity region = regionService.getRegionById(regionId);
                if (region == null) {
                    logger.info("沒有這個地區ID: {}", regionId);
                    return new ResponseEntity<>("地區不存在", HttpStatus.NOT_FOUND);
                }
                regions.add(region); // 添加地區
            } else {
                // 如果 regionId 為 null，清空地區
                regions.clear();
            }


            // 保存更新到資料庫
            CountryEntity updatedCountry = countryService.saveCountry(existingcountry);
            logger.info("國家資料已更新：" + updatedCountry.toString());

            return new ResponseEntity<>(updatedCountry, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("更新國家資料失敗", e);
            return new ResponseEntity<>("更新國家資料失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // 刪除資料
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCountry(@PathVariable Long id) {
    	Logger logger = LoggerFactory.getLogger(CountryController.class);
    	try {
    		// (!!!前端注意)先刪除所有相關的 regions、Cases 記錄才能刪除國家，但是這樣會造成所有資料損失
            countryService.deleteCountry(id);
            logger.info("國家資料已刪除，ID：" + id);
            return new ResponseEntity<>("國家資料已成功刪除", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("刪除國家資料失敗", e);
            return new ResponseEntity<>("國家資料刪除失敗",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<CountryDto>> getAllCountriesWithRegionsAndCases() {
        List<CountryDto> countries = countryService.getCountriesWithRegionsAndCases();
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }
}
