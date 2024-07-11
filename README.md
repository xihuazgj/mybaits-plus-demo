### 一、mybaitsplus的使用-7.9

#### 1.mybatis-plus使用的前置条件

##### 1.1在项目pom.xml文件中导入依赖
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.3.1</version>
        </dependency>
##### 1.2.在mapper层中的接口要继承BaseMapper<User>

public interface AddressMapper extends BaseMapper<Address> {
}
！一定记住（因为这个问题idea并不会报错），否则不会生效
#### 2.使用mybaits-plus

##### 2.1 在所需要的业务层中，实例化对应的_Mapper

对于UserMapper来说

```
private UserMapper userMapper;
```

先实例化一个userMapper,userMapper内可以使用很多中方法

```
userMapper.insert(user);
```

```
userMapper.selectById(5L)；
```

```
userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L)); //批量查询
```

```
userMapper.updateById(user);
```

```
userMapper.deleteById(5L);
```

##### 2.2.使用mybaits-plus中的QueryWrapper<User>。

QueryWrapper<User>`泛型化了`QueryWrapper`类，指定了其内部操作的数据类型为`User`。意味着这个`QueryWrapper`实例将会被用来构建针对`User`表（或实体）的查询条件。

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

wrapper.select("column","column",)   //查询的哪些列

wrapper.like("column",“val”,)  // mybaits-plus中的模糊查询方法，里面要传入所要模糊查询的列和like的值

wrapper里有多种方法

// 更新id为1,2,4的用户的balance，扣200

    @Test
    void testUpdateWrapper() {
        List<Long> ids = List.of(1L, 2L, 4L);
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
                .setSql("balance = balance - 200")
                .in("id", ids);
        userMapper.update(null, wrapper);
    }

    
    
        @Test
    void testInsert() {
        User user = new User();
        user.setId(5L);  //这里可以不用写，因为我们mybaits-plus中可以根据自增生成id
        user.setUsername("zgj1");
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo(UserInfo.of(24,"编程老师","男"));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

###### 2.2.1.使用LambdaWrapper


    @Test
    void testLambdaQueryWrapper() {
        LambdaQueryWrapper<User> lambdaWrapper = new LambdaQueryWrapper<User>()
                .select(User::getId, User::getUsername, User::getInfo, User::getBalance)
                .like(User::getUsername, req())
                .ge(User::getBalance, 1000);//1.构建查询条件
        
        List<User> users = userMapper.selectList(lambdaWrapper);
        users.forEach(System.out::println);
    }//2.查询
    String req(){
        return "o";
    } //这个是我觉得前端应该要传的参数
    
LambdaWrapper的特点是用函数式来表达，开发中更符合规范，其它与普通Wrapper一样

#### 2.3.自定义sql+使用mybatis-plus

2.3.1.使用方法
2.3.1.1.在Mapper层中创建自定义方法，处理业务需求
    public interface UserMapper extends BaseMapper<User> {
    void updateBalanceByIds(
            @Param(Constants.WRAPPER
            ) LambdaQueryWrapper<User> wrapper,@Param("amount") int amount);

    @Update("UPDATE tb_user SET balance = balance - #{money} WHERE id = #{id}" )
    void deductBalance(@Param("id") Long id, @Param("money") Integer money);
    }

2.3.1.2.在Mapper.xml文件中，写出自定义sql语句
    <update id="updateBalanceByIds">
        UPDATE tb_user SET balance = balance + #{amount} ${ew.customSqlSegment}
    </update>

2.3.2.获取业务中的更新条件，如果业务需求所要改变的值不是定值，则需使用自定义sql和mybaits-plus配合使用


    List<Long> ids = List.of(1L, 2L, 4L);
    int amount = 200; //1.更新条件，相当于前端伙伴传过来的值，而不是我们后端所写的定值










