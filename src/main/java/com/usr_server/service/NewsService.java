package com.usr_server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usr_server.Entity.NewsEntity;
import com.usr_server.Repository.NewsRepository;

@Service
public class NewsService {
	@Autowired
    private NewsRepository newsRepository;
	
	// 取得全部資料
    public List<NewsEntity> getAllNews(){
        return newsRepository.findAll();
    }
    
 // 根據ID查詢資料
    public NewsEntity getNewsById(Long id) throws Exception{
        return newsRepository.findById(id)
            .orElseThrow(() -> new Exception("找不到此亮點報導"));
    }
    
 // 更新資料
    public NewsEntity saveNews(NewsEntity news){
    	Logger logger = LoggerFactory.getLogger(NewsService.class);
        logger.info("save保存亮點報導: " + news.toString());
        return newsRepository.save(news);
    	
    }
    
//  刪除
  public void deleteNews(Long id) {
      // 檢查是否存在該輪播圖
      if (newsRepository.existsById(id)) {
    	  newsRepository.deleteById(id);
      } else {
          throw new RuntimeException("亮點報導不存在");
      }
  }
}
