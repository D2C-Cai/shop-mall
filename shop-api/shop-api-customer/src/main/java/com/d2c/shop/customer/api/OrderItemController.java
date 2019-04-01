package com.d2c.shop.customer.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.customer.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.member.model.MemberDO;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cai
 */
@Api(description = "订单明细业务")
@RestController
@RequestMapping("/c_api/order_item")
public class OrderItemController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private CrowdGroupService crowdGroupService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<OrderItemDO>> list(PageModel page, OrderItemQuery query) {
        query.setMemberId(loginMemberHolder.getLoginId());
        Page<OrderItemDO> pager = (Page<OrderItemDO>) orderItemService.page(page, QueryUtil.buildWrapper(query));
        if (query.getType() != null && query.getType().equals(OrderDO.TypeEnum.CROWD.name())) {
            Set<Long> crowdIds = new HashSet<>();
            pager.getRecords().forEach(item -> crowdIds.add(item.getCrowdId()));
            if (crowdIds.size() == 0) return Response.restResult(pager, ResultCode.SUCCESS);
            List<CrowdGroupDO> groupList = (List<CrowdGroupDO>) crowdGroupService.listByIds(crowdIds);
            Map<Long, CrowdGroupDO> groupMap = new ConcurrentHashMap<>();
            groupList.forEach(item -> groupMap.put(item.getId(), item));
            for (OrderItemDO oi : pager.getRecords()) {
                oi.setCrowdGroup(groupMap.get(oi.getCrowdId()));
            }
        }
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

    @ApiOperation(value = "订单明细签收")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public R receiveItem(Long orderItemId) {
        OrderItemDO orderItem = orderItemService.getById(orderItemId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, orderItem);
        Asserts.eq(orderItem.getStatus(), OrderItemDO.StatusEnum.DELIVERED.name(), "订单明细状态异常");
        MemberDO member = loginMemberHolder.getLoginMember();
        Asserts.eq(orderItem.getMemberId(), member.getId(), "订单不属于本人");
        OrderItemDO entity = new OrderItemDO();
        entity.setId(orderItemId);
        entity.setStatus(OrderItemDO.StatusEnum.RECEIVED.name());
        orderItemService.updateById(entity);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
