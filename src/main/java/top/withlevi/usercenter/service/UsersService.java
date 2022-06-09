package top.withlevi.usercenter.service;

import org.apache.catalina.User;
import top.withlevi.usercenter.model.domain.Users;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Dongxiao.Wang
* @description 针对表【users(用户表)】的数据库操作Service
* @createDate 2022-05-11 17:49:38
*/
public interface UsersService extends IService<Users> {



    /**
     *
     * @param userAccount   登录账户
     * @param userPassword  登录密码
     * @param checkPassword 验证密码
     * @param checkPassword 星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);


    /**
     * @param userAccount  登录账户
     * @param userPassword 登录密码
     * @param request
     * @return 脱敏后的用户信息
     */
    Users userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     *
     *用户脱敏
     * @param originUser
     * @return Users
     */
    Users getSafetyUser(Users originUser);


    /**
     * 用户注销
     * @param request
     * @return int
     */
    int userLogout(HttpServletRequest request);
}
