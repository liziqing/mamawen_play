package dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Ronald on 2015/3/19.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorInformationRequest {
    public String name;
    public String hospital;
    public String department;
    public String title;
}
