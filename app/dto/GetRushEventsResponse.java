package dto;

import models.RushEvent;

import java.util.List;

/**
 * Created by Ronald on 2015/4/13.
 */
public class GetRushEventsResponse extends GeneralResponse{
    public List<RushEventInfo> events;

    public GetRushEventsResponse(List<RushEventInfo> events) {
        this.events = events;
    }
}
