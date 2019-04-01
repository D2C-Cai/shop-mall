package com.d2c.shop.business.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.d2c.shop.service.modules.product.model.ProductClassifyDO;
import com.d2c.shop.service.modules.product.model.ProductDO;
import com.d2c.shop.service.modules.product.model.ProductSkuDO;
import com.d2c.shop.service.modules.product.query.ProductQuery;
import com.d2c.shop.service.modules.product.query.ProductSkuQuery;
import com.d2c.shop.service.modules.product.service.ProductCategoryService;
import com.d2c.shop.service.modules.product.service.ProductClassifyService;
import com.d2c.shop.service.modules.product.service.ProductService;
import com.d2c.shop.service.modules.product.service.ProductSkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Cai
 */
@Api(description = "商品业务")
@RestController
@RequestMapping("/b_api/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSkuService productSkuService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductClassifyService productClassifyService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<ProductDO>> list(PageModel page, ProductQuery query) {
        QueryWrapper<ProductDO> queryWrapper = null;
        query.setShopId(loginKeeperHolder.getLoginShopId());
        // 拼团参数
        if (query.getCrowd() != null) {
            Date now = new Date();
            switch (query.getCrowd()) {
                case 1:
                    // 拼团进行中
                    query.setCrowd(1);
                    query.setCrowdStartDateR(now);
                    query.setCrowdEndDateL(now);
                    break;
                case 0:
                    // 拼团未开始
                    query.setCrowd(1);
                    query.setCrowdStartDateL(now);
                    break;
                case -1:
                    // 拼团已结束
                    query.setCrowd(null);
                    queryWrapper = QueryUtil.buildWrapper(query);
                    queryWrapper.and(i -> i.eq("crowd", -1).or().lt("crowd_end_date", now));
                    break;
                case 8:
                    // 正常商品和拼团已过期
                    query.setCrowd(null);
                    queryWrapper = QueryUtil.buildWrapper(query);
                    queryWrapper.and(i -> i.le("crowd", 0).or().lt("crowd_end_date", now));
                default:
                    break;
            }
        }
        if (queryWrapper == null) {
            queryWrapper = QueryUtil.buildWrapper(query);
        }
        Page<ProductDO> pager = (Page<ProductDO>) productService.page(page, queryWrapper);
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<ProductDO> select(@PathVariable Long id) {
        ProductDO product = productService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, product);
        ProductCategoryDO category2 = productCategoryService.getById(product.getCategoryId());
        ProductCategoryDO category1 = productCategoryService.getById(category2.getParentId());
        if (category1 != null) {
            category1.getChildren().add(category2);
            product.setCategory(category1);
        } else {
            product.setCategory(category2);
        }
        ProductClassifyDO classify2 = productClassifyService.getById(product.getClassifyId());
        ProductClassifyDO classify1 = productClassifyService.getById(category2.getParentId());
        if (classify1 != null) {
            classify1.getChildren().add(classify2);
            product.setClassify(classify1);
        } else {
            product.setClassify(classify2);
        }
        ProductSkuQuery query = new ProductSkuQuery();
        query.setProductId(product.getId());
        List<ProductSkuDO> skuList = productSkuService.list(QueryUtil.buildWrapper(query));
        product.setSkuList(skuList);
        return Response.restResult(product, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "新增")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<ProductDO> insert(@RequestBody ProductDO product) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        product.setShopId(keeper.getShopId());
        for (ProductSkuDO sku : product.getSkuList()) {
            sku.setShopId(keeper.getShopId());
        }
        productService.doCreate(product);
        return Response.restResult(product, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<ProductDO> update(@RequestBody ProductDO product) {
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(product.getShopId(), keeper.getShopId(), "您不是本店店员");
        productService.doUpdate(product);
        return Response.restResult(productService.getById(product.getId()), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "通过ID删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public R delete(@PathVariable Long id) {
        ProductDO product = productService.getById(id);
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(product.getShopId(), keeper.getShopId(), "您不是本店店员");
        productService.removeById(id);
        ProductSkuQuery query = new ProductSkuQuery();
        query.setProductId(id);
        productSkuService.remove(QueryUtil.buildWrapper(query));
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "更改状态")
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public R status(Long id, Integer status) {
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, id, status);
        ProductDO product = productService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, product);
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(product.getShopId(), keeper.getShopId(), "您不是本店店员");
        ProductDO entity = new ProductDO();
        entity.setId(id);
        entity.setStatus(status);
        productService.updateById(entity);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "更改拼团")
    @RequestMapping(value = "/crowd", method = RequestMethod.POST)
    public R crowd(Long id, Integer crowd) {
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, id, crowd);
        ProductDO product = productService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, product);
        ShopkeeperDO keeper = loginKeeperHolder.getLoginKeeper();
        Asserts.eq(product.getShopId(), keeper.getShopId(), "您不是本店店员");
        ProductDO entity = new ProductDO();
        entity.setId(id);
        entity.setCrowd(crowd);
        productService.updateById(entity);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
