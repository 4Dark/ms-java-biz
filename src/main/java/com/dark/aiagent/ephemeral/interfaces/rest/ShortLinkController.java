package com.dark.aiagent.ephemeral.interfaces.rest;

import com.dark.aiagent.ephemeral.application.EphemeralRoomUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * 短链重定向接口
 *
 * <p>响应行为：
 * <ul>
 *   <li>有效短链 → 302 重定向到前端 /room/{shortCode}</li>
 *   <li>过期/销毁 → 410 Gone</li>
 * </ul>
 *
 * <p>无 OG Meta 标签，响应头携带 X-Robots-Tag 阻止搜索引擎索引。
 * 微信/钉钉爬虫拿到的是空壳 302，无法预览任何内容。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final EphemeralRoomUseCase useCase;

    /** 前端房间页路径前缀（由 application.yaml 配置） */
    private static final String FRONTEND_ROOM_BASE = "/room/";

    /**
     * 短链访问入口，执行 302 重定向到前端房间页。
     *
     * @param code Base62 短码（8位）
     * @return 302 重定向；过期返回 410
     */
    @GetMapping("/s/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return useCase.findRoom(code)
                .map(room -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create(FRONTEND_ROOM_BASE + code));
                    // 禁止搜索引擎爬取，防止社交平台缓存任何预览信息
                    headers.set("X-Robots-Tag", "noindex, nofollow");
                    return new ResponseEntity<Void>(headers, HttpStatus.FOUND);
                })
                .orElseGet(() -> {
                    log.info("【ShortLink】短链已过期或已销毁 code={}", code);
                    return ResponseEntity.status(HttpStatus.GONE).build();
                });
    }
}
