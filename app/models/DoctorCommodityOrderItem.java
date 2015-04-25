package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/6.
 */
@Entity
@Table(name = "doctor_order_comodity")
public class DoctorCommodityOrderItem {
    @Id
    @GeneratedValue
    public Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    public DoctorCommodityOrder order;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    public Commodity commodity;

    public Integer priceOfPoint;
    public Integer priceOfCash;

    public Integer quantity;
}
