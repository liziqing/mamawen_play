package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ronald on 2015/3/3.
 */
@Entity
@Table(name = "inquiry",
       indexes = {@Index(name = "index_inquiry_patient", columnList = "user_id"),
                 @Index(name = "index_inquiry_doctor", columnList = "doctor_id"),
                 @Index(name = "index_inquiry_create_time", columnList = "createTime"),
                 @Index(name = "index_inquiry_update_time", columnList = "updateTime")
      }
)
public class Inquiry {
    @Id
    @GeneratedValue
    public Long id;

    public String description;

    public String content;
    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER,optional=true)
    public User user;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER,optional=true)
    public Doctor doctor;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER, optional = true)
    public Department depart;

    public Integer point;
    public String drug;
    public String photoes;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "inquiry_tag",
            joinColumns = @JoinColumn(name = "inquiry_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    public Set<Tag> tags = new HashSet<>();
    public Integer category = 0;
    public  Boolean finished = false;
    public Long assigned = 0l;
    public Boolean recepted = false ;
    public Integer level = 1;
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    public InquiryReport report;
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "inquiry", cascade = CascadeType.ALL, optional = true)
    UserMarkRecord markRecord;
    public Long createTime;
    public Long updateTime;
}
