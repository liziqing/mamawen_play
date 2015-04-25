package models;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Ronald on 2015/4/6.
 */
@Entity
@Table(name = "doctor_order",
       indexes = {@Index(name = "order_index_code", columnList = "orderCode")}
)
public class DoctorCommodityOrder {
    @Id
    @GeneratedValue
    public Long id;

    public String orderCode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.EAGER)
    public Set<DoctorCommodityOrderItem> items;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    public Doctor doctor;

    public Double price;
    public Integer priceType;
    public String extraInfo;
    public String expCompany;
    public String expFeePayer;
    public String address;
    public String comment;
    public Integer category;
    public Integer status = 0;
    public String phoneNumber;
    public String receiver;
    public Long createTime;
}
