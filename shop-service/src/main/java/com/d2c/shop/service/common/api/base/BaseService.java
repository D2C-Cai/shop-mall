package com.d2c.shop.service.common.api.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.d2c.shop.service.common.api.emuns.OperateEnum;
import com.d2c.shop.service.common.utils.AssertUtil;
import com.d2c.shop.service.common.utils.PreventUtil;

import java.util.Collection;

/**
 * @author Cai
 */
public abstract class BaseService<M extends BaseMapper<T>, T extends BaseDO> extends ServiceImpl<M, T> {

    @Override
    public boolean save(T entity) {
        AssertUtil.checkout(entity, OperateEnum.INSERT);
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        for (T entity : entityList) {
            AssertUtil.checkout(entity, OperateEnum.INSERT);
        }
        return super.saveBatch(entityList, batchSize);
    }

    @Override
    public boolean updateById(T entity) {
        PreventUtil.filtrate(entity, OperateEnum.UPDATE);
        return super.updateById(entity);
    }

    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        PreventUtil.filtrate(entity, OperateEnum.UPDATE);
        return super.update(entity, updateWrapper);
    }

}
