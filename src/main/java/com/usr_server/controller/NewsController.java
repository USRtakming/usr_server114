package com.usr_server.controller;


import java.util.List;

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

import com.usr_server.Entity.NewsEntity;
import com.usr_server.service.NewsService;



@RestController
@RequestMapping("/api/news")
public class NewsController {
	@Autowired
    private NewsService newsService;
	
//  查全部
//  http://localhost:xxxx/api/achievements/allAchievements
  @GetMapping("/allNews")
  public List<NewsEntity> getAllNews(){
  	List<NewsEntity> news = newsService.getAllNews();
  	if(news == null || news.isEmpty()) {
  		System.out.println("未找到亮點報導資料");
  	}else {
  		System.out.println("找到全部亮點報導資料");
  	}
  	return news;
  }
  
//Id查詢
	@GetMapping("/{id}")
	public ResponseEntity<?> getNewsById(@PathVariable Long id){
		try {
			NewsEntity news = newsService.getNewsById(id);
			return new ResponseEntity<>(news, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("找不到此ID的亮點報導", HttpStatus.BAD_REQUEST);
		}
	}
	
//	創建
	@PostMapping("/create")
	public ResponseEntity<NewsEntity> createNews(
            @RequestParam String name,
            @RequestParam String link) {
        NewsEntity news = new NewsEntity();
        news.setName(name);
        news.setLink(link);
        NewsEntity savedNews = newsService.saveNews(news);
        return ResponseEntity.ok(savedNews);
    }
	
//	修改
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateNews(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "link", required = false) String link) {
		
		Logger logger = LoggerFactory.getLogger(NewsController.class);
        try {
        	// 查詢亮點報導資料是否存在
            NewsEntity existingNews = newsService.getNewsById(id);

            if (existingNews == null) {
                return new ResponseEntity<>("亮點報導資料不存在", HttpStatus.NOT_FOUND);
            }

            // 更新報導名
            if (name != null) {
                existingNews.setName(name);
            }

            // 更新報導連結
            if (link != null) {
                existingNews.setLink(link);
            }

            // 保存更新到資料庫
            NewsEntity updatedNews = newsService.saveNews(existingNews);
            logger.info("亮點報導資料已更新：" + updatedNews.toString());

            return new ResponseEntity<>(updatedNews, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("更新亮點報導資料失敗", e);
            return new ResponseEntity<>("更新亮點報導資料失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	
	
// 	刪除
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAchievements(@PathVariable Long id) {

        try {
        	//查詢亮點報導是否存在
        	NewsEntity news = newsService.getNewsById(id);
            if (news == null) {
            	//404
                return new ResponseEntity<>("亮點報導不存在", HttpStatus.NOT_FOUND);
            }

            // 刪除資料庫中的實體
            newsService.deleteNews(id);
            return new ResponseEntity<>("亮點報導已刪除成功", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("刪除亮點報導失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	
	
	
	
	
}
