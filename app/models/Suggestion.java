package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/22.
 */
@Entity
@Table(name = "suggestion")
public class Suggestion {
    @Id
    @GeneratedValue
    public Long id;

    @Column(name = "content", length = 500)
    public String content;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    public User user;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    public Doctor doctor;

}
