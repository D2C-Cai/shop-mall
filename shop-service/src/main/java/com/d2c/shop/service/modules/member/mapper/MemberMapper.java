package com.d2c.shop.service.modules.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.shop.service.modules.member.model.MemberDO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author BaiCai
 */
public interface MemberMapper extends BaseMapper<MemberDO> {

    int doConsume(@Param("id") Long id, @Param("amount") BigDecimal amount);

}
