package com.d2c.shop.business.api;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopDO;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.core.query.ShopQuery;
import com.d2c.shop.service.modules.core.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Cai
 */
@Api(description = "店铺业务")
@RestController
@RequestMapping("/b_api/shop")
public class ShopController extends BaseController {

    @Autowired
    private ShopService shopService;

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<ShopDO> select(@PathVariable Long id) {
        ShopDO shop = shopService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, shop);
        return Response.restResult(shop, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "开店")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<ShopDO> insert(@RequestBody ShopDO shop) {
        shop.setStatus(1);
        shop.setAuthenticate(0);
        shop.setBalance(BigDecimal.ZERO);
        shop.setValidDate(DateUtil.offsetDay(new Date(), 365));
        shopService.doCreate(shop, loginKeeperHolder.getLoginAccount());
        return Response.restResult(shop, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<ShopDO> update(@RequestBody ShopDO shop) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(shop.getId(), keeper.getShopId(), "您不是本店店员");
        if (shop.getEnterprise() != null) {
            ShopQuery query = new ShopQuery();
            query.setEnterprise(shop.getEnterprise());
            Asserts.gt(1, shopService.count(QueryUtil.buildWrapper(query)), "店铺名称已存在");
        }
        shop.setBalance(null);
        shopService.updateById(shop);
        return Response.restResult(shopService.getById(shop.getId()), ResultCode.SUCCESS);
    }

}
