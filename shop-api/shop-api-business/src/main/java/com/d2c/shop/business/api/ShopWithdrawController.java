package com.d2c.shop.business.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopDO;
import com.d2c.shop.service.modules.core.model.ShopWithdrawDO;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.core.query.ShopWithdrawQuery;
import com.d2c.shop.service.modules.core.service.ShopService;
import com.d2c.shop.service.modules.core.service.ShopWithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cai
 */
@Api(description = "店铺提现业务")
@RestController
@RequestMapping("/b_api/shop_withdraw")
public class ShopWithdrawController extends BaseController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopWithdrawService shopWithdrawService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<ShopWithdrawDO>> list(PageModel page, ShopWithdrawQuery query) {
        query.setShopId(loginKeeperHolder.getLoginShopId());
        Page<ShopWithdrawDO> pager = (Page<ShopWithdrawDO>) shopWithdrawService.page(page, QueryUtil.buildWrapper(query));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<ShopWithdrawDO> insert(@RequestBody ShopWithdrawDO shopWithdraw) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        ShopDO shop = shopService.getById(keeper.getShopId());
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, shop);
        Asserts.ge(shop.getBalance(), shopWithdraw.getAmount(), "店铺金额不足，请确认后再提现");
        shopWithdraw.setShopId(keeper.getShopId());
        shopWithdraw.setShopKeeperId(keeper.getId());
        shopWithdraw.setShopKeeperAccount(keeper.getAccount());
        shopWithdraw.setStatus(ShopWithdrawDO.StatusEnum.APPLY.name());
        shopWithdraw.setArrivalAmount(shopWithdraw.getAmount());
        shopWithdrawService.doWithdraw(shopWithdraw);
        return Response.restResult(shopWithdraw, ResultCode.SUCCESS);
    }

}
