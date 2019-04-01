package com.d2c.shop.business.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopFlowDO;
import com.d2c.shop.service.modules.core.query.ShopFlowQuery;
import com.d2c.shop.service.modules.core.service.ShopFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cai
 */
@Api(description = "店铺资金业务")
@RestController
@RequestMapping("/b_api/shop_flow")
public class ShopFlowController extends BaseController {

    @Autowired
    private ShopFlowService shopFlowService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<ShopFlowDO>> list(PageModel page, ShopFlowQuery query) {
        query.setShopId(loginKeeperHolder.getLoginShopId());
        Page<ShopFlowDO> pager = (Page<ShopFlowDO>) shopFlowService.page(page, QueryUtil.buildWrapper(query));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

}
