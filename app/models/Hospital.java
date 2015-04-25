package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/3/2.
 */
@Entity
@Table(name = "hospital",
       indexes = {@Index(name = "index_hospital_name", columnList = "name", unique = true),
                  @Index(name = "index_hospital_city", columnList = "city")  }
)
public class Hospital {
    @Id
    @GeneratedValue
    public Long id;

    @Column(name = "name", nullable = false)
    public String name;

    public Double longitude;
    public Double latitude;

    public String address;
    public String city;


    public Hospital() {
    }

    public Hospital(String name) {
        this.name = name;
    }
}
