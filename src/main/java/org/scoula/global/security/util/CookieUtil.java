package org.scoula.global.security.util;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

	private static final int MAX_AGE_OF_COOKIE = 3 * 24 * 60 * 60;

	public static void setCookie(HttpServletResponse response, String name, String value) {
		String cookieString = name + "=" + value +
			"; Max-Age=" + MAX_AGE_OF_COOKIE +
			"; Path=/" +
			"; Secure" +
			"; HttpOnly" +
			"; SameSite=None" +
			"; Partitioned";

		response.setHeader("Set-Cookie", cookieString);
	}

	public static void deleteCookie(HttpServletResponse response, String name) {
		ResponseCookie cookie = ResponseCookie.from(name, "value")
			.maxAge(0)
			.path("/")
			.secure(true)
			.httpOnly(true)
			.sameSite("None")
			.build();

		response.setHeader("Set-Cookie", cookie.toString());
	}
}
