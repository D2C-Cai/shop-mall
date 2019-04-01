package com.d2c.shop.customer.api.handler.impl;

import cn.hutool.core.date.DateUtil;
import com.d2c.shop.customer.api.handler.OrderHandler;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.order.model.CrowdGroupDO;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.product.model.ProductDO;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Cai
 */
@Component
public class OrderCrowdHandler implements OrderHandler {

    @Override
    public void operator(OrderDO order, Object... conditions) {
        OrderItemDO orderItem = order.getOrderItemList().get(0);
        MemberDO member = (MemberDO) conditions[0];
        ProductDO product = orderItem.getProduct();
        // 参与拼团活动
        if (product != null && order.getCrowdId() != null) {
            Asserts.eq(product.crowding(), true, "该商品不符合拼团购买条件");
            orderItem.setRealPrice(product.getCrowdPrice());
            order.setType(OrderDO.TypeEnum.CROWD.name());
            CrowdGroupDO crowdGroup = order.getCrowdGroup();
            if (crowdGroup == null) {
                crowdGroup = new CrowdGroupDO();
                crowdGroup.setShopId(order.getShopId());
                crowdGroup.setProductId(orderItem.getProductId());
                crowdGroup.setProductName(orderItem.getProductName());
                crowdGroup.setProductPic(orderItem.getProductPic());
                crowdGroup.setStandard(orderItem.getStandard());
                crowdGroup.setProductPrice(orderItem.getProductPrice());
                crowdGroup.setCrowdPrice(orderItem.getRealPrice());
                crowdGroup.setCrowdNum(product.getCrowdGroupNum());
                crowdGroup.setDeadline(DateUtil.offsetHour(new Date(), product.getCrowdGroupTime()));
                crowdGroup.setVirtual(orderItem.getVirtual());
                crowdGroup.setAttendNum(1);
                crowdGroup.setPaidNum(0);
                crowdGroup.setStatus(0);
                crowdGroup.setAvatars(crowdGroup.pushAvatars(member.getId(), member.getNickname(), member.getAvatar()));
                order.setCrowdGroup(crowdGroup);
            } else {
                crowdGroup.setAvatars(crowdGroup.pushAvatars(member.getId(), member.getNickname(), member.getAvatar()));
                order.setCrowdId(order.getCrowdGroup().getId());
                orderItem.setCrowdId(order.getCrowdGroup().getId());
            }
        }
    }

}
