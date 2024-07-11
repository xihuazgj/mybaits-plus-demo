package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert() {
        User user = new User();
//        user.setId(5L);
        user.setUsername("zgj1");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo(UserInfo.of(24,"编程老师","男"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Test
    void testSelectById() {
        User user = userMapper.selectById(5L);
        System.out.println("user = " + user);
    }


    @Test
    void testQueryByIds() {
        List<User> users = userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L));
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = new User();
        user.setId(5L);
        user.setBalance(20000);
        userMapper.updateById(user);
    }

    @Test
    void testDeleteUser() {
        userMapper.deleteById(5L);
    }

    /*
     * 查询username中带有“o”的，balance>=1000的 “"id","username","info","balance"”
     * */
    @Test
    void testQueryWrapper() {
        //1.构建查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .select("id", "username", "info", "balance")
                .like("username", "o")
                .ge("balance", 1000);
        //2.查询
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);
    }
    @Test
    void reviewQueryWrapper(){
        //1.构建查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .like("username","z")
                .ge("balance",1000);
        //2.查询
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);
    }

    /*
     * 将username = Jack的balance设置为3000
     * */
    @Test
    void testUpdateByQueryWrapper() {
        //1.要更新的数据
        User user = new User();
        user.setBalance(3000);
        //2.要更新的条件
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("username", "Jack");
        //3.执行更新
        userMapper.update(user, wrapper);
    }

    /*
     * 更新id为1,2,4的用户的balance，扣200
     * */
    @Test
    void testUpdateWrapper() {
        List<Long> ids = List.of(1L, 2L, 4L);
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
                .setSql("balance = balance - 200")
                .in("id", ids);
        userMapper.update(null, wrapper);
    }
    /*
    * 自定义sql+使用mybatis-plus
    * */
    @Test
    void testCustomSqlUpdate() {
        //1.更新条件
        List<Long> ids = List.of(1L, 2L, 4L);
        int amount = 200;
        //2.定义条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .in(User::getId,ids);
        //3.调用自定义方法
        userMapper.updateBalanceByIds(wrapper,amount);
    }

    /*
     * LambdaWrapper
     * */
    @Test
    void testLambdaQueryWrapper() {
        //1.构建查询条件
        LambdaQueryWrapper<User> lambdaWrapper = new LambdaQueryWrapper<User>()
                .select(User::getId, User::getUsername, User::getInfo, User::getBalance)
                .like(User::getUsername, res())
                .ge(User::getBalance, 1000);
        //2.查询
        List<User> users = userMapper.selectList(lambdaWrapper);
        users.forEach(System.out::println);
    }
    String res(){
        return "o";
    }
}