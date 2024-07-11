package com.itheima.mp.controller;


import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @ApiOperation("新增用户接口")
    @PostMapping
    public void saveUser(@RequestBody UserFormDTO userDTO) {
        // 1.将DTO拷贝到PO
        User user = BeanUtil.copyProperties(userDTO, User.class);
        // 2.新增
        userService.save(user);
    }

    @ApiOperation("删除用户接口")
    @DeleteMapping("{id}")
    public void removeUser(@ApiParam("用户id") @PathVariable("id") Long id) {
        // 删除用户
        userService.removeById(id);
    }

    @ApiOperation("根据id查询用户接口")
    @GetMapping("{id}")
    public UserVO queryUser(@ApiParam("用户id") @PathVariable("id") Long id) {

//        User user = userService.getById(id);
//        return BeanUtil.copyProperties(user, UserVO.class);
        return userService.queryUsersAndAddressById(id);
    }

    @ApiOperation("根据id集合查询用户接口")
    @GetMapping
    public List<UserVO> queryUser(
            @ApiParam("用户id集合") @RequestParam("ids")
            List<Long> ids) {
        // 查询用户集合
//        List<User> users = userService.listByIds(ids);
//        return BeanUtil.copyToList(users, UserVO.class);
        return userService.queryUsersAndAddressByIds(ids);
    }

    @ApiOperation("根据id扣减用户余额接口")
    @PutMapping("/{id}/deduction/{money}")
    public void deductBalanceById(
            @ApiParam("用户id") @PathVariable("id") Long id,
            @ApiParam("扣减的金额") @PathVariable("money") Integer money) {
        //
        userService.deductBalance(id,money);
    }

    @ApiOperation("根据id增加用户余额接口")
    @PutMapping("/{id}/increase/{money}")
    public void increaseBalanceById(
            @ApiParam("用户id") @PathVariable("id") Long id,
            @ApiParam("增加的金额") @PathVariable("money") Integer money) {
        //
        userService.increaseBalance(id,money);
    }

    @ApiOperation("根据复杂条件查询用户接口")
    @GetMapping("list")
    public List<UserVO> queryUser(UserQuery query) {
        List<User> users = userService.queryUsers(
                query.getName(),query.getStatus()
                ,query.getMinBalance(),query.getMaxBalance());

        return BeanUtil.copyToList(users,UserVO.class);
    }
    @ApiOperation("根据条件分页查询用户接口")
    @GetMapping("page")
    public PageDTO<UserVO> queryUserPage(UserQuery query) {
        return userService.queryUserPage(query);
    }
}
