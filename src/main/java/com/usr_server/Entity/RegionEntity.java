package com.usr_server.Entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Regions")
public class RegionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "region_id")
	private Long id;
	
	@Column(name = "region_name", nullable = false)
	private String name;
	
	@ManyToOne
    @JoinColumn(name = "country_id")
	@JsonIgnore
    private CountryEntity country;
	
	@OneToMany(mappedBy = "region", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<CasesEntity> cases = new HashSet<>();
	
	// Getters and Setters
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public CountryEntity getCountry() {
		return country;
	}
	
	public void setCountry(CountryEntity country) {
		this.country = country;
	}
	
	public Set<CasesEntity> getCases() {
        return cases;
    }
    
    public void setCases(Set<CasesEntity> cases) {
        this.cases = cases;
    }

}
