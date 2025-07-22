package org.scoula.global.redis.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtil {

	private final RedisTemplate<String, String> redisTemplate;

	public void set(String key, String o, int minutes) {
		redisTemplate.opsForValue().set(key, o, minutes, TimeUnit.MINUTES);
	}

	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public boolean delete(String key) {
		return redisTemplate.delete(key);
	}

	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	public Long checkExpired(String key) {
		Long ttl = redisTemplate.getExpire(key);
		return ttl;
	}

	public long countResumeKeysWithPrefix() {
		ScanOptions options = ScanOptions.scanOptions()
			.match("resume:temp:" + "*")
			.count(1000) // 한 번에 검색할 키 수
			.build();

		long count = 0;
		try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
			.getConnection()
			.scan(options)) {

			while (cursor.hasNext()) {
				cursor.next();
				count++;
			}
		} catch (Exception e) {
			throw new RuntimeException("Redis scan error", e);
		}

		return count;
	}

	// prefix 기반 키 삭제
	public void deleteByPrefix(String prefix) {
		var keys = redisTemplate.keys(prefix + "*");
		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}
}

