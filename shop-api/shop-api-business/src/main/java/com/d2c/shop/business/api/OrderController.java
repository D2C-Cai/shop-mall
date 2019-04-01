package com.d2c.shop.business.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.order.model.CrowdGroupDO;
import com.d2c.shop.service.modules.order.model.OrderDO;
import com.d2c.shop.service.modules.order.model.OrderItemDO;
import com.d2c.shop.service.modules.order.query.OrderItemQuery;
import com.d2c.shop.service.modules.order.query.OrderQuery;
import com.d2c.shop.service.modules.order.service.CrowdGroupService;
import com.d2c.shop.service.modules.order.service.OrderItemService;
import com.d2c.shop.service.modules.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cai
 */
@Api(description = "订单业务")
@RestController
@RequestMapping("/b_api/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private CrowdGroupService crowdGroupService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<OrderDO>> list(PageModel page, OrderQuery query) {
        query.setShopId(loginKeeperHolder.getLoginShopId());
        Page<OrderDO> pager = (Page<OrderDO>) orderService.page(page, QueryUtil.buildWrapper(query));
        List<String> orderSns = new ArrayList<>();
        Map<String, OrderDO> orderMap = new ConcurrentHashMap<>();
        for (OrderDO order : pager.getRecords()) {
            orderSns.add(order.getSn());
            orderMap.put(order.getSn(), order);
        }
        if (orderSns.size() == 0) return Response.restResult(pager, ResultCode.SUCCESS);
        OrderItemQuery itemQuery = new OrderItemQuery();
        itemQuery.setOrderSn(orderSns.toArray(new String[0]));
        List<OrderItemDO> orderItemList = orderItemService.list(QueryUtil.buildWrapper(itemQuery));
        for (OrderItemDO orderItem : orderItemList) {
            if (orderMap.get(orderItem.getOrderSn()) != null) {
                orderMap.get(orderItem.getOrderSn()).getOrderItemList().add(orderItem);
            }
        }
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<OrderDO> select(@PathVariable Long id) {
        OrderDO order = orderService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, order);
        OrderItemQuery itemQuery = new OrderItemQuery();
        itemQuery.setOrderSn(new String[]{order.getSn()});
        List<OrderItemDO> orderItemList = orderItemService.list(QueryUtil.buildWrapper(itemQuery));
        order.getOrderItemList().addAll(orderItemList);
        if (order.getType().equals(OrderDO.TypeEnum.CROWD.name())) {
            CrowdGroupDO crowdGroup = crowdGroupService.getById(order.getCrowdId());
            order.setCrowdGroup(crowdGroup);
        }
        return Response.restResult(order, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "修改收货地址")
    @RequestMapping(value = "/update/address", method = RequestMethod.POST)
    public R updateAddress(Long orderId, String province, String city, String district, String address, String name, String mobile) {
        OrderDO order = orderService.getById(orderId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, order);
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(order.getShopId(), keeper.getShopId(), "您不是本店店员");
        OrderDO entity = new OrderDO();
        entity.setId(orderId);
        entity.setProvince(province);
        entity.setCity(city);
        entity.setDistrict(district);
        entity.setAddress(address);
        entity.setName(name);
        entity.setMobile(mobile);
        orderService.updateById(entity);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
