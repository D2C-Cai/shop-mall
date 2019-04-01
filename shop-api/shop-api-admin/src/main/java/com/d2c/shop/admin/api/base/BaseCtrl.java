package com.d2c.shop.admin.api.base;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.admin.config.security.context.LoginUserHolder;
import com.d2c.shop.service.common.api.Asserts;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.base.BaseQuery;
import com.d2c.shop.service.common.utils.QueryUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author BaiCai
 */
public abstract class BaseCtrl<E extends BaseDO, Q extends BaseQuery> {

    @Autowired
    public IService<E> service;
    @Autowired
    public LoginUserHolder loginUserHolder;
    @Autowired
    public HttpServletRequest request;

    @ApiOperation(value = "新增数据")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public R<E> insert(@RequestBody E entity) {
        Asserts.notNull(ResultCode.REQUEST_PARAM_NULL, entity);
        service.save(entity);
        return Response.restResult(entity, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "通过ID获取数据")
    @RequestMapping(value = "/select/{id}", method = RequestMethod.GET)
    public R<E> select(@PathVariable Long id) {
        E entity = service.getById(id);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, entity);
        return Response.restResult(entity, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "通过ID更新数据")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public R<E> update(@RequestBody E entity) {
        Asserts.notNull(ResultCode.REQUEST_PARAM_NULL, entity);
        service.updateById(entity);
        return Response.restResult(service.getById(entity.getId()), ResultCode.SUCCESS);
    }

    @ApiOperation(value = "通过ID删除数据")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public R delete(Long[] ids) {
        service.removeByIds(CollUtil.toList(ids));
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "分页查询数据")
    @RequestMapping(value = "/select/page", method = RequestMethod.POST)
    public R<Page<E>> selectPage(PageModel page, Q query) {
        Page<E> pager = (Page<E>) service.page(page, QueryUtil.buildWrapper(query, false));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

}
