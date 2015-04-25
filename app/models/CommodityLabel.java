package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/7.
 */
@Entity
@Table(name = "commodity_label")
public class CommodityLabel {
    @Id
    @GeneratedValue
    public Long id;

    public String label;
    public Integer stock;
    public String photos;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    CommodityCategory category;
}
