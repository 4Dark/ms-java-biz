package com.dark.aiagent.module.aidev.application;

import com.dark.aiagent.module.aidev.domain.entity.AiDevChatMessage;
import com.dark.aiagent.module.aidev.domain.entity.AiDevTask;
import com.dark.aiagent.module.aidev.interfaces.rest.AiDevTokenSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.sql.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AI 开发任务应用服务 (轨道 B: 原生模式)。
 *
 * <p>职责边界：本服务不读取 PostgreSQL，而是通过底层库直接去读写 ~/.hermes/kanban.db。
 */
@Service
@ConditionalOnProperty(name = "ai-dev.integration.mode", havingValue = "NATIVE")
public class NativeAiDevIntegrationServiceImpl implements AiDevIntegrationUseCase {

    @Value("${ai-dev.integration.native.kanban-db-path:${user.home}/.hermes/kanban.db}")
    private String kanbanDbPath;

    private String getDbUrl() {
        return "jdbc:sqlite:" + kanbanDbPath;
    }

    private OffsetDateTime toOffsetDateTime(long epochSeconds) {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
    }

    private String mapStatus(String sqliteStatus) {
        if (sqliteStatus == null) return "PENDING";
        switch (sqliteStatus.toLowerCase()) {
            case "triage":
                return "PENDING";
            case "todo":
                return "PLANNING";
            case "in-progress":
                return "GENERATING";
            case "blocked":
                return "WAITING_ON_APPROVAL";
            case "completed":
                return "COMPLETED";
            default:
                return sqliteStatus.toUpperCase();
        }
    }

    @Override
    public AiDevTokenSummaryResponse getTokenSummary(String taskId) {
        // Native 模式下不记录 token 消耗，返回空数据
        return new AiDevTokenSummaryResponse(0, 0, 0.0, 0, new ArrayList<>());
    }

    @Override
    public List<AiDevTask> getAllTasks() {
        List<AiDevTask> tasks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title, body, status, branch_name, created_at FROM tasks ORDER BY created_at DESC")) {
            while (rs.next()) {
                tasks.add(new AiDevTask(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("body"),
                        mapStatus(rs.getString("status")),
                        rs.getString("branch_name"),
                        0.0,
                        null,
                        toOffsetDateTime(rs.getLong("created_at")),
                        toOffsetDateTime(rs.getLong("created_at"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public Optional<AiDevTask> getTaskById(String id) {
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, title, body, status, branch_name, created_at FROM tasks WHERE id = ?")) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new AiDevTask(
                            rs.getString("id"),
                            rs.getString("title"),
                            rs.getString("body"),
                            mapStatus(rs.getString("status")),
                            rs.getString("branch_name"),
                            0.0,
                            null,
                            toOffsetDateTime(rs.getLong("created_at")),
                            toOffsetDateTime(rs.getLong("created_at"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<AiDevChatMessage> getChatMessages(String taskId) {
        List<AiDevChatMessage> messages = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, task_id, author, body, created_at FROM task_comments WHERE task_id = ? ORDER BY created_at ASC")) {
            pstmt.setString(1, taskId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(new AiDevChatMessage(
                            String.valueOf(rs.getInt("id")),
                            rs.getString("task_id"),
                            rs.getString("author").toUpperCase(),
                            rs.getString("body"),
                            toOffsetDateTime(rs.getLong("created_at")),
                            true
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public AiDevTask createTask(String description, java.util.List<String> relatedWorkspaces) {
        String id = UUID.randomUUID().toString();
        String title = description.length() > 50 ? description.substring(0, 50) + "..." : description;
        long now = Instant.now().getEpochSecond();
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tasks (id, title, body, status, created_at, consecutive_failures, goal_mode) VALUES (?, ?, ?, ?, ?, 0, 0)")) {
            pstmt.setString(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, "triage");
            pstmt.setLong(5, now);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new AiDevTask(id, title, description, "TRIAGE", null, 0.0, null, toOffsetDateTime(now), toOffsetDateTime(now), 5, 3, relatedWorkspaces);
    }

    @Override
    public void resumeTask(String id, String feedback) {
        addHumanMessage(id, feedback != null ? feedback : "Approved");
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement("UPDATE tasks SET status = 'todo' WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollbackTask(String id) {
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement("UPDATE tasks SET status = 'blocked' WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AiDevChatMessage addHumanMessage(String taskId, String content) {
        long now = Instant.now().getEpochSecond();
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO task_comments (task_id, author, body, created_at) VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, taskId);
            pstmt.setString(2, "human");
            pstmt.setString(3, content);
            pstmt.setLong(4, now);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new AiDevChatMessage(UUID.randomUUID().toString(), taskId, "HUMAN", content, toOffsetDateTime(now), false);
    }

    @Override
    public void deleteTask(String id) {
        try (Connection conn = DriverManager.getConnection(getDbUrl())) {
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM task_comments WHERE task_id = ?")) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {
                pstmt.setString(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reopenTask(String id) {
        try (Connection conn = DriverManager.getConnection(getDbUrl());
             PreparedStatement pstmt = conn.prepareStatement("UPDATE tasks SET status = 'triage', branch_name = NULL WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新任务的头脑风暴配置参数（写入 SQLite）。
     * 在写入前调用自愈逻辑，确保列存在。
     */
    @Override
    public void updateTaskConfig(String id, int maxBrainstormingRounds, int contextSlidingWindow) {
        try (Connection conn = DriverManager.getConnection(getDbUrl())) {
            ensureSchemaColumns(conn);
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE tasks SET max_brainstorming_rounds = ?, context_sliding_window = ? WHERE id = ?")) {
                pstmt.setInt(1, maxBrainstormingRounds);
                pstmt.setInt(2, contextSlidingWindow);
                pstmt.setString(3, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Schema 自愈：若 tasks 表尚不包含头脑风暴配置列，则自动补齐。
     * SQLite 的 ALTER TABLE ADD COLUMN IF NOT EXISTS 从 3.37.0 起才支持，
     * 这里用 try-catch 保持兼容性。
     */
    private void ensureSchemaColumns(Connection conn) {
        for (String ddl : new String[]{
                "ALTER TABLE tasks ADD COLUMN max_brainstorming_rounds INTEGER NOT NULL DEFAULT 5",
                "ALTER TABLE tasks ADD COLUMN context_sliding_window   INTEGER NOT NULL DEFAULT 3"
        }) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(ddl);
            } catch (SQLException ignored) {
                // 列已存在时 SQLite 会抛异常，忽略即可
            }
        }
    }

    @Override
    public void processWebhookEvent(java.util.Map<String, Object> payload) {
        // 原生模式下不处理 Webhook
        System.out.println("[NATIVE] Ignored webhook event: " + payload);
    }

    @Override
    public SseEmitter subscribe(String taskId) {
        throw new UnsupportedOperationException("Native mode does not support SSE subscriptions.");
    }
}
