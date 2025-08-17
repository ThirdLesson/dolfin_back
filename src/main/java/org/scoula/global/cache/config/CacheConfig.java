package org.scoula.global.cache.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.github.benmanes.caffeine.cache.Caffeine;

@EnableAsync // <-- 비동기 기능을 활성화합니다.
@Configuration
@EnableCaching // 캐시 기능 활성화
public class CacheConfig {

    @Bean // Spring에게 CacheManager 객체를 등록
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("ledgerCodes"); // 1. 캐시 이름 등록
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    // 2. Caffeine 캐시의 상세 설정을 정의
    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(10)       // 초기 저장 공간 10개
                .maximumSize(50)           // 최대 50개까지 저장
                .expireAfterWrite(1, TimeUnit.HOURS) // 데이터를 쓴 후 1시간이 지나면 자동 삭제
                .recordStats();            // 캐시 사용 통계 기록
    }
}
