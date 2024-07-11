package com.itheima.mp.service;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class IUserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    void testSaveUser() {
        User user = new User();
//        user.setId(5L);
        user.setUsername("lilei");
        user.setPassword("1234");
        user.setPhone("18688990012");
        user.setBalance(2000);
        user.setInfo(UserInfo.of(24,"编程老师","男"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userService.save(user);
    }

    private User builder(int i) {
        User user = new User();
        user.setUsername("user_" + i);
        user.setPassword("123");
        user.setPhone("" + (18688198789L + i));
        user.setBalance(2000);
        user.setInfo(UserInfo.of(24,"编程老师","男"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    /*
     * 一个一个插入100000条数据
     * */
    @Test
    void testSaveOneByOne() {
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) {
            userService.save(builder(i));
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时：" + (e - b));
    }

    /*
     * 批处理插入
     * */
    @Test
    void testSaveBatch() {
        //每次批量插入1000条，插入100次即完成10万条

        //准备一个容量为1000的集合
        List<User> list = new ArrayList<>(1000);
        long b = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) {
            list.add(builder(i));
            // 每1000条插入一次
            if (i % 1000 == 0) {
                userService.saveBatch(list);
                //清空集合，准备下一批数据
                list.clear();
            }
        }
        long e = System.currentTimeMillis();
        System.out.println("耗时：" + (e - b));
    }

    @Test
    void testQuery() {
        List<User> users = userService.listByIds(List.of(1L, 2L, 4L));
        users.forEach(System.out::println);
    }

    /*
    * 分页查询
    * */
    @Test
    void  testPageQuery(){
        int pageNo = 1, pageSize = 2;

        //分页条件
        Page<User> userPage = Page.of(pageNo,pageSize);
        //排序条件
        userPage.addOrder(new OrderItem("balance",true));
        userPage.addOrder(new OrderItem("id",true));
        //分页查询
        Page<User> page = userService.page(userPage);
        //解析
        long total = page.getTotal();
        System.out.println("total= " + total);
        long pages = page.getPages();
        System.out.println("pages= " + pages);
        List<User> users = page.getRecords();
        users.forEach(System.out::println);
    }

}