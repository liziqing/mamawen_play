package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/22.
 */
@Entity
public class UserMarkRecord {
    @Id
    @GeneratedValue
    public Long id;

    public Integer servingMark;
    public Integer efftMark;
    public String comment;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    public Inquiry inquiry;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    public User user;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    public Doctor doctor;
}
