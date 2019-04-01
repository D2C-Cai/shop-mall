package com.d2c.shop.admin.api.core;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.core.model.ShopWithdrawDO;
import com.d2c.shop.service.modules.core.query.ShopWithdrawQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "店铺提现管理")
@RestController
@RequestMapping("/back/shop_withdraw")
public class ShopWithdrawController extends BaseCtrl<ShopWithdrawDO, ShopWithdrawQuery> {

}
