package com.d2c.shop.admin.api.member;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.core.model.ShopDO;
import com.d2c.shop.service.modules.core.service.ShopService;
import com.d2c.shop.service.modules.member.model.MemberDO;
import com.d2c.shop.service.modules.member.model.MemberShopDO;
import com.d2c.shop.service.modules.member.query.MemberShopQuery;
import com.d2c.shop.service.modules.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiCai
 */
@Api(description = "会员店铺关系")
@RestController
@RequestMapping("/back/member_shop")
public class MemberShopController extends BaseCtrl<MemberShopDO, MemberShopQuery> {

    @Autowired
    private ShopService shopService;
    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "会员关联的门店列表")
    @RequestMapping(value = "/relation/shop", method = RequestMethod.POST)
    public R<Page<ShopDO>> memberR(Long memberId, PageModel page) {
        MemberShopQuery query = new MemberShopQuery();
        query.setMemberId(memberId);
        IPage<MemberShopDO> relationPager = super.service.page(page, QueryUtil.buildWrapper(query));
        List<Long> shopIds = new ArrayList<>();
        relationPager.getRecords().forEach(r -> shopIds.add(r.getShopId()));
        if (shopIds.size() == 0) {
            return Response.restResult(new Page<>(), ResultCode.SUCCESS);
        }
        List<ShopDO> shops = (List<ShopDO>) shopService.listByIds(shopIds);
        Page<ShopDO> shopPager = new Page<>();
        BeanUtil.copyProperties(relationPager, shopPager, "records", "optimizeCountSql");
        shopPager.setRecords(shops);
        return Response.restResult(shopPager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "门店关联的会员列表")
    @RequestMapping(value = "/relation/member", method = RequestMethod.POST)
    public R<Page<MemberDO>> shopR(Long shopId, PageModel page) {
        MemberShopQuery query = new MemberShopQuery();
        query.setShopId(shopId);
        IPage<MemberShopDO> relationPager = super.service.page(page, QueryUtil.buildWrapper(query));
        List<Long> memberIds = new ArrayList<>();
        relationPager.getRecords().forEach(r -> memberIds.add(r.getMemberId()));
        if (memberIds.size() == 0) {
            return Response.restResult(new Page<MemberDO>(), ResultCode.SUCCESS);
        }
        List<MemberDO> members = (List<MemberDO>) memberService.listByIds(memberIds);
        Page<MemberDO> memberPager = new Page<>();
        BeanUtil.copyProperties(relationPager, memberPager, "records", "optimizeCountSql");
        memberPager.setRecords(members);
        return Response.restResult(memberPager, ResultCode.SUCCESS);
    }

}
