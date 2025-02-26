package com.usr_server.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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
import com.usr_server.Entity.CasesEntity;
import com.usr_server.Entity.RegionEntity;
import com.usr_server.service.CasesService;
import com.usr_server.service.RegionService;

@RestController
@RequestMapping("/api/cases")
public class CasesController {
	
	@Autowired
	private CasesService casesService;
	@Autowired
	private RegionService regionService;
	
	// 保存文件的路徑，可以根據需要進行修改
    private static String UPLOAD_DIR = "src/main/resources/static/uploads/CasesImage";

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();
    static {
        MIME_TYPE_MAP.put("jpg", "image/jpeg");
        MIME_TYPE_MAP.put("jpeg", "image/jpeg");
        MIME_TYPE_MAP.put("png", "image/png");
        MIME_TYPE_MAP.put("gif", "image/gif");
    }
    
//  查全部資料
    @GetMapping("/allCases")
    public List<CasesEntity> getAllCases(){
    	List<CasesEntity> cases = casesService.getAllCases();
    	if(cases == null || cases.isEmpty()) {
    		System.out.println("未找到足跡案例");
    	}else {
    		System.out.println("找到足跡案例");
    	}
    	return cases;
    }
    
//  用ID查
    @GetMapping("/{id}")
	public ResponseEntity<?> getCasesById(@PathVariable Long id){
		try {
			CasesEntity cases = casesService.getCasesById(id);
			return new ResponseEntity<>(cases, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("找不到此ID的服務足跡", HttpStatus.BAD_REQUEST);
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
    
//	創建
    @PostMapping("/create")
    public ResponseEntity<CasesEntity> createCases(
            @RequestParam("title") String title,
            @RequestParam("image") MultipartFile file,
            @RequestParam("pdfLink") String pdfLink,
            @RequestParam("content_date") String contentDate,
            @RequestParam("regionId") Long regionId) {

        Logger logger = LoggerFactory.getLogger(CasesController.class);

        // 檢查文件是否為空
        if (file.isEmpty()) {
            logger.error("文件為空");
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }

        // 檢查並創建上傳目錄
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                logger.info("創建上傳目錄：" + uploadPath.toString());
            } catch (IOException e) {
                logger.error("創建上傳目錄失敗", e);
                return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // 處理文件上傳
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("文件上傳成功並覆蓋：" + filePath.toString());

            // 創建 CasesEntity 並設置屬性
            CasesEntity cases = new CasesEntity();
            cases.setTitle(title);
            cases.setImage(fileName);
            cases.setPdfLink(pdfLink);
            cases.setContentDate(contentDate);

            // 獲取並設置 RegionEntity
            RegionEntity region = regionService.getRegionById(regionId);
            if (region == null) {
                return new ResponseEntity<>( HttpStatus.NOT_FOUND);
            }
            cases.setRegion(region); // 設置關聯

            // 儲存案例並返回結果
            CasesEntity savedCase = casesService.saveCases(cases);
            logger.info("案例已保存：" + savedCase.toString());

            return new ResponseEntity<>(savedCase, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("案例創建失敗", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//	修改
	@PutMapping("/update/{id}")
    public ResponseEntity<?> updateCases(
    	@PathVariable Long id,
        @RequestParam(value = "title", required = false)String title,
  		@RequestParam(value = "image", required = false) MultipartFile file,
  		@RequestParam(value = "pdfLink", required = false)String pdfLink,
  		@RequestParam(value = "content_date", required = false)String content_date,
  		@RequestParam(value = "regions", required = false)Long regions) {

        Logger logger = LoggerFactory.getLogger(CasesController.class);

        try {
        	//查詢輪播圖是否存在
        	CasesEntity existingCases = casesService.getCasesById(id);

            if (existingCases == null) {
                return new ResponseEntity<>("足跡資料不存在", HttpStatus.NOT_FOUND);
            }
            
            if (title != null) {
            	existingCases.setTitle(title);
            }
            
            if (pdfLink != null) {
            	existingCases.setPdfLink(pdfLink);
            }
            
            if (content_date != null) {
            	existingCases.setContentDate(content_date);
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
                    // 更新輪播圖實體中的圖片名稱
                    existingCases.setImage(fileName); // 更新圖片 URL
                } catch (IOException e) {
                    logger.error("文件上傳失敗", e);
                    return new ResponseEntity<>("文件上傳失敗", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            //保存更新到資料庫
            CasesEntity updatedCases = casesService.saveCases(existingCases);
            logger.info("足跡資料已更新：" + existingCases.toString());

            return new ResponseEntity<>(updatedCases, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("更新足跡失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
//  刪除
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCases(@PathVariable Long id) {
        Logger logger = LoggerFactory.getLogger(CasesController.class);

        try {
        	//查詢成果展現資料是否存在
        	CasesEntity cases = casesService.getCasesById(id);
            if (cases == null) {
            	//404
                return new ResponseEntity<>("足跡資料不存在", HttpStatus.NOT_FOUND);
            }

            // 刪除圖片文件
            Path filePath = Paths.get(UPLOAD_DIR).resolve(cases.getImage());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("已刪除圖片文件：" + filePath.toString());
            }

            // 刪除資料庫中的實體
            casesService.deleteCases(id);
            return new ResponseEntity<>("足跡資料已刪除成功", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("刪除足跡資料失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
