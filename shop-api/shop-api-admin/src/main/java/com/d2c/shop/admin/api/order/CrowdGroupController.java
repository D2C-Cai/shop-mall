package com.d2c.shop.admin.api.order;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.order.model.CrowdGroupDO;
import com.d2c.shop.service.modules.order.query.CrowdGroupQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "拼团团组管理")
@RestController
@RequestMapping("/back/crowd_group")
public class CrowdGroupController extends BaseCtrl<CrowdGroupDO, CrowdGroupQuery> {

}
