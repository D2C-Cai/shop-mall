package com.d2c.shop.admin.api.product;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.product.model.ProductDO;
import com.d2c.shop.service.modules.product.query.ProductQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "商品管理")
@RestController
@RequestMapping("/back/product")
public class ProductController extends BaseCtrl<ProductDO, ProductQuery> {

}
