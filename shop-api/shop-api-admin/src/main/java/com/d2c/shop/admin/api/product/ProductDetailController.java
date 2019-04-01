package com.d2c.shop.admin.api.product;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.product.model.ProductDetailDO;
import com.d2c.shop.service.modules.product.query.ProductDetailQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "商品详情管理")
@RestController
@RequestMapping("/back/product_detail")
public class ProductDetailController extends BaseCtrl<ProductDetailDO, ProductDetailQuery> {

}
