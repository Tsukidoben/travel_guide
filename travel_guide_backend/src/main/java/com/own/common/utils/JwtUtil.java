package com.own.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.own.common.config.CustomYmlProperties;
import com.own.common.constant.SysConstant;
import com.own.common.vo.UserContext;
import com.own.model.User;
import com.own.service.UserService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
* token工具类
*/
@Component
@Slf4j
public class JwtUtil {

    /**
     * 存储作废的token
     */
    private static final Set<String> invalidatedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 创建token
     *
     * @param id       用户id不是账号
     * @param userName 用户名
     * @return
     */
    public static String createToken(String id, String userName) {
        CustomYmlProperties customYmlProperties = SpringUtil.getBean(CustomYmlProperties.class);
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        JwtBuilder builder = Jwts.builder()
                .setHeader(header)  //设置头
                .setId(id)          //用户id
                //token过期时间
                .setExpiration(new Date(System.currentTimeMillis() + customYmlProperties.getJwt().getExpireTime()))
                .setSubject(userName)   //用户名
                .setIssuedAt(new Date())    //token创建时间
                .signWith(SignatureAlgorithm.HS256, customYmlProperties.getJwt().getKey()); //加密方式
        return builder.compact();
    }

    /**
     * 验证token是否有效
     *
     * @param token 请求中的token
     * @return 返回1 有效  0 token有效 但用户不存在  2 token过期  3 其他异常
     */
    public int verify(String token) {
        CustomYmlProperties customYmlProperties = SpringUtil.getBean(CustomYmlProperties.class);
        if (invalidatedTokens.contains(token)) {
            return 3; // token 退出登录
        }
        Claims claims = null;
        try {
            // token过期后会抛出ExpiredJwtException 异常，通过这个来判断token过期
            claims = Jwts.parser()
                    .setSigningKey(customYmlProperties.getJwt().getKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return 2;
        } catch (Exception e) {
            return 3;
        }
        // 从token中获取用户id，查询该id的用户是否存在，存在则token验证通过
        String id = claims.getId();
        User user = SpringUtil.getBean(UserService.class).getByIdPlus(id);
        if (ObjectUtil.isNotEmpty(user)) {
            //塞入ThreadLocal
            UserContext userLocal = new UserContext();

            userLocal.setUserId(user.getId())
                    .setUserName(user.getUserName())
                    .setUserAccount(user.getUserAccount())
                    .setUserRole(user.getUserRole());
            ContextUtil.setUserInfo(userLocal);


            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 作废token
     *
     * @param token 要作废的token
     */
    public static void invalidateToken(String token) {
        if(ObjectUtil.isEmpty(token)){
            // 如果token为空则查询当前登陆人token
            token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(SysConstant.TOKEN);
        }
        invalidatedTokens.add(token);
    }

}
