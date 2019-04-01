package com.d2c.shop.business.api;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.constant.PrefixConstant;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.order.query.OrderItemQuery;
import com.d2c.shop.service.modules.order.query.OrderQuery;
import com.d2c.shop.service.modules.order.service.OrderItemService;
import com.d2c.shop.service.modules.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author Cai
 */
@Api(description = "订单明细业务")
@RestController
@RequestMapping("/b_api/order_item")
public class OrderItemController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<OrderItemDO>> list(PageModel page, OrderItemQuery query) {
        query.setShopId(loginKeeperHolder.getLoginShopId());
        Page<OrderItemDO> pager = (Page<OrderItemDO>) orderItemService.page(page, QueryUtil.buildWrapper(query));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<OrderDO> select(@PathVariable Long id) {
        OrderItemDO orderItem = orderItemService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, orderItem);
        OrderQuery query = new OrderQuery();
        query.setSn(orderItem.getOrderSn());
        OrderDO order = orderService.getOne(QueryUtil.buildWrapper(query));
        order.getOrderItemList().add(orderItem);
        return Response.restResult(order, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "订单明细发货")
    @RequestMapping(value = "/deliver", method = RequestMethod.POST)
    public R deliverItem(Long orderItemId, String logisticsCom, String logisticsNum) {
        OrderItemDO orderItem = orderItemService.getById(orderItemId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, orderItem);
        Asserts.eq(orderItem.getStatus(), OrderItemDO.StatusEnum.WAIT_DELIVER.name(), "订单明细状态异常");
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(orderItem.getShopId(), keeper.getShopId(), "您不是本店店员");
        OrderItemDO entity = new OrderItemDO();
        entity.setId(orderItemId);
        entity.setLogisticsCom(logisticsCom);
        entity.setLogisticsNum(logisticsNum);
        entity.setStatus(OrderItemDO.StatusEnum.DELIVERED.name());
        orderItemService.updateById(entity);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "修改明细价格")
    @RequestMapping(value = "/update/amount", method = RequestMethod.POST)
    public R updateAmount(Long orderItemId, BigDecimal realPrice) {
        Asserts.notNull("修改金额不能为空", realPrice);
        OrderItemDO orderItem = orderItemService.getById(orderItemId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, orderItem);
        Asserts.eq(orderItem.getStatus(), OrderItemDO.StatusEnum.WAIT_PAY.name(), "只能修改未付款订单金额");
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(orderItem.getShopId(), keeper.getShopId(), "您不是本店店员");
        // 修改后金额差值
        BigDecimal diffAmount = (orderItem.getRealPrice().subtract(realPrice)).multiply(new BigDecimal(orderItem.getQuantity()));
        Asserts.ge(orderItem.getPayAmount().subtract(diffAmount), BigDecimal.ZERO, "明细付款金额必须大于0");
        OrderQuery query = new OrderQuery();
        query.setSn(orderItem.getOrderSn());
        OrderDO order = orderService.getOne(QueryUtil.buildWrapper(query));
        Asserts.ge(order.getPayAmount().subtract(diffAmount), BigDecimal.ZERO, "订单付款金额必须大于0");
        Snowflake snowFlake = new Snowflake(2, 2);
        String newSn = PrefixConstant.ORDER_PREFIX + snowFlake.nextId();
        // 修改订单变动金额
        OrderDO entity = new OrderDO();
        entity.setId(order.getId());
        entity.setSn(newSn);
        entity.setProductAmount(order.getProductAmount().subtract(diffAmount));
        entity.setPayAmount(order.getPayAmount().subtract(diffAmount));
        orderService.updateById(entity);
        // 修改明细变动金额
        OrderItemDO item = new OrderItemDO();
        item.setId(orderItemId);
        item.setOrderSn(newSn);
        item.setRealPrice(realPrice);
        item.setPayAmount(orderItem.getPayAmount().subtract(diffAmount));
        orderItemService.updateById(item);
        OrderItemDO entity2 = new OrderItemDO();
        entity2.setOrderSn(newSn);
        OrderItemQuery query2 = new OrderItemQuery();
        query2.setOrderSn(new String[]{order.getSn()});
        orderItemService.update(entity2, QueryUtil.buildWrapper(query2));
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
