package com.usr_server.Entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Achievements")
public class AchievementsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Achievements_id")
	private Long id;
	
	@Column(name = "Achievements_name")
    private String aname;
	
	@Column(name = "Achievements_img")
    private String image;
	
	@Column(name = "activity_date")
	private String activity;
	
	@Column(name = "Achievements_link")
	private String link;

	
	// Getters and Setters
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return aname;
    }

    public void setName(String aname) {
        this.aname = aname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivitylDate(String activity) {
        this.activity = activity;
    }
    
    public String getIink() {
        return link;
    }

    public void setIink(String link) {
        this.link = link;
    }
}
