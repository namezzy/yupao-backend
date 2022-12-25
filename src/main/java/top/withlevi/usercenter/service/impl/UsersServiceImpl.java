package top.withlevi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import top.withlevi.usercenter.common.ErrorCode;
import top.withlevi.usercenter.exception.BusinessException;
import top.withlevi.usercenter.mapper.UsersMapper;
import top.withlevi.usercenter.model.domain.Users;
import top.withlevi.usercenter.service.UsersService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static top.withlevi.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author Levi
 */

@Service
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
        implements UsersService {

    @Resource
    private UsersMapper usersMapper;

    /**
     * 盐值-密码混淆
     */
    private final static String sAlt = "Levi";


    /**
     * 用户注册
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //1.校验 账户、密码、校验密码不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            // todo  封装异常类
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验 账户、密码、校验密码不能为空");
        }
        //2. 账户长度不能低于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度不能低于4位");
        }
        //3. 密码和校验密码不能低于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不能低于8位");
        }

        // 4. 星球编号目前校验长度不能大于5
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }

        //账户不能包含特殊字符
        String validPattern = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }

        // 判断密码和校验密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不相同");
        }

        // 账户不能重复
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = usersMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复");
        }

        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = usersMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号重复");
        }

        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((sAlt + userPassword).getBytes());

        // 插入数据
        Users user = new Users();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户注册不成功");
        }


        return user.getId();
    }

    @Override
    public Users userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //1.校验账户、密码不能为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验账户、密码不能为空");
        }
        //2. 校验账户长度不能低于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验账户长度不能低于4位");
        }
        //3. 校验密码和校验密码不能低于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验密码和校验密码不能低于8位");
        }

        // 校验账户不能包含特殊字符
        String validPattern = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验账户不能包含特殊字符");
        }

        // 设置加密
        String encryptPassword = DigestUtils.md5DigestAsHex((sAlt + userPassword).getBytes());

        // 查询用户是否存在
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        Users user = usersMapper.selectOne(queryWrapper);

        // 用户不存在
        if (user == null) {
            log.info("user login failed. userAccount cant match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 用户脱敏
        Users safetyUser = getSafetyUser(user);

        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        // session 失效时间
        request.getSession().setMaxInactiveInterval(86400);


        return safetyUser;

    }


    /**
     * 用户脱敏
     *
     * @param originUser
     * @return users
     */

    @Override
    public Users getSafetyUser(Users originUser) {
        if (originUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users safetyUser = new Users();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public List<Users> searchUsersByTagsByMethod01(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long startTime = System.currentTimeMillis();

        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();

        // 拼接 and 查询
        // like '%Java%' and like '%Python%'
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }

        List<Users> usersList = usersMapper.selectList(queryWrapper);
        // lambda 表达式 this::getSafetyUser
        log.info("sql query time =  " + (System.currentTimeMillis() - startTime));

        return usersList.stream().map(this::getSafetyUser).collect(Collectors.toList());

    }

    @Override
    public List<Users> searchUsersByTagsByMethod02(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long startTime = System.currentTimeMillis();

        // 1. 先查询所有用户
        QueryWrapper<Users> queryWrapper = new QueryWrapper();
        List<Users> userList = usersMapper.selectList(queryWrapper);
        Gson gson = new Gson();

        // 2. 在内存中判断是否包含要求的标签
//        for (Users user : userList) {
//            String tagsStr = user.getTags();
//            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
//            }.getType());
//            for (String tagName : tagNameList) {
//                if (!tempTagNameSet.contains(tagName)) {
//                    return false;
//                }
//            }

        // 简便写法
        userList.stream().filter(users -> {

            String tagsStr = users.getTags();
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }

            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
        log.info("Memory query time = " + (System.currentTimeMillis() - startTime));
        return userList;

    }
}




