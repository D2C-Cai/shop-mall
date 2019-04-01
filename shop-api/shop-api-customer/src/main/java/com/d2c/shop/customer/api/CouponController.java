package com.d2c.shop.customer.api;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.customer.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopDO;
import com.d2c.shop.service.modules.core.service.ShopService;
import com.d2c.shop.service.modules.member.model.MemberCouponDO;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.member.query.MemberCouponQuery;
import com.d2c.shop.service.modules.member.service.MemberCouponService;
import com.d2c.shop.service.modules.product.model.CouponDO;
import com.d2c.shop.service.modules.product.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cai
 */
@Api(description = "优惠券业务")
@RestController
@RequestMapping("/c_api/coupon")
public class CouponController extends BaseController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private MemberCouponService memberCouponService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<MemberCouponDO>> list(PageModel page, Integer status, Long shopId) {
        Asserts.notNull(ResultCode.REQUEST_PARAM_NULL, status);
        MemberCouponQuery query = new MemberCouponQuery();
        query.setShopId(shopId);
        query.setMemberId(loginMemberHolder.getLoginId());
        Date now = new Date();
        QueryWrapper<MemberCouponDO> queryWrapper = null;
        switch (status) {
            case 1:
                // 可使用
                query.setStatus(1);
                query.setServiceEndDateL(now);
                break;
            case 0:
                // 已使用
                query.setStatus(0);
                query.setServiceEndDateL(now);
                break;
            case -1:
                // 已过期
                query.setServiceEndDateR(now);
                break;
            default:
                break;
        }
        if (queryWrapper == null) {
            queryWrapper = QueryUtil.buildWrapper(query);
        }
        Page<MemberCouponDO> pager = (Page<MemberCouponDO>) memberCouponService.page(page, queryWrapper);
        Set<Long> couponIds = new HashSet<>();
        pager.getRecords().forEach(item -> couponIds.add(item.getCouponId()));
        if (couponIds.size() == 0) return Response.restResult(pager, ResultCode.SUCCESS);
        List<CouponDO> couponList = (List<CouponDO>) couponService.listByIds(couponIds);
        Map<Long, CouponDO> couponMap = new ConcurrentHashMap<>();
        couponList.forEach(item -> couponMap.put(item.getId(), item));
        for (MemberCouponDO mc : pager.getRecords()) {
            mc.setName(couponMap.get(mc.getCouponId()).getName());
            mc.setNeedAmount(couponMap.get(mc.getCouponId()).getNeedAmount());
            mc.setAmount(couponMap.get(mc.getCouponId()).getAmount());
            mc.setRemark(couponMap.get(mc.getCouponId()).getRemark());
        }
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "领取")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<MemberCouponDO> insert(Long couponId) {
        MemberDO member = loginMemberHolder.getLoginMember();
        CouponDO coupon = couponService.getById(couponId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, coupon);
        Asserts.eq(coupon.getCrowd(), 0, "优惠券需参与拼团活动获取");
        ShopDO shop = shopService.getById(coupon.getShopId());
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, shop);
        Asserts.eq(coupon.available(), true, "优惠券未在指定领取时间范围");
        Asserts.gt(coupon.getCirculation(), coupon.getConsumption(), "优惠券已被一抢而空");
        MemberCouponQuery query = new MemberCouponQuery();
        query.setMemberId(member.getId());
        query.setCouponId(coupon.getId());
        int count = memberCouponService.count(QueryUtil.buildWrapper(query));
        Asserts.gt(coupon.getRestriction(), count, "优惠券每人限领" + coupon.getRestriction() + "张，不要贪心哦");
        Date serviceStartDate = coupon.getServiceStartDate();
        Date serviceEndDate = coupon.getServiceEndDate();
        if (coupon.getServiceSustain() != null) {
            serviceStartDate = new Date();
            serviceEndDate = DateUtil.offsetHour(serviceStartDate, coupon.getServiceSustain());
        }
        MemberCouponDO memberCoupon = new MemberCouponDO();
        memberCoupon.setMemberId(member.getId());
        memberCoupon.setCouponId(coupon.getId());
        memberCoupon.setShopId(shop.getId());
        memberCoupon.setShopName(shop.getName());
        memberCoupon.setStatus(1);
        memberCoupon.setServiceStartDate(serviceStartDate);
        memberCoupon.setServiceEndDate(serviceEndDate);
        memberCouponService.doReceive(memberCoupon);
        return Response.restResult(memberCoupon, ResultCode.SUCCESS);
    }

}
