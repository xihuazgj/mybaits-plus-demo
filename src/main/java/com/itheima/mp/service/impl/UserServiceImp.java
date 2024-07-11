package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements IUserService {
    @Override
    @Transactional
    public void deductBalance(Long id, Integer money) {
        //1.查询用户
        User user = getById(id);
        //2.校验用户状态
        if (user == null || user.getStatus() == UserStatus.FROZEN) {
            throw new RuntimeException("用户状态异常！");
        }
        //3.校验余额是否充足
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足！");
        }
        //4.扣减余额 update tb_user set balance = balance - ?
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance == 0, User::getStatus, 2)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance())  //乐观锁
                .update();
    }


    @Override
    public void increaseBalance(Long id, Integer money) {
        //1.查询用户
        User user = getById(id);
        //2.增加余额 update tb_user set balance = balance + ?
        int remainBalance = user.getBalance() + money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance > 0, User::getStatus, 1)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance())  //乐观锁
                .update();
    }

    @Override
    public UserVO queryUsersAndAddressById(Long id) {
        //1.查询用户
        User user = getById(id);
        if (user == null || user.getStatus() == UserStatus.FROZEN) {
            throw new RuntimeException("用户状态异常");
        }
        //2.查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId, id)
                .list();
        //3.封装VO
        //3.1.转User的PO为VO
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        //3.2.转地址
        if (CollUtil.isNotEmpty(addresses)) {
            userVO.setAddress(BeanUtil.copyToList(addresses, AddressVO.class));
        }

        return userVO;
    }

    @Override
    public List<UserVO> queryUsersAndAddressByIds(List<Long> ids) {
        //1.查询用户
        List<User> users = listByIds(ids);
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        //2.查询地址
        //2.1获取用户id集合
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        //2.2根据用户id查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class)
                .in(Address::getUserId, userIds)
                .list();
        //2.3转换地址VO
        List<AddressVO> addressVOList = BeanUtil.copyToList(addresses, AddressVO.class);
        //2.4梳理地址集合，分组，相同用户的放入一个集合
        Map<Long, List<AddressVO>> addressMap = new HashMap<>(0);
        if (CollUtil.isNotEmpty(addressVOList)) {
            addressMap = addressVOList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }
        //3.转换VO，返回
        List<UserVO> list = new ArrayList<>(users.size());
        for (User user : users) {
            //3.1转换user的po为vo
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            list.add(vo);
            //3.2转换地址vo
            vo.setAddress(addressMap.get(user.getId()));

        }
        return list;
    }

    @Override
    public PageDTO<UserVO> queryUserPage(UserQuery query) {
        //1.查询条件
        String name = query.getName();
        Integer status = query.getStatus();
        //分页条件
        Page<User> page = query.toMpPageDefaultSortByUpdateTime();
        //排序条件
        if (StrUtil.isNotEmpty(query.getSortBy())) {
            page.addOrder(new OrderItem(query.getSortBy(), query.getIsAsc()));
        }
        else {
            page.addOrder(new OrderItem("update_time",false));
        }
        //2.分页查询,业务处理逻辑
        Page<User> userPage = lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .page(page);
        //3.封装VO结果
//        return PageDTO.of(page,UserVO.class);
        return PageDTO.of(page,user -> {
            //1.拷贝基础属性
            UserVO vo = BeanUtil.copyProperties(user,UserVO.class);
            //2.处理特殊逻辑
            vo.setUsername(vo.getUsername().substring(0,vo.getUsername().length()-2) + "**");
            return vo;
        });
    }

    @Override
    public List<User> queryUsers(String name, Integer status,
                                 Integer minBalance, Integer maxBalance
    ) {

        return lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();
    }

}
