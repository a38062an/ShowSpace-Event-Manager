package anthonynguyen.showspace.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;
	
	private double longitude;
	private double latitude;

	@Column(columnDefinition = "boolean default true")
	private boolean addressChanged = true;
	
	@OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Event> events;
	
	@NotBlank(message = "Please enter a road name.")
	@Column(length = 300)
    @Size(max = 299, message = "Road name must be less than 300 characters.")
    private String roadName;  // Separate road name

	@Pattern(regexp = "^[A-Z]{1,2}[0-9]{1,2}[A-Z0-9]?\\s?[0-9][A-Z]{2}$", 
	         message = "Please enter a valid UK postcode.")
    private String postcode;  // Separate postcode

	
	@NotBlank(message = "Please enter a venue name.")
    @Size(max = 255, message = "Venue name must be less than 256 characters.")
    private String name;
	
	@NotNull(message = "Please enter a capacity.")
    @Min(value = 1, message = "Capacity must be a positive integer.")
    private Integer capacity;
	

	public Venue() {
	}
	
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	
	
	public String getRoadName() {
		return roadName;
	}
	
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	
	public String getPostcode() {
		return postcode;
	}
	
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public List<Event> getEvents() {
		return events;
	}
	
	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setAddressChanged(boolean addressChanged) {
		this.addressChanged = addressChanged;
	}

	public boolean isAddressChanged() {
		return addressChanged;
	}
	
	

}
