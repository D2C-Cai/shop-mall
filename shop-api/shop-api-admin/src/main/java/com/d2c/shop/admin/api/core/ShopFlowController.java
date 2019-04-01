package com.d2c.shop.admin.api.core;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.core.model.ShopFlowDO;
import com.d2c.shop.service.modules.core.query.ShopFlowQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "店铺流水管理")
@RestController
@RequestMapping("/back/shop_flow")
public class ShopFlowController extends BaseCtrl<ShopFlowDO, ShopFlowQuery> {

}
