package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/15.
 */
@Entity
@Table(name = "receiving_info")
public class DoctorCommodityReceivingInfo {
    @Id
    @GeneratedValue
    public Long id;

    public String receiver;
    public String phoneNumber;
    public String address;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    Doctor doctor;
}
