package com.usr_server.service;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.usr_server.Entity.CarouselEntity;
import com.usr_server.Repository.CarouselRepository;


//對應repository在連接到Controller，例如findAll
@Service
public class CarouselService {
	@Autowired
    private CarouselRepository carouselRepository;
	
	
	// 取得全部資料
    public List<CarouselEntity> getAllCarousels(){
        return carouselRepository.findAll();
    }
    
    // 根據ID查詢資料
    public CarouselEntity getCarouselById(Long id) throws Exception{
        return carouselRepository.findById(id)
            .orElseThrow(() -> new Exception("找不到此輪播圖"));
    }
    
 // 創建一筆新資料
    public CarouselEntity createCarousel(CarouselEntity request) {
        CarouselEntity carousel = new CarouselEntity();
        carousel.setImageName(request.getImageName());
        carousel.setImage(request.getImage());
        return carouselRepository.save(carousel);
    }
    
 // 更新資料
    public CarouselEntity saveCarousel(CarouselEntity carousel){
    	Logger logger = LoggerFactory.getLogger(CarouselService.class);
        logger.info("save保存輪播圖: " + carousel.toString());
        return carouselRepository.save(carousel);
    	
    }

    
   
//    刪除
    public void deleteCarousel(Long id) {
        // 檢查是否存在該輪播圖
        if (carouselRepository.existsById(id)) {
            carouselRepository.deleteById(id);
        } else {
            throw new RuntimeException("輪播圖不存在");
        }
    }

    
} 
