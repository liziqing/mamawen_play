package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/10.
 */
@Entity
public class FriendItem {
    @Id
    @GeneratedValue
    public Long id;

    public Integer status = 1;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    public Doctor doctor;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    public User user;
    public Long createTime;
}
