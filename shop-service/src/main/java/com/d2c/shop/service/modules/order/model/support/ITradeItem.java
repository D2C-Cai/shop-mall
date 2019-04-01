package com.d2c.shop.service.modules.order.model.support;

/**
 * @author Cai
 */
public interface ITradeItem {

    Long getShopId();

    Long getMemberId();

    String getMemberAccount();

    Long getProductId();

    Long getProductSkuId();

    Integer getQuantity();

    String getStandard();

    String getProductName();

    String getProductPic();

}
