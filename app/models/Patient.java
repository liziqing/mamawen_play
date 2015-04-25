package models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Ronald on 2015/3/3.
 */
@Entity
@Table(name = "patient"
)
public class Patient {
    @Id
    @GeneratedValue
    public Long id;
    public String name;
    public String gender;
    public String birthday;
    public Integer status = 1;
    public Double height;
    public Double weight;
    public Double pastWeight;
    public Integer mensCycle;
    public String lastMensDate;
    public String exptBirthday;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY, optional = false)
    public User user;
    public Long createTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        if (name != null ? !name.equals(patient.name) : patient.name != null) return false;
        if (status != null ? !status.equals(patient.status) : patient.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
