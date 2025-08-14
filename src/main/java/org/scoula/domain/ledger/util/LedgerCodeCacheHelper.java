package org.scoula.domain.ledger.util;

import java.util.Locale;
import java.util.Optional;

import org.scoula.domain.ledger.entity.LedgerCode;
import org.scoula.domain.ledger.mapper.LedgerCodeMapper;
import org.scoula.global.redis.util.RedisUtil;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LedgerCodeCacheHelper {

	private final RedisUtil redisUtil;
	private final ObjectMapper objectMapper;
	private final LedgerCodeMapper ledgerCodeMapper;

	private static final String KEY_PREFIX = "LedgerCode:"; // e.g. LedgerCode:BANK_PAYABLE
	private static final int TTL_MINUTES = 24 * 60;         // 24시간 고정

	private static String normalize(String name) {
		return name == null ? "" : name.trim().toUpperCase(Locale.ROOT);
	}

	private static String keyOf(String name) {
		return KEY_PREFIX + normalize(name);
	}

	public Optional<LedgerCode> getByName(String name) {
		final String key = keyOf(name);

		String cached = redisUtil.get(key);
		if (cached != null) {
			try {
				return Optional.of(objectMapper.readValue(cached, LedgerCode.class));
			} catch (Exception e) {
				redisUtil.delete(key);
			}
		}

		Optional<LedgerCode> fromDb = ledgerCodeMapper.findByName(normalize(name));

		fromDb.ifPresent(code -> {
			try {
				String json = objectMapper.writeValueAsString(code);
				redisUtil.set(key, json, TTL_MINUTES);
			} catch (Exception ignore) {
				throw new RuntimeException(ignore);
			}
		});

		return fromDb;
	}

	public void put(String name, LedgerCode code) {
		try {
			redisUtil.set(keyOf(name), objectMapper.writeValueAsString(code), TTL_MINUTES);
		} catch (Exception ignore) {
			throw new RuntimeException(ignore);
		}
	}

	public void evict(String name) {
		redisUtil.delete(keyOf(name));
	}

	public void warm(String... names) {
		for (String n : names) {
			getByName(n); // 미스면 DB→캐시 적재
		}
	}
}
