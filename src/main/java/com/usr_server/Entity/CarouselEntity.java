package com.usr_server.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity // 標記成JPA實體
@Data // 自動生成getter和setter//沒用會謝
@Table(name = "carousel") // 確保表名為小寫
public class CarouselEntity {
    @Id // 主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carousel_id")
    private Long id;

    @Column(name = "carousel_name")
    private String imageName;

    @Column(name = "carousel_img")
    private String image;

    @Column(name = "carousel_date", updatable = false)
    private LocalDateTime carouselDate;

    @PrePersist
    protected void onCreate() {
        carouselDate = LocalDateTime.now();
    }
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public LocalDateTime getCarouselDate() {
        return carouselDate;
    }

    public void setCarouselDate(LocalDateTime carouselDate) {
        this.carouselDate = carouselDate;
    }
}
