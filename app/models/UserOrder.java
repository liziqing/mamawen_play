package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/14.
 */
@Entity
@Table(name = "user_order", indexes = {@Index(name = "user_order_code", columnList = "orderCode")})
public class UserOrder {
    @Id
    @GeneratedValue
    public Long id;

    public String orderCode;
    public Double price;
    public Integer category;
    public Integer status = 0;
    public Integer feeType;
    public Integer quantity;
    public Double feeUnit;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    public Doctor doctor;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    public User user;

}
