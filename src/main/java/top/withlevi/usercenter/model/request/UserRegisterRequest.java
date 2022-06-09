package top.withlevi.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author Levi
 */

@Data
public class UserRegisterRequest implements Serializable {


    private static final long serialVersionUID = 8715533090594231402L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;

}
