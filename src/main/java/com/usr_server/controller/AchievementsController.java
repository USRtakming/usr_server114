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

import com.usr_server.Entity.AchievementsEntity;
import com.usr_server.service.AchievementsService;




@RestController
@RequestMapping("/api/achievements")
public class AchievementsController {
	
	@Autowired
    private AchievementsService achievementsService;
    
    // 保存文件的路徑，可以根據需要進行修改
    private static String UPLOAD_DIR = "src/main/resources/static/uploads/AchievementsImage";

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();
    static {
        MIME_TYPE_MAP.put("jpg", "image/jpeg");
        MIME_TYPE_MAP.put("jpeg", "image/jpeg");
        MIME_TYPE_MAP.put("png", "image/png");
        MIME_TYPE_MAP.put("gif", "image/gif");
    }
    
//    查全部
//    http://localhost:xxxx/api/achievements/allAchievements
    @GetMapping("/allAchievements")
    public List<AchievementsEntity> getAllAchievements(){
    	List<AchievementsEntity> achievements = achievementsService.getAllachievements();
    	if(achievements == null || achievements.isEmpty()) {
    		System.out.println("未找到成果展現數據");
    	}else {
    		System.out.println("找到成果展現數據");
    	}
    	return achievements;
    }
    
//    Id查詢
    @GetMapping("/{id}")
    public ResponseEntity<?> getAchievementsById(@PathVariable Long id){
    	try {
    		AchievementsEntity achievemants = achievementsService.getAchievementsById(id);
    		return new ResponseEntity<>(achievemants, HttpStatus.OK);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return new ResponseEntity<>("找不到這則成果展現", HttpStatus.BAD_REQUEST);
    	}
    }
    
//    創建
    @PostMapping("/create")
    public ResponseEntity<?> createAchievemants(@RequestParam("image") MultipartFile file,
    		@RequestParam("activity")String activity,
    		@RequestParam("link")String link,
    		@RequestParam("aname")String aname){
    	
    		Logger logger = LoggerFactory.getLogger(AchievementsController.class);
    		
	    	if (file.isEmpty()) {
	    		logger.error("文件為空");
	            return new ResponseEntity<>("請選擇要上傳的文件", HttpStatus.BAD_REQUEST);
	        }
	    	
	    	Path uploadPath = Paths.get(UPLOAD_DIR);
	        if (!Files.exists(uploadPath)) {
	            try {
	                Files.createDirectories(uploadPath);
	                logger.info("創建上傳目錄：" + uploadPath.toString());
	            } catch (IOException e) {
	                logger.error("創建上傳目錄失敗", e);
	                return new ResponseEntity<>("創建上傳目錄失敗", HttpStatus.INTERNAL_SERVER_ERROR);
	            }
	        }
	        
	        try (InputStream inputStream = file.getInputStream()) {
	            String fileName = file.getOriginalFilename();
	            Path filePath = uploadPath.resolve(fileName);

	            // 覆蓋已存在的文件
	            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	            logger.info("文件上傳成功並覆蓋：" + filePath.toString());

	            // 創建 AchievementsEntity 並設置屬性
	            AchievementsEntity achievemants = new AchievementsEntity();
	           
				achievemants.setName(aname);
	            achievemants.setImage(fileName); // 設置圖片的 URL
	            achievemants.setActivitylDate(activity);
	            achievemants.setIink(link);


	            AchievementsEntity saveAchievements = achievementsService.saveAchievements(achievemants);
	            logger.info("成果展現已保存：" + saveAchievements.toString());

	            return new ResponseEntity<>(saveAchievements, HttpStatus.CREATED);
	        } catch (IOException e) {
	            logger.error("成果展現創建失敗", e);
	            return new ResponseEntity<>("成果展現創建失敗", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
    	
    }
    
    
//  查圖片
	  @GetMapping("/image/{filename:.+}")
	  public ResponseEntity<?> getImage(@PathVariable String filename) {
	      Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
	
	      if (Files.exists(filePath)) {
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
	    public ResponseEntity<?> updateAchievements(
	        @PathVariable Long id,
	        @RequestParam(value = "image", required = false) MultipartFile file,
	        @RequestParam(value = "aname", required = false) String aname,
	        @RequestParam(value ="activity", required = false)String activity,
    		@RequestParam(value ="link", required = false)String link) {
	
	        Logger logger = LoggerFactory.getLogger(AchievementsController.class);
	
	        try {
	        	//查詢成果展現是否存在
	        	AchievementsEntity existingAchievements = achievementsService.getAchievementsById(id);
	
	            if (existingAchievements == null) {
	                return new ResponseEntity<>("成果展現資料不存在", HttpStatus.NOT_FOUND);
	            }
	            //更新圖名
	            if (aname != null) {
	            	existingAchievements.setName(aname);
	            }
	            
	            //更新活動日期
	            if (activity != null) {
	            	existingAchievements.setActivitylDate(activity);
	            }
	            
	            //更新活動日期
	            if (link != null) {
	            	existingAchievements.setIink(link);
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
	                    existingAchievements.setImage(fileName); // 更新圖片 URL
	                } catch (IOException e) {
	                    logger.error("文件上傳失敗", e);
	                    return new ResponseEntity<>("文件上傳失敗", HttpStatus.INTERNAL_SERVER_ERROR);
	                }
	            }
	            //保存更新到資料庫
	            AchievementsEntity updatedAchievements = achievementsService.saveAchievements(existingAchievements);
	            logger.info("成果展現資料已更新：" + updatedAchievements.toString());
	
	            return new ResponseEntity<>(updatedAchievements, HttpStatus.OK);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ResponseEntity<>("更新成果展現失敗", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	    
// 刪除
	    @DeleteMapping("/delete/{id}")
	    public ResponseEntity<?> deleteAchievements(@PathVariable Long id) {
	        Logger logger = LoggerFactory.getLogger(AchievementsController.class);

	        try {
	        	//查詢成果展現資料是否存在
	        	AchievementsEntity achievements = achievementsService.getAchievementsById(id);
	            if (achievements == null) {
	            	//404
	                return new ResponseEntity<>("成果展現資料不存在", HttpStatus.NOT_FOUND);
	            }

	            // 刪除圖片文件
	            Path filePath = Paths.get(UPLOAD_DIR).resolve(achievements.getImage());
	            if (Files.exists(filePath)) {
	                Files.delete(filePath);
	                logger.info("已刪除圖片文件：" + filePath.toString());
	            }

	            // 刪除資料庫中的實體
	            achievementsService.deleteAchievements(id);
	            return new ResponseEntity<>("成果展現資料已刪除成功", HttpStatus.OK);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ResponseEntity<>("刪除成果展現資料失敗", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
    
    
    
    
    
    
    
    
    
}
