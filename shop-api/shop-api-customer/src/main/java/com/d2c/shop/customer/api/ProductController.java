package com.d2c.shop.customer.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.customer.api.base.BaseController;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.ExecutorUtil;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.logger.nosql.mongodb.document.UserOperateLog;
import com.d2c.shop.service.modules.logger.nosql.mongodb.document.UserVisitLog;
import com.d2c.shop.service.modules.logger.nosql.mongodb.service.UserOperateLogService;
import com.d2c.shop.service.modules.logger.nosql.mongodb.service.UserVisitLogService;
import com.d2c.shop.service.modules.logger.nosql.mongodb.support.IUoLog;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.product.model.ProductCategoryDO;
import com.d2c.shop.service.modules.product.model.ProductDO;
import com.d2c.shop.service.modules.product.model.ProductSkuDO;
import com.d2c.shop.service.modules.product.query.ProductCategoryQuery;
import com.d2c.shop.service.modules.product.query.ProductQuery;
import com.d2c.shop.service.modules.product.query.ProductSkuQuery;
import com.d2c.shop.service.modules.product.service.ProductCategoryService;
import com.d2c.shop.service.modules.product.service.ProductService;
import com.d2c.shop.service.modules.product.service.ProductSkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Cai
 */
@Api(description = "商品业务")
@RestController
@RequestMapping("/c_api/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductSkuService productSkuService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private UserOperateLogService userOperateLogService;
    @Autowired
    private UserVisitLogService userVisitLogService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<ProductDO>> list(PageModel page, ProductQuery query) {
        QueryWrapper<ProductDO> queryWrapper = null;
        // 品类参数
        if (query.getCategoryId() != null) {
            ProductCategoryDO pc = productCategoryService.getById(query.getCategoryId());
            if (pc != null && pc.getParentId() == null) {
                // 一级大类特殊处理
                query.setCategoryId(null);
                ProductCategoryQuery pcq = new ProductCategoryQuery();
                pcq.setParentId(pc.getId());
                List<ProductCategoryDO> children = productCategoryService.list(QueryUtil.buildWrapper(pcq));
                List<Long> categoryIds = new ArrayList<>();
                children.forEach(item -> categoryIds.add(item.getId()));
                query.setCategoryIds(categoryIds.toArray(new Long[0]));
            }
        }
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
    public R<ProductDO> select(@PathVariable Long id, Long shopId) {
        ProductDO product = productService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, product);
        ProductSkuQuery query = new ProductSkuQuery();
        query.setProductId(id);
        List<ProductSkuDO> skuList = productSkuService.list(QueryUtil.buildWrapper(query));
        product.getSkuList().addAll(skuList);
        try {
            // 用户操作记录
            MemberDO member = loginMemberHolder.getLoginMember();
            ExecutorUtil.cachedPool.submit(() -> {
                        this.saveLog(member, product, shopId);
                    }
            );
        } catch (ApiException e) {
        }
        return Response.restResult(product, ResultCode.SUCCESS);
    }

    private void saveLog(MemberDO member, IUoLog uolog, Long shopId) {
        UserOperateLog log = new UserOperateLog(UserOperateLog.TypeEnum.BROWSE, UserOperateLog.TargetEnum.PRODUCT, uolog);
        log.setMemberId(member.getId());
        userOperateLogService.doSaveLog(log);
        UserVisitLog log2 = new UserVisitLog(member.getId(), shopId);
        userVisitLogService.doSaveLog(log2);
    }

}
