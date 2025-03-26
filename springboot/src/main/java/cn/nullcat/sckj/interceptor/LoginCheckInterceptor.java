package cn.nullcat.sckj.interceptor;
import cn.nullcat.sckj.pojo.Result;
import cn.nullcat.sckj.utils.JwtUtils;
import cn.nullcat.sckj.utils.TokenUtils;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor{
    @Autowired
    private TokenUtils tokenUtils;
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) throws Exception {

        String url = req.getRequestURI().toString();
        if (url.contains("login")) {
            return true;
        }
        String jwt = req.getHeader("token");
        if (!StringUtils.hasLength(jwt)) {
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            rep.getWriter().write(notLogin);
            return false;
        }
        try {
            // 解析 token
            Claims claims = JwtUtils.parseJWT(jwt);
            //
            Integer userId = (Integer) claims.get("userId");
            if (!tokenUtils.validateToken(jwt, userId)) {
                Result error = Result.error("NOT_LOGIN");
                String notLogin = JSONObject.toJSONString(error);
                rep.getWriter().write(notLogin);
                return false;
            }
            //
            // 将用户ID存入 request 属性中
            req.setAttribute("userId", claims.get("userId"));
        } catch (Exception e) {
            e.printStackTrace();
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            rep.getWriter().write(notLogin);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
