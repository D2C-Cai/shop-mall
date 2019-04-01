package com.d2c.shop.admin.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.d2c.shop.admin.config.security.context.LoginUserHolder;
import com.d2c.shop.service.common.api.constant.FieldConstant;
import com.d2c.shop.service.common.utils.SpringUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author BaiCai
 */
@Component
public class ModelMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createDate = this.getFieldValByName(FieldConstant.CREATE_DATE, metaObject);
        if (null == createDate) {
            this.setFieldValByName(FieldConstant.CREATE_DATE, new Date(), metaObject);
        }
        Object createMan = this.getFieldValByName(FieldConstant.CREATE_MAN, metaObject);
        if (null == createMan) {
            this.setFieldValByName(FieldConstant.CREATE_MAN, getLoginName(), metaObject);
        }
        Object deleted = this.getFieldValByName(FieldConstant.DELETED, metaObject);
        if (null == deleted) {
            this.setFieldValByName(FieldConstant.DELETED, new Integer(0), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object modifyDate = this.getFieldValByName(FieldConstant.MODIFY_DATE, metaObject);
        this.setFieldValByName(FieldConstant.MODIFY_DATE, new Date(), metaObject);
        Object modifyMan = this.getFieldValByName(FieldConstant.MODIFY_MAN, metaObject);
        if (null == modifyMan) {
            this.setFieldValByName(FieldConstant.MODIFY_MAN, getLoginName(), metaObject);
        }
    }

    private String getLoginName() {
        return SpringUtil.getBean(LoginUserHolder.class).getUsername();
    }

}
