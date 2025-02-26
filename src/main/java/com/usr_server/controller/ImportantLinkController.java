package com.usr_server.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.usr_server.Entity.ImportantLinkEntity;
import com.usr_server.service.ImportLinkService;

@RestController
@RequestMapping("/api/importLink")
public class ImportantLinkController {

	@Autowired
	private ImportLinkService importLinkService;
	
	//獲取全部資料
	@GetMapping("/allImportLink")
	public List<ImportantLinkEntity> getAllImportantLinks(){
		List<ImportantLinkEntity> importLink = importLinkService.getAllImportantLink();
		if(importLink == null || importLink.isEmpty()) {
			System.out.println("未找到重要連結資料");
		}else {
			System.out.println("找到全部重要連結資料");
		}
		return importLink;
		
	}
	
	//獲取特定ID資料
	@GetMapping("/{id}")
	public ResponseEntity<?> getImportLinkById(@PathVariable Long id){
		try {
			ImportantLinkEntity importantlink = importLinkService.getImportLinkById(id);
			return new ResponseEntity<>(importantlink,HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("找不到此ID的重要連結",HttpStatus.BAD_REQUEST);
		}
	}
	
	//創建
	@PostMapping("/create")
	public ResponseEntity<ImportantLinkEntity> createImportLink(
			@RequestParam String name,
			@RequestParam String link){
		ImportantLinkEntity importlink = new ImportantLinkEntity();
		importlink.setName(name);
		importlink.setLink(link);
		ImportantLinkEntity savedImportLink = importLinkService.saveImportLinks(importlink);
		return ResponseEntity.ok(savedImportLink);
	}
	
	//修改
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateImportLink(
			@PathVariable Long id,
			@RequestParam(value = "name",required = false) String name,
			@RequestParam(value = "link",required = false) String link){
		
		Logger logger = LoggerFactory.getLogger(ImportantLinkController.class);
		try {
			// 查詢重要連結資料是否存在
			ImportantLinkEntity existingImportLink = importLinkService.getImportLinkById(id);
								
				if(existingImportLink == null) {
				return new ResponseEntity<>("重要連結資料不存在",HttpStatus.NOT_FOUND);
				}
				
				//更新連結名
				if(name != null) {
					existingImportLink.setName(name);
				}
				
				//更新連結
				if(link!=null) {
					existingImportLink.setLink(link);
				}
				
				//保存到資料庫
				ImportantLinkEntity updatedImportLink = importLinkService.saveImportLinks(existingImportLink);
				logger.info("重點連結資料已更新:"+updatedImportLink.toString());
				
				return new ResponseEntity<>(updatedImportLink, HttpStatus.OK);
			}catch(Exception e) {
				logger.error("更新重要連結資料失敗",e);
				return new ResponseEntity<>("更新重要連結資料失敗",HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	
	//刪除
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteImportLink(@PathVariable Long id){
		try {
			//查詢連結是否存在
			ImportantLinkEntity ImportLink = importLinkService.getImportLinkById(id);
			if(ImportLink == null) {
				//404
				return new ResponseEntity<>("重要連結不存在",HttpStatus.NOT_FOUND);
			}
			
			//刪除資料庫中的實體
			importLinkService.deleteImportantLinkS(id);
			return new ResponseEntity<>("重要連結已刪除成功",HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("刪除重要連結失敗",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	}
