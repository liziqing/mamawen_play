package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Ronald on 2015/3/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetVolunteerDoctorRequest {
    public String department;
    //List<Integer> weekDays;
}
