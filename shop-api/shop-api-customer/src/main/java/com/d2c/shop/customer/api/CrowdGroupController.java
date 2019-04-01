package com.d2c.shop.customer.api;

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
import com.d2c.shop.service.modules.logger.nosql.mongodb.document.UserVisitLog;
import com.d2c.shop.service.modules.logger.nosql.mongodb.service.UserVisitLogService;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.order.model.CrowdGroupDO;
import com.d2c.shop.service.modules.order.query.CrowdGroupQuery;
import com.d2c.shop.service.modules.order.service.CrowdGroupService;
import com.d2c.shop.service.modules.product.model.ProductDO;
import com.d2c.shop.service.modules.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Cai
 */
@Api(description = "拼团业务")
@RestController
@RequestMapping("/c_api/crowd_group")
public class CrowdGroupController extends BaseController {

    @Autowired
    private CrowdGroupService crowdGroupService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserVisitLogService userVisitLogService;

    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Page<CrowdGroupDO>> list(PageModel page, CrowdGroupQuery query) {
        query.setStatus(0);
        query.setDeadlineL(new Date());
        if (query.getProductId() != null) {
            page.setAsc("attend_num", "create_date");
        }
        Page<CrowdGroupDO> pager = (Page<CrowdGroupDO>) crowdGroupService.page(page, QueryUtil.buildWrapper(query));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "根据ID查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public R<CrowdGroupDO> select(@PathVariable Long id, Long shopId) {
        CrowdGroupDO crowdGroup = crowdGroupService.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, crowdGroup);
        ProductDO product = productService.getById(crowdGroup.getProductId());
        crowdGroup.setProduct(product);
        try {
            // 用户操作记录
            MemberDO member = loginMemberHolder.getLoginMember();
            ExecutorUtil.cachedPool.submit(() -> {
                        UserVisitLog log = new UserVisitLog(member.getId(), id);
                        userVisitLogService.doSaveLog(log);
                    }
            );
        } catch (ApiException e) {
        }
        return Response.restResult(crowdGroup, ResultCode.SUCCESS);
    }

}
