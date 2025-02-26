package com.usr_server.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.usr_server.Entity.HomeMapsEntity;
import com.usr_server.service.HomeMapsService;
import com.usr_server.service.RegionService;

@RestController
@RequestMapping("/api/homemaps")
public class HomeMapController{
	
	@Autowired
	private HomeMapsService homemapsService;
	@Autowired
	private RegionService regionService;
	
	//保存文件的路徑，可以根據需要進行修改
	private static String UPLOAD_DIR = "src/main/resources/static/uploads/HomeMapImage";
	
	private static final Map<String,String> MIME_TYPE_MAP = new HashMap<>();
	static {
        MIME_TYPE_MAP.put("jpg", "image/jpeg");
        MIME_TYPE_MAP.put("jpeg", "image/jpeg");
        MIME_TYPE_MAP.put("png", "image/png");
        MIME_TYPE_MAP.put("gif", "image/gif");
    }
	
//  查全部
//  http://localhost:xxxx/api/homemaps/allHomeMap
	@GetMapping("/allHomeMaps")
	public List<HomeMapsEntity> getAllHomeMaps(){
		List<HomeMapsEntity> homemaps = homemapsService.getAllHomeMaps();
		if(homemaps == null || homemaps.isEmpty()) {
			System.out.println("未找到成果展現數據");
    	}else {
    		System.out.println("找到成果展現數據");
    	}
		return homemaps;
	}
	
//  id查詢	
	@GetMapping("/{id}")
	public ResponseEntity<?> getHomeMapsById(@PathVariable Long id){
		try {
			HomeMapsEntity homemaps = homemapsService.getHomeMapsById(id);
			return new ResponseEntity<>(homemaps,HttpStatus.OK);
		}catch(Exception e) {
    		e.printStackTrace();
    		return new ResponseEntity<>("找不到這則內容", HttpStatus.BAD_REQUEST);
    	}
	}
	
//   創建
	@PostMapping("/create")
	public ResponseEntity<?> createHomeMaps(
			@RequestParam("aname")String aname,
      		@RequestParam("content_text")String contentText,
      		@RequestParam("image") MultipartFile file
    		){
		
		Logger logger = LoggerFactory.getLogger(HomeMapController.class);
		
		if(file.isEmpty()) {
			logger.error("圖片為空");
			return new ResponseEntity<>("請選擇要上傳的圖片",HttpStatus.BAD_REQUEST);
		}
		
		Path uploadPath = Paths.get(UPLOAD_DIR);
		if(!Files.exists(uploadPath)) {
			try {
				Files.createDirectories(uploadPath);
				logger.info("創建上傳目錄:"+uploadPath.toString());
			}catch(IOException e){
				logger.error("創建上傳目錄失敗",e);
				return new ResponseEntity<>("創建上傳目錄失敗",HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		try (InputStream inputStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // 覆蓋已存在的文件
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("文件上傳成功並覆蓋：" + filePath.toString());

            // 創建 AchievementsEntity 並設置屬性
            HomeMapsEntity homemaps = new HomeMapsEntity();
           
			homemaps.setAname(aname);
			homemaps.setContent_text(contentText);
			homemaps.setImage(fileName); // 設置圖片的 URL

            HomeMapsEntity saveHomeMaps = homemapsService.saveHomeMaps(homemaps);
            logger.info("首頁地圖已保存：" + saveHomeMaps.toString());

            return new ResponseEntity<>(saveHomeMaps, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("首頁地圖創建失敗", e);
            return new ResponseEntity<>("首頁地圖創建失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
//查圖片
	@GetMapping("/image/{filename:.+}")
	public ResponseEntity<?> getImage(@PathVariable String filename){
		Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
		
	if(Files.exists(filePath)) {
		try {
			// 不直接創建 FileInputStream，而是使用 Files.newInputStream 來獲取流
            InputStream inputStream = Files.newInputStream(filePath);
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            
	         // 使用 InputStreamResource 來處理響應
	            InputStreamResource resource = new InputStreamResource(inputStream);
	
	            // 返回響應
	            return ResponseEntity.ok()
	                    .contentType(MediaType.parseMediaType(mimeType))
	                    .body(resource);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return new ResponseEntity<>("讀取圖片失敗", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
		} else {
	        return new ResponseEntity<>("圖片不存在", HttpStatus.NOT_FOUND);
	    }
	}	
		
// 修改
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateHomeMaps(
        @PathVariable Long id,
        @RequestParam(value = "image", required = false) MultipartFile file,
        @RequestParam(value = "aname", required = false) String aname,
        @RequestParam(value ="content_text", required = false)String content_text) {

        Logger logger = LoggerFactory.getLogger(HomeMapController.class);

        try {
        	//查詢成果展現是否存在
        	HomeMapsEntity existingHomeMaps = homemapsService.getHomeMapsById(id);

            if (existingHomeMaps == null) {
                return new ResponseEntity<>("服務資料不存在", HttpStatus.NOT_FOUND);
            }
            //更新縣市名
            if (aname != null) {
            	existingHomeMaps.setAname(aname);
            }
            
            //更新內容
            if (content_text != null) {
            	existingHomeMaps.setContent_text(content_text);
            }
            
			//處理圖片文件
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    logger.info("創建上傳目錄：" + uploadPath.toString());
                }
				//上傳新圖片文件
                try (InputStream inputStream = file.getInputStream()) {
                    String fileName = file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);

                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("文件上傳成功並覆蓋：" + filePath.toString());
                    // 更新成果展現實體中的圖片名稱
                    existingHomeMaps.setImage(fileName); // 更新圖片 URL
                } catch (IOException e) {
                    logger.error("文件上傳失敗", e);
                    return new ResponseEntity<>("文件上傳失敗", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            //保存更新到資料庫
            HomeMapsEntity updatedHomeMaps = homemapsService.saveHomeMaps(existingHomeMaps);
            logger.info("首頁地圖資料已更新：" + updatedHomeMaps.toString());

            return new ResponseEntity<>(updatedHomeMaps, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("更新首頁地圖失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }		
		
		
 // 刪除
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteHomeMaps(@PathVariable Long id) {
        Logger logger = LoggerFactory.getLogger(HomeMapController.class);

        try {
        	//查詢成果展現資料是否存在
        	HomeMapsEntity homemaps = homemapsService.getHomeMapsById(id);
            if (homemaps == null) {
            	//404
                return new ResponseEntity<>("首頁地圖資料不存在", HttpStatus.NOT_FOUND);
            }

            // 刪除圖片文件
            Path filePath = Paths.get(UPLOAD_DIR).resolve(homemaps.getImage());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("已刪除圖片文件：" + filePath.toString());
            }

            // 刪除資料庫中的實體
            homemapsService.deleteHomemaps(id);
            return new ResponseEntity<>("首頁地圖資料已刪除成功", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("刪除首頁地圖資料失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }		
		
		
}
	
	
