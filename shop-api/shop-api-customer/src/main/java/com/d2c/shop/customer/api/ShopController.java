package com.d2c.shop.customer.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.customer.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.ExecutorUtil;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopDO;
import com.d2c.shop.service.modules.core.model.support.IShop;
import com.d2c.shop.service.modules.core.service.ShopService;
import com.d2c.shop.service.modules.logger.nosql.mongodb.document.UserVisitLog;
import com.d2c.shop.service.modules.logger.nosql.mongodb.service.UserVisitLogService;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.product.model.CouponDO;
import com.d2c.shop.service.modules.product.query.CouponQuery;
import com.d2c.shop.service.modules.product.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Cai
 */
@Api(description = "店铺业务")
@RestController
@RequestMapping("/c_api/shop")
public class ShopController extends BaseController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private UserVisitLogService userVisitLogService;

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<IShop> select(@PathVariable Long id) {
        ShopDO shop = shopService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, shop);
        IShop result = shop;
        try {
            // 用户操作记录
            MemberDO member = loginMemberHolder.getLoginMember();
            ExecutorUtil.cachedPool.submit(() -> {
                        UserVisitLog log = new UserVisitLog(member.getId(), id);
                        userVisitLogService.doSaveLog(log);
                    }
            );
        } catch (ApiException e) {
        }
        return Response.restResult(result, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "查询可领优惠券")
    @RequestMapping(value = "/coupon/list", method = RequestMethod.GET)
    public R<Page<CouponDO>> coupons(PageModel page, Long id) {
        CouponQuery query = new CouponQuery();
        query.setShopId(id);
        Date now = new Date();
        query.setStatus(1);
        query.setCrowd(0);
        query.setReceiveStartDateR(now);
        query.setReceiveEndDateL(now);
        page.setP(1);
        page.setPs(50);
        Page<CouponDO> pager = (Page<CouponDO>) couponService.page(page, QueryUtil.buildWrapper(query));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

}
