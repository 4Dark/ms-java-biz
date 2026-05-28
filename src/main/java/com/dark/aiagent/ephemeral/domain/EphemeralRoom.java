package com.dark.aiagent.ephemeral.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * 阅后即焚通讯空间（房间）领域实体（充血模型）
 *
 * <p>服务器只存密文，密钥永不上传。
 * 房间生命周期由 TTL + 活跃延长 + 手动销毁三种机制共同控制。
 */
public class EphemeralRoom {

    private final String id;
    private final String shortCode;
    private final String title;
    private final long maxTtlSeconds;
    private OffsetDateTime expireAt;
    private OffsetDateTime lastActiveAt;
    private final String createdBy;
    private boolean destroyed;
    private final OffsetDateTime createTime;

    /**
     * 完整构造函数，用于从持久化层还原对象。
     */
    public EphemeralRoom(String id, String shortCode, String title,
                         long maxTtlSeconds, OffsetDateTime expireAt,
                         OffsetDateTime lastActiveAt, String createdBy,
                         boolean destroyed, OffsetDateTime createTime) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Room id cannot be empty");
        if (shortCode == null || shortCode.isBlank()) throw new IllegalArgumentException("Short code cannot be empty");
        if (maxTtlSeconds <= 0) throw new IllegalArgumentException("maxTtlSeconds must be positive");
        this.id = id;
        this.shortCode = shortCode;
        this.title = title;
        this.maxTtlSeconds = maxTtlSeconds;
        this.expireAt = expireAt;
        this.lastActiveAt = lastActiveAt;
        this.createdBy = createdBy;
        this.destroyed = destroyed;
        this.createTime = createTime;
    }

    /**
     * 工厂方法：创建一个新房间。
     *
     * @param id            UUID
     * @param shortCode     Base62 短码
     * @param title         可选公开名称
     * @param maxTtlSeconds 创建者设定的最大存活秒数
     * @param createdBy     创建者匿名 ID
     * @return 新建的 EphemeralRoom 实体
     */
    public static EphemeralRoom create(String id, String shortCode, String title,
                                       long maxTtlSeconds, String createdBy) {
        OffsetDateTime now = OffsetDateTime.now();
        return new EphemeralRoom(id, shortCode, title, maxTtlSeconds,
                now.plusSeconds(maxTtlSeconds), now, createdBy, false, now);
    }

    /**
     * 活跃延长 TTL：最多延长到 createTime + maxTtlSeconds，避免无限续期。
     *
     * @param activityExtendSeconds 每次活跃延长的秒数
     */
    public void extendOnActivity(long activityExtendSeconds) {
        if (destroyed || isExpired()) return;
        OffsetDateTime ceiling = createTime.plusSeconds(maxTtlSeconds);
        OffsetDateTime extended = OffsetDateTime.now().plusSeconds(activityExtendSeconds);
        this.expireAt = extended.isBefore(ceiling) ? extended : ceiling;
        this.lastActiveAt = OffsetDateTime.now();
    }

    /**
     * 手动销毁房间。
     */
    public void destroy() {
        this.destroyed = true;
    }

    /**
     * 判断房间是否已过期（TTL 到期 或 手动销毁）。
     *
     * @return true 表示房间不再有效
     */
    public boolean isExpired() {
        return destroyed || OffsetDateTime.now().isAfter(expireAt);
    }

    // --- Getters ---

    public String getId() { return id; }
    public String getShortCode() { return shortCode; }
    public String getTitle() { return title; }
    public long getMaxTtlSeconds() { return maxTtlSeconds; }
    public OffsetDateTime getExpireAt() { return expireAt; }
    public OffsetDateTime getLastActiveAt() { return lastActiveAt; }
    public String getCreatedBy() { return createdBy; }
    public boolean isDestroyed() { return destroyed; }
    public OffsetDateTime getCreateTime() { return createTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((EphemeralRoom) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
