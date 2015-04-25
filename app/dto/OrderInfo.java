package dto;

import models.DoctorCommodityOrder;
import models.DoctorCommodityOrderItem;

import java.util.Iterator;

/**
 * Created by Ronald on 2015/4/11.
 */
public class OrderInfo {
    public String orderCode;
    public Long commodity;
    public Integer category;
    public String name;
    public String description;
    public String extrInfo;
    public Integer priceType;
    public Double price;
    public String status;
    public Integer quantity;
    public String receiver;
    public String address;
    public String phoneNumber;
    public String comment;
    public String expCompany;
    public String expFeePayer;
    public static OrderInfo getOrderInfo(DoctorCommodityOrder order){
        OrderInfo info = new OrderInfo();
        info.orderCode = order.orderCode;
        info.category = order.category;
        info.receiver = order.receiver;
        info.address = order.address;
        info.phoneNumber = order.phoneNumber;
        info.comment = order.comment;
        info.expCompany = order.expCompany;
        info.expFeePayer = order.expFeePayer;
        info.extrInfo = order.extraInfo;
        info.price = order.price;
        info.priceType = order.priceType;
        info.status = getOrderStatus(order.status);
        if (order.category == 1 && order.items.size() > 0){
            Iterator<DoctorCommodityOrderItem> it = order.items.iterator();
            DoctorCommodityOrderItem item = it.next();
            info.name = item.commodity.name;
            info.commodity = item.commodity.id;
            info.description = item.commodity.description;
            info.quantity = item.quantity;
        }
        return info;
    }
    public static String getOrderStatus(Integer status){
        switch (status){
            case 0:
                return "未支付";
            case 1:
                return "已发货";
            case 2:
                return "已收货";
            default:
                return "";
        }
    }
}
