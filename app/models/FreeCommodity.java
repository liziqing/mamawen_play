package models;

import javax.annotation.Generated;
import javax.persistence.*;

/**
 * Created by Ronald on 2015/4/13.
 */
@Entity
public class FreeCommodity {
    @Id
    @GeneratedValue
    public Long id;

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    public Commodity commodity;
}
