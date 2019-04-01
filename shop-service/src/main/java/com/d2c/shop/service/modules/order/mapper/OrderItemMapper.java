package com.d2c.shop.service.modules.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.order.query.OrderItemQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author BaiCai
 */
public interface OrderItemMapper extends BaseMapper<OrderItemDO> {

    Map<String, Object> countDaily(@Param("query") OrderItemQuery query);

}
