package models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Ronald on 2015/4/7.
 */
@Entity
@Table(name = "rush_event", indexes = {@Index(name = "rush_event_time", columnList = "startTime")})
public class RushEvent {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "events", fetch = FetchType.LAZY)
    public List<Commodity> goods;

    public Date startTime;
}
