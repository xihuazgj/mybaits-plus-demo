package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.itheima.mp.domain.po.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


public interface UserMapper extends BaseMapper<User> {
    void updateBalanceByIds(
            @Param(Constants.WRAPPER
            ) LambdaQueryWrapper<User> wrapper,@Param("amount") int amount);

    @Update("UPDATE tb_user SET balance = balance - #{money} WHERE id = #{id}" )
    void deductBalance(@Param("id") Long id, @Param("money") Integer money);
}
