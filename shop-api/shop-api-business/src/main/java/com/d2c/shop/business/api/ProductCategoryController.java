package com.d2c.shop.business.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopkeeperDO;
import com.d2c.shop.service.modules.product.model.ProductCategoryDO;
import com.d2c.shop.service.modules.product.query.ProductCategoryQuery;
import com.d2c.shop.service.modules.product.query.ProductQuery;
import com.d2c.shop.service.modules.product.service.ProductCategoryService;
import com.d2c.shop.service.modules.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Cai
 */
@Api(description = "商品品类业务")
@RestController
@RequestMapping("/b_api/product_category")
public class ProductCategoryController extends BaseController {

    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductService productService;

    @ApiOperation(value = "品类查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Collection<ProductCategoryDO>> list(PageModel page) {
        ProductCategoryQuery query = new ProductCategoryQuery();
        query.setShopId(loginKeeperHolder.getLoginShopId());
        page.setPs(PageModel.MAX_SIZE);
        page.setDesc("sort", "create_date");
        Page<ProductCategoryDO> pager = (Page<ProductCategoryDO>) productCategoryService.page(page, QueryUtil.buildWrapper(query));
        Map<Long, ProductCategoryDO> first = new LinkedHashMap<>();
        List<ProductCategoryDO> second = new ArrayList<>();
        // 一级分类
        for (ProductCategoryDO pc : pager.getRecords()) {
            if (pc.getParentId() == null) {
                first.put(pc.getId(), pc);
            } else if (pc.getParentId() > 0) {
                second.add(pc);
            }
        }
        // 二级分类
        for (ProductCategoryDO pc : second) {
            if (first.get(pc.getParentId()) != null) {
                first.get(pc.getParentId()).getChildren().add(pc);
            }
        }
        return Response.restResult(first.values(), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<ProductCategoryDO> select(@PathVariable Long id) {
        ProductCategoryDO productCategory = productCategoryService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, productCategory);
        return Response.restResult(productCategory, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<ProductCategoryDO> insert(@RequestBody ProductCategoryDO productCategory) {
        productCategory.setShopId(loginKeeperHolder.getLoginShopId());
        productCategoryService.save(productCategory);
        return Response.restResult(productCategory, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<ProductCategoryDO> update(@RequestBody ProductCategoryDO productCategory) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(productCategory.getShopId(), keeper.getShopId(), "您不是本店店员");
        productCategoryService.updateById(productCategory);
        return Response.restResult(productCategoryService.getById(productCategory.getId()), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "通过ID删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public R delete(@PathVariable Long id) {
        ProductCategoryDO productCategory = productCategoryService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, productCategory);
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(productCategory.getShopId(), keeper.getShopId(), "您不是本店店员");
        ProductQuery query = new ProductQuery();
        query.setCategoryId(id);
        Asserts.gt(1, productService.count(QueryUtil.buildWrapper(query)), "该品类下存在商品，无法删除");
        productCategoryService.removeById(id);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
