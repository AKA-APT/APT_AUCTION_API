package apt.auctionapi.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Parameter;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        // 핸들러가 HandlerMethod 타입이 아닌 경우 (정적 리소스 등) 통과
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 메서드의 파라미터들을 검사하여 @AuthMember 어노테이션이 있는지 확인
        boolean requiresAuth = false;
        for (Parameter parameter : handlerMethod.getMethod().getParameters()) {
            if (parameter.isAnnotationPresent(AuthMember.class)) {
                AuthMember annotation = parameter.getAnnotation(AuthMember.class);
                if (!annotation.required()) {
                    return true;
                }
                requiresAuth = true;
                break;
            }
        }

        // @AuthMember 어노테이션이 없으면 인증 불필요
        if (!requiresAuth) {
            return true;
        }

        // 인증 체크
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }
}
