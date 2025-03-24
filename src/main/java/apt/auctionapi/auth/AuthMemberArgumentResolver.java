package apt.auctionapi.auth;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import apt.auctionapi.domain.SessionUser;
import apt.auctionapi.entity.Member;
import apt.auctionapi.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthMember.class) &&
            parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        AuthMember authMember = parameter.getParameterAnnotation(AuthMember.class);
        if (authMember == null || !authMember.required()) {
            return memberRepository.findById(((SessionUser)session.getAttribute("user")).id())
                .orElse(null);
        }

        return memberRepository.findById(((SessionUser)session.getAttribute("user")).id())
            .orElseThrow(() -> new RuntimeException("해당 사용자가 존재하지 않습니다."));
    }
}
