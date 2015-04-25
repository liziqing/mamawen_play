package models;

import javax.persistence.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Ronald on 2015/4/14.
 */
@Entity
@Table(name = "commodity_cat")
public class CommodityCategory {
    @Id
    @GeneratedValue
    public Long id;

    public Integer cat;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category", fetch = FetchType.EAGER)
    public  List<CommodityLabel> labels;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    Commodity commodity;

}
