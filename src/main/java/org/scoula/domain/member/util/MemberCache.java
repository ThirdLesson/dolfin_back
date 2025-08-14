package org.scoula.domain.member.util;

import org.scoula.domain.member.dto.response.MemberByPhoneNumberCacheResponse;
import org.scoula.global.redis.util.RedisUtil;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberCache {
	private final RedisUtil redis;
	private final ObjectMapper om;

	private static final String PREFIX = "MemberMapper:selectMemberByPhoneNumber:";
	private static final int SELECT_BY_PHONE_TTL_MINUTES = 60 * 12;

	private String normalize(String raw) {
		return raw.replaceAll("[^0-9]", "");
	}

	private String key(String phone) {
		return PREFIX + normalize(phone);
	}

	public MemberByPhoneNumberCacheResponse get(String phone) {
		String v = redis.get(key(phone));
		if (v == null)
			return null;
		try {
			return om.readValue(v, MemberByPhoneNumberCacheResponse.class);
		} catch (Exception e) {
			redis.delete(key(phone));
			return null;
		}
	}

	public void put(String phone, MemberByPhoneNumberCacheResponse lite) {
		try {
			redis.set(key(phone), om.writeValueAsString(lite), SELECT_BY_PHONE_TTL_MINUTES);
		} catch (Exception ignore) {
		}
	}

	public void evictByPhone(String phone) {
		redis.delete(key(phone));
	}
}

