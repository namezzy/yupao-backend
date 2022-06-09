package top.withlevi.usercenter.service;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.withlevi.usercenter.model.domain.Users;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * 用户服务测试
 *
 * @author Levi
 */


@SpringBootTest
@RunWith(SpringRunner.class)
public class UsersServiceTest {

    @Resource
    private UsersService usersService;


    @Test
    public void testAddUer() {
        Users users = new Users();
        users.setUsername("levi");
        users.setUserAccount("withLevi");
        users.setAvatarUrl("https://cicd.withlevi.top/img/avatar-me.jpg");
        users.setGender(0);
        users.setUserPassword("admin");
        users.setEmail("zengyin.ssr@gmail.com");
        users.setUserStatus(0);
        users.setPhone("18515883717");

        boolean result = usersService.save(users);
        System.out.println(users.getId());
        assertEquals(true, result);


    }

    @Test
    public void userRegister() {
        // 测试非空
        String userAccount = "withLevi";
        String userPassword = "";
        String checkPassword = "12345678";
        String planetCode = "1";
        long result = usersService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 测试账户长度
        userAccount = "zha";
        result = usersService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 测试密码不低于8位
        userAccount = "wang";
        userPassword = "123456";
        result = usersService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 测试账户不能重复
        userPassword = "12345678";
        result = usersService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 测试账户不包含特殊字符
        userAccount = "with Levi";
        userPassword = "12345678";
        result = usersService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 测试密码校验相同
        userAccount = "test";
        userPassword = "1234568";
        result = usersService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);


    }

    @Test
    public void testRegister(){
        String userAccount = "levizhao";
        String userPassword = "admin@123456";
        String checkPassword = "admin@123456";
        String planetCode = "66";

        long result = usersService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        if (result == -1){
            System.out.println("插入失败-账户密码低于4位");
        }else {
            System.out.println("用户插入成功！");

        }
    }

}