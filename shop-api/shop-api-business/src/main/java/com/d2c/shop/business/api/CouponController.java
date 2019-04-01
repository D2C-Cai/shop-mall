package com.d2c.shop.business.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.member.model.MemberCouponDO;
import com.d2c.shop.service.modules.member.service.MemberCouponService;
import com.d2c.shop.service.modules.product.model.CouponDO;
import com.d2c.shop.service.modules.product.model.CouponProductDO;
import com.d2c.shop.service.modules.product.query.CouponProductQuery;
import com.d2c.shop.service.modules.product.query.CouponQuery;
import com.d2c.shop.service.modules.product.service.CouponProductService;
import com.d2c.shop.service.modules.product.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Cai
 */
@Api(description = "优惠券业务")
@RestController
@RequestMapping("/b_api/coupon")
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponProductService couponProductService;
    @Autowired
    private MemberCouponService memberCouponService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<CouponDO>> list(PageModel page, Integer status, Integer enable) {
        CouponQuery query = new CouponQuery();
        query.setShopId(loginKeeperHolder.getLoginShopId());
        Date now = new Date();
        QueryWrapper<CouponDO> queryWrapper = null;
        if (enable != null && enable == 1) {
            query.setStatus(1);
            query.setReceiveEndDateL(now);
        } else {
            switch (status) {
                case 1:
                    // 可领取
                    query.setStatus(1);
                    query.setReceiveStartDateR(now);
                    query.setReceiveEndDateL(now);
                    break;
                case 0:
                    // 未开始
                    query.setStatus(1);
                    query.setReceiveStartDateL(now);
                    break;
                case -1:
                    // 已失效
                    query.setStatus(null);
                    queryWrapper = QueryUtil.buildWrapper(query);
                    queryWrapper.and(i -> i.eq("status", 0).or().lt("receive_end_date", now));
                    break;
                default:
                    break;
            }
        }
        if (queryWrapper == null) {
            queryWrapper = QueryUtil.buildWrapper(query);
        }
        Page<CouponDO> pager = (Page<CouponDO>) couponService.page(page, queryWrapper);
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<CouponDO> select(@PathVariable Long id) {
        CouponDO coupon = couponService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, coupon);
        return Response.restResult(coupon, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<CouponDO> insert(@RequestBody CouponDO coupon) {
        coupon.setShopId(loginKeeperHolder.getLoginShopId());
        coupon.setConsumption(0);
        couponService.save(coupon);
        return Response.restResult(coupon, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<CouponDO> update(@RequestBody CouponDO coupon) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(coupon.getShopId(), keeper.getShopId(), "您不是本店店员");
        couponService.updateById(coupon);
        return Response.restResult(couponService.getById(coupon.getId()), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "查询已绑商品")
    @RequestMapping(value = "/product/relation", method = RequestMethod.POST)
    public R<List<Long>> relationProduct(Long[] productIds, Long couponId) {
        CouponProductQuery query = new CouponProductQuery();
        query.setCouponId(couponId);
        query.setProductId(productIds);
        List<CouponProductDO> list = couponProductService.list(QueryUtil.buildWrapper(query));
        List<Long> resultList = new ArrayList<>();
        list.forEach(item -> resultList.add(item.getProductId()));
        return Response.restResult(resultList, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "绑定商品")
    @RequestMapping(value = "/product/insert", method = RequestMethod.POST)
    public R<CouponProductDO> insertProduct(@RequestBody List<CouponProductDO> list) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        for (CouponProductDO item : list) {
            CouponDO coupon = couponService.getById(item.getCouponId());
            Asserts.eq(coupon.getShopId(), keeper.getShopId(), "您不是本店店员");
            CouponProductQuery query = new CouponProductQuery();
            query.setCouponId(item.getCouponId());
            query.setProductId(new Long[]{item.getProductId()});
            couponProductService.remove(QueryUtil.buildWrapper(query));
            couponProductService.save(item);
        }
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "解绑商品")
    @RequestMapping(value = "/product/delete", method = RequestMethod.POST)
    public R deleteProduct(Long couponId, Long productId) {
        CouponDO coupon = couponService.getById(couponId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, coupon);
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(coupon.getShopId(), keeper.getShopId(), "您不是本店店员");
        CouponProductQuery query = new CouponProductQuery();
        query.setCouponId(couponId);
        query.setProductId(new Long[]{productId});
        couponProductService.remove(QueryUtil.buildWrapper(query));
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "优惠券核销")
    @RequestMapping(value = "/cancel/{id}", method = RequestMethod.GET)
    public R cancel(@PathVariable Long id) {
        MemberCouponDO memberCoupon = memberCouponService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, memberCoupon);
        Asserts.eq(memberCoupon.available(), true, "该优惠券已失效");
        MemberCouponDO entity = new MemberCouponDO();
        memberCoupon.setId(id);
        memberCoupon.setStatus(0);
        memberCouponService.updateById(memberCoupon);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
