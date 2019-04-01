package com.d2c.shop.business.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.product.model.ProductDetailDO;
import com.d2c.shop.service.modules.product.query.ProductDetailQuery;
import com.d2c.shop.service.modules.product.service.ProductDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Cai
 */
@Api(description = "商品详情业务")
@RestController
@RequestMapping("/b_api/product_detail")
public class ProductDetailController extends BaseController {

    @Autowired
    private ProductDetailService productDetailService;

    @ApiOperation(value = "根据商品ID查询")
    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    public R<ProductDetailDO> select(@PathVariable Long productId) {
        ProductDetailQuery query = new ProductDetailQuery();
        query.setProductId(productId);
        ProductDetailDO productDetail = productDetailService.getOne(QueryUtil.buildWrapper(query));
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, productDetail);
        return Response.restResult(productDetail, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<ProductDetailDO> insertDetail(@RequestBody ProductDetailDO productDetail) {
        productDetail.setShopId(loginKeeperHolder.getLoginShopId());
        productDetailService.save(productDetail);
        return Response.restResult(productDetail, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<ProductDetailDO> update(@RequestBody ProductDetailDO productDetail) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(productDetail.getShopId(), keeper.getShopId(), "您不是本店店员");
        productDetailService.updateById(productDetail);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "通过商品ID删除")
    @RequestMapping(value = "/delete/{productId}", method = RequestMethod.POST)
    public R delete(@PathVariable Long productId) {
        ProductDetailQuery query = new ProductDetailQuery();
        query.setProductId(productId);
        ProductDetailDO productDetail = productDetailService.getOne(QueryUtil.buildWrapper(query));
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, productDetail);
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(productDetail.getShopId(), keeper.getShopId(), "您不是本店店员");
        productDetailService.removeById(productDetail.getId());
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
