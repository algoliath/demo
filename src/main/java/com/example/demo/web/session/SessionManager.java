package com.example.demo.web.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    public void createSession(Object value, HttpServletResponse response){
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);
        Cookie mySessionCookie = new Cookie(SessionConst.LOGIN_MEMBER, sessionId);
        response.addCookie(mySessionCookie);
    }

    public Object getSession(HttpServletRequest request){
        Cookie sessionCookie = findCookie(request, SessionConst.LOGIN_MEMBER);
        if(sessionCookie == null){
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    private Cookie findCookie(HttpServletRequest request, String targetName){
        Cookie[] cookies = request.getCookies();
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(targetName))
                .findFirst()
                .orElse(null);
    }


}
