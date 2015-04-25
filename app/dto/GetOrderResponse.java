package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/4/11.
 */
public class GetOrderResponse extends GeneralResponse{
    public List<OrderInfo> orders;

    public GetOrderResponse(List<OrderInfo> orders) {
        this.orders = orders;
    }
}
