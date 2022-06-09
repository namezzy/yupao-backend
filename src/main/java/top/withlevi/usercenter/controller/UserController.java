package top.withlevi.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.withlevi.usercenter.common.BaseResponse;
import top.withlevi.usercenter.common.ErrorCode;
import top.withlevi.usercenter.common.ResultUtil;
import top.withlevi.usercenter.exception.BusinessException;
import top.withlevi.usercenter.model.domain.Users;
import top.withlevi.usercenter.model.request.UserLoginRequest;
import top.withlevi.usercenter.model.request.UserRegisterRequest;
import top.withlevi.usercenter.service.UsersService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static top.withlevi.usercenter.constant.UserConstant.ADMIN_ROLE;
import static top.withlevi.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author Levi
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UsersService usersService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long result = usersService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtil.success(result);

    }

    @PostMapping("/login")
    public BaseResponse<Users> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Users users = usersService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(users);

    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int status = usersService.userLogout(request);

        return ResultUtil.success(status);

    }

    @GetMapping("/search")
    public BaseResponse<List<Users>> searchUsers(String username, HttpServletRequest servletRequest) {
        // 鉴权-仅管理员可查询
        if (!isAdmin(servletRequest)) {
            return ResultUtil.success(new ArrayList<>());
        }

        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        List<Users> usersList = usersService.list(queryWrapper);
        List<Users> list = usersList.stream().map(user -> usersService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtil.success(list);
    }


    @GetMapping("/current")
    public BaseResponse<Users> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        Users currentUser = (Users) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = currentUser.getId();
        // todo 校验用户是否合法
        Users user = usersService.getById(userId);
        Users safetyUser = usersService.getSafetyUser(user);
        return ResultUtil.success(safetyUser);


    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest servletRequest) {
        if (!isAdmin(servletRequest)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        boolean b = usersService.removeById(id);
        return ResultUtil.success(b);

    }


    /**
     * 是否为管理员
     *
     * @param servletRequest
     * @return Boolean
     */
    private boolean isAdmin(HttpServletRequest servletRequest) {

        // 鉴权-仅管理员可查询
        Object userObj = servletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        Users user = (Users) userObj;
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

}
