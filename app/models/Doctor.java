package models;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ronald on 2015/3/2.
 */
@Entity
@Table(name = "doctor",
       indexes = {@Index(name = "index_doctor_level", columnList = "level"),
                  @Index(name = "index_doctor_phone", columnList = "phoneNumber", unique = true)}
)
public class Doctor {
    @Id
    @GeneratedValue
    public Long id;
    public String userName;
    public String password;
    public String name;
    public String jid;
    public String xmppToken;
    public String avatar;
    @Column(name = "phoneNumber", nullable = false)
    public String phoneNumber;
    public String email;
    public String gender;
    public Date birthday;
    public String clientID;
    public String title;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER,optional=true)
    public Department  department;
    public String graduated;
    public String goodAt;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.EAGER,optional=true)
    public Hospital inHospital;
    public String background;
    public String achievement;
    public Boolean recept = true;
    public Boolean serveMore = false;
    public Integer deviceType = 0;
    @Column(name = "level", nullable = false)
    public Integer level = 0;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "doctor_work_time",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "time_id")
    )
    public Set<OutPatientTime> workTimes = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctor", fetch = FetchType.LAZY)
    Set<FriendItem> friends;
    public String licenceUrl;
    public String plateUrl;
    public Boolean freeServing = true;
    public Integer txtDiagFee;
    public Integer phnDiaFee;
    public Integer prvtDiaFee;
    public Integer diagPlusFee;
    public Integer voltrDay;
    public Integer freeCount;
    public String sessionKey;
    public Long createTime;
    public Long updateTime;
    public Long lastActiveTime;
}
