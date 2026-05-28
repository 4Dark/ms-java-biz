package com.dark.aiagent.ephemeral.interfaces.rest;

import java.net.URI;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.dark.aiagent.ephemeral.application.EphemeralRoomUseCase;
import lombok.extern.slf4j.Slf4j;

/**
 * 短链重定向接口
 *
 * <p>
 * 响应行为：
 * <ul>
 * <li>有效短链 → 302 重定向到前端 /#/room/{shortCode}</li>
 * <li>过期/销毁 → 410 Gone</li>
 * </ul>
 *
 * <p>
 * 无 OG Meta 标签，响应头携带 X-Robots-Tag 阻止搜索引擎索引。 微信/钉钉爬虫拿到的是空壳 302，无法预览任何内容。
 */
@Slf4j
@RestController
public class ShortLinkController {

    private final EphemeralRoomUseCase useCase;
    private final String defaultFrontendUrl;
    private final String productionBaseDomain;

    /** 前端房间页路径前缀（由 application.yaml 配置） */
    private static final String FRONTEND_ROOM_BASE = "/#/room/";

    /** 用于判定是否为 IP 地址的预编译正则表达式 */
    private static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");

    /**
     * 构造函数注入，并在实例化时一次性提取生产环境的根域名，避免在高并发下每次请求重复进行解析计算。
     */
    public ShortLinkController(
            EphemeralRoomUseCase useCase,
            @Value("${app.frontend-url}") String defaultFrontendUrl) {
        this.useCase = useCase;
        this.defaultFrontendUrl = defaultFrontendUrl;
        this.productionBaseDomain = extractBaseDomain(defaultFrontendUrl);
    }

    /**
     * 短链访问入口，执行 302 重定向到前端房间页。
     *
     * @param code Base62 短码（8位）
     * @return 302 重定向；过期返回 410
     */
    @GetMapping("/s/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code,
            @RequestHeader(value = "Referer", required = false) String referer,
            @RequestHeader(value = "X-Forwarded-Host", required = false) String forwardedHost,
            @RequestHeader(value = "X-Forwarded-Proto", required = false) String forwardedProto) {
        return useCase.findRoom(code).map(room -> {
            HttpHeaders headers = new HttpHeaders();
            String base = getFrontendBase(referer, forwardedHost, forwardedProto);
            if (base.isEmpty()) {
                base = defaultFrontendUrl;
            }
            if (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            headers.setLocation(URI.create(base + FRONTEND_ROOM_BASE + code));
            // 禁止搜索引擎爬取，防止社交平台缓存任何预览信息
            headers.set("X-Robots-Tag", "noindex, nofollow");
            return new ResponseEntity<Void>(headers, HttpStatus.FOUND);
        }).orElseGet(() -> {
            log.info("【ShortLink】短链已过期或已销毁 code={}", code);
            return ResponseEntity.status(HttpStatus.GONE).build();
        });
    }

    /**
     * 解析前端的基础域名 (Scheme + Authority)。
     * 1. 如果 Referer 存在且为外部开发/预览分支（其根域名与生产根域名不一致），则优先使用 Referer 域名进行跳转。
     * 2. 在其它常规场景下，优先采用用户实际访问时的域名 (X-Forwarded-Host) 进行跳转。
     * 3. 如果以上均不可达，降级使用 Referer 或 defaultFrontendUrl。
     */
    private String getFrontendBase(String referer, String forwardedHost, String forwardedProto) {
        String refererBase = "";
        String refererHost = "";
        if (referer != null && !referer.isBlank()) {
            try {
                URI uri = new URI(referer);
                String scheme = uri.getScheme();
                String authority = uri.getAuthority();
                if (scheme != null && authority != null) {
                    refererBase = scheme + "://" + authority;
                    refererHost = uri.getHost();
                }
            } catch (Exception e) {
                log.warn("【ShortLink】解析 Referer 失败 referer={}", referer, e);
            }
        }

        // 1. 如果 Referer 是开发/预览分支域名（且非空，且其根域名与生产根域名不一致），为了方便联调测试，优先使用 Referer
        if (!refererBase.isEmpty() && !refererHost.isEmpty() && !productionBaseDomain.isEmpty()) {
            String refererBaseDomain = getBaseDomain(refererHost);
            if (!refererBaseDomain.equalsIgnoreCase(productionBaseDomain)) {
                return refererBase;
            }
        }

        // 2. 否则，优先使用访问时的实际请求域名 (X-Forwarded-Host)
        if (forwardedHost != null && !forwardedHost.isBlank()) {
            String scheme = (forwardedProto != null && !forwardedProto.isBlank()) ? forwardedProto : "https";
            return scheme + "://" + forwardedHost.trim();
        }

        // 3. 降级使用 Referer Base
        if (!refererBase.isEmpty()) {
            return refererBase;
        }

        return "";
    }

    /**
     * 辅助静态工具：从给定的默认前端 URL 中提取主域名。
     */
    private static String extractBaseDomain(String url) {
        if (url != null && !url.isBlank()) {
            try {
                URI uri = new URI(url);
                return getBaseDomain(uri.getHost());
            } catch (Exception e) {
                log.warn("【ShortLink】解析 URL 根域名失败 url={}", url, e);
            }
        }
        return "";
    }

    /**
     * 动态提取 Host 的主域名 (根域名)，例如 sub.domain.com 提取为 domain.com
     */
    private static String getBaseDomain(String host) {
        if (host == null || host.isBlank()) {
            return "";
        }
        if (host.equalsIgnoreCase("localhost") || IP_PATTERN.matcher(host).matches()) {
            return host;
        }
        String[] parts = host.split("\\.");
        if (parts.length >= 2) {
            return parts[parts.length - 2] + "." + parts[parts.length - 1];
        }
        return host;
    }
}
