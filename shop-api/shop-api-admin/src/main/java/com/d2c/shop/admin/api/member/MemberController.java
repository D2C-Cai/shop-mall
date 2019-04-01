package com.d2c.shop.admin.api.member;

import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.member.query.MemberQuery;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "会员管理")
@RestController
@RequestMapping("/back/member")
public class MemberController extends BaseCtrl<MemberDO, MemberQuery> {

}
