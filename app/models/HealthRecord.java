package models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Ronald on 2015/4/16.
 */
@Entity
@Table(name = "health_record",
       indexes = {@Index(name = "health_index_cat", columnList = "category"), @Index(name = "health_index_subcat", columnList = "subCategory")})
public class HealthRecord {
    @Id
    @GeneratedValue
    public Long id;

    public String category;
    public String subCategory;
    public Double value;
    public Integer cycle;
    public Date recordDate;
    public Long createTime;
    public Long updateTime;
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    public User user;
}
