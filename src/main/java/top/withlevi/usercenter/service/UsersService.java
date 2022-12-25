package top.withlevi.usercenter.service;

import top.withlevi.usercenter.model.domain.Users;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
//    List<Users> searchUsersByTags(List<String> tagNameList);

    /**
     * 根据标签搜索用户-常规SQL查询
     *
     * @param tagNameList
     * @return
     */
    List<Users> searchUsersByTagsByMethod01(List<String> tagNameList);

    /**
     * 根据标签搜索用户-在内存中进行查询
     * @param tagNameList
     * @return
     */
    List<Users> searchUsersByTagsByMethod02(List<String> tagNameList);
}
