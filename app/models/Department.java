package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/3/2.
 */
@Entity
@Table(name = "department",
    indexes = {@Index(name = "index_department_name", columnList = "name", unique = true)}
)
public class Department {
    @Id
    @GeneratedValue
    public Long id;

    @Column(name = "name", nullable = false)
    public String name;

    public Department(String name) {
        this.name = name;
    }

    public Department() {
    }

}
