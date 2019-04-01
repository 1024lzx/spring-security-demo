package com.lzx.websecuritydemo.objectmapper;

import com.lzx.websecuritydemo.po.UserPO;
import com.lzx.websecuritydemo.util.IVPMapper;
import com.lzx.websecuritydemo.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import static com.lzx.websecuritydemo.util.BCryptUtil.encode;

@Component
public class UserObjectMapper {
    @Mapper
    public interface IMapper extends IVPMapper<UserVO,UserPO>{}

    private final IMapper mapper;

    UserObjectMapper(){
        mapper = Mappers.getMapper(IMapper.class);
    }

    public UserVO po2vo(UserPO userPO){
        UserVO userVO = mapper.po2vo(userPO);
        userVO.setPassword(null);
        return userVO;
    }

    public UserPO vo2po(UserVO userVO){
        UserPO userPO = mapper.vo2po(userVO);
        userPO.setPassword(encode(userPO.getPassword()));
        return userPO;
    }
}
