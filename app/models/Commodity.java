package models;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by Ronald on 2015/4/8.
 */
@Entity
@Table(name = "commodity")
public class Commodity {
    @Id
    @GeneratedValue
    public Long id;

    public String description;
    public String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy ="commodity", fetch = FetchType.EAGER)
    public List<CommodityCategory> categories;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "commodity_event",
            joinColumns = @JoinColumn(name = "commodity_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    Set<RushEvent> events;

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST}, mappedBy = "commodity", fetch = FetchType.LAZY)
    public FreeCommodity freeItem;
    public Integer stock;
    public Integer pointPrice;
    public Integer cashPrice;
    public Integer discPointPrice;
    public Integer discCashPrice;
    public Integer priceType;
    public String comparison;
    public Integer compPrice;
    public String discount;
    public String thumbUrl;
    public String slidePicUrl;
}
