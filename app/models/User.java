package models;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by Ronald on 2015/3/9.
 */

@Entity
@Table(name = "user",
        indexes = {@Index(name = "index_user_username", columnList = "userName"),
                   @Index(name="index_user_phone", columnList = "phoneNumber"),
                    @Index(name = "index_user_jjd", columnList = "jjd")}
)
public class User {
    @Id
    @GeneratedValue
    public Long id;
    @Column(name = "userName", nullable = false)
    public String userName;
    public String password;
    @Column(name = "jjd")
    public String jid;
    public String name;
    public String nickame;
    public String sessionKey;
    public String phoneNumber;
    public String email;
    public String clientID;
    public Integer status;
    public Integer previlege;
    public String avatar;
    public Integer points;
    public Integer balance;
    public String xmppToken;
    public Integer deviceType = 0;
    @OneToMany(mappedBy = "user", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<Patient> patients;
    @OneToMany (mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<HealthRecord> records;
    public Long createTime;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    Set<FriendItem> friends;
}
