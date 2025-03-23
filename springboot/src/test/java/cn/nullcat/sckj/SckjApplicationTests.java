package cn.nullcat.sckj;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SckjApplicationTests {

    @Test
    void contextLoads() {
    }

    /**
     * 生成jwt令牌
     * @param
     */
    @Test
    public void testJwt() {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", 1);
        claims.put("username","nullcat");
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "123456")//签名算法
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .compact();
        System.out.println(jwt);
    }

    /**
     * 解析jwt令牌
     */
    @Test
    public void testJwt2(){
        Claims claims = Jwts.parser()
                .setSigningKey("123456")
                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NDI3ODg3NTgsInVzZXJJZCI6MSwidXNlcm5hbWUiOiJudWxsY2F0In0.QdzpZg1qZxotflsbAgv_GF99w9O4Hm9iBTjHze5_zjs")
                .getBody();
        System.out.println(claims);
    }
}
