package top.withlevi.usercenter.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.withlevi.usercenter.model.domain.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Dongxiao.Wang
* @description 针对表【users(用户表)】的数据库操作Mapper
* @createDate 2022-05-11 17:49:38
* @Entity generator.domain.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}




