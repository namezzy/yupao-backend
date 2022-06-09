package top.withlevi.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author Levi
 */

@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = 1714596267175002646L;

    private String userAccount;

    private String userPassword;


}
