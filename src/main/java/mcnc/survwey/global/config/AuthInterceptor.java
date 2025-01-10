package mcnc.survwey.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String LOGIN_USER = "loginUser";

    /**
     * 요청을 인터셉트하여 요청 수행 전에 세션 체크하는 메서드
     * - 세션이 유효하지 않다면 401을 보냄
     * - 세션이 유효하다면 SessionContext(ThreadLocal)에 값을 담고, 이를 요청동안 유지
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     * @Author 이건희
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        //Preflight 요청은 허용
        if(RequestMethod.OPTIONS.name().equals(request.getMethod())){
            return true;
        }
        //세션이 유효하지 않으면 401 에러 응답
        if (session == null || session.getAttribute(LOGIN_USER) == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "세션이 유효하지 않습니다.");
            return false;
        }
        //세션 컨텍스트에 저장
        String userId = (String) session.getAttribute(LOGIN_USER);
        SessionContext.setCurrentUser(userId);
        return true;
    }

    /**
     * 요청이 끝난 후, SessionContext(ThreadLocal)에 있는 값 삭제 메서드
     * - 메모리 누수 방지
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     * @Author 이건희
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SessionContext.clear();
    }
}
