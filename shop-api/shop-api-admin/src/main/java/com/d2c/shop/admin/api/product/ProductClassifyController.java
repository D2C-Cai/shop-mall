package com.d2c.shop.admin.api.product;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.product.model.ProductClassifyDO;
import com.d2c.shop.service.modules.product.query.ProductClassifyQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "商品分类管理")
@RestController
@RequestMapping("/back/product_classify")
public class ProductClassifyController extends BaseCtrl<ProductClassifyDO, ProductClassifyQuery> {

}
