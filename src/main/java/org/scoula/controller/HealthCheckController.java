
package org.scoula.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "헬스 체크 API")
@RestController
@RequestMapping("/api/health")
@Log4j2
public class HealthCheckController {

    @GetMapping
    @ApiOperation("서버 상태 확인")
    public Map<String, Object> healthCheck() {
        log.info("Health check called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "동작 중");
        response.put("timestamp", System.currentTimeMillis());
        response.put("message", "Server is running");
        
        return response;
    }

    @GetMapping("/swagger")
    @ApiOperation("Swagger 연동 테스트")
    public Map<String, String> swaggerTest() {
        log.info("Swagger test called");
        
        Map<String, String> response = new HashMap<>();
        response.put("swagger", "enabled");
        response.put("version", "2.0");
        
        return response;
    }


}