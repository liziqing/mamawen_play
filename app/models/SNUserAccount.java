package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/15.
 */
@Entity
@Table(name = "sn__user_account",
        indexes = {@Index(name = "sn_id", columnList = "snID"),
                   @Index(name = "sn_toekn", columnList = "snToken")})
public class SNUserAccount {
    @Id
    @GeneratedValue
    public Long id;

    public String name;
    public String category;
    public String snID;
    public String snToken;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    User user;

}
