package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/4/14.
 */
public class GetFreeCommodityResponse extends GeneralResponse{
    public List<BaseCommodityInfo> commodities;

    public GetFreeCommodityResponse(List<BaseCommodityInfo> commodities) {
        this.commodities = commodities;
    }
}
