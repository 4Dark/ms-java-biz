package com.dark.aiagent.infrastructure.persistence.knowledge.repository;

import com.dark.aiagent.domain.knowledge.entity.KnowledgeDocument;
import com.dark.aiagent.domain.common.PageResult;
import com.dark.aiagent.infrastructure.persistence.knowledge.converter.KnowledgeDocumentConverter;
import com.dark.aiagent.infrastructure.persistence.knowledge.entity.KnowledgeDocumentDO;
import com.dark.aiagent.infrastructure.persistence.knowledge.mapper.KnowledgeDocumentMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeDocumentRepositoryImplTest {

    @Mock
    private KnowledgeDocumentMapper mapper;

    @Mock
    private KnowledgeDocumentConverter converter;

    @InjectMocks
    private KnowledgeDocumentRepositoryImpl repository;

    @Test
    @DisplayName("应当能正确物理分页查询并转换为 PageResult")
    void shouldReturnPageResultWhenQueryingPaged() {
        // Given
        String topicId = "topic-123";
        int page = 1;
        int size = 5;

        KnowledgeDocumentDO do1 = new KnowledgeDocumentDO();
        do1.setId("doc-1");
        do1.setTopicId(topicId);
        do1.setTitle("测试文档");

        Page<KnowledgeDocumentDO> resultPage = new Page<>(page, size);
        resultPage.setRecords(List.of(do1));
        resultPage.setTotal(1L);

        when(mapper.selectPage(any(Page.class), any())).thenReturn(resultPage);

        KnowledgeDocument domainDoc = new KnowledgeDocument(
                "doc-1", topicId, "测试文档", "CREATED", "Antigravity", "path",
                null, null, null, null, null, null, null
        );
        when(converter.toDomain(do1)).thenReturn(domainDoc);

        // When
        PageResult<KnowledgeDocument> result = repository.findByTopicIdPaged(topicId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.records().size());
        assertEquals("doc-1", result.records().get(0).getId());
        assertEquals(1L, result.total());
        assertEquals(5L, result.size());
        assertEquals(1L, result.current());
        assertEquals(1L, result.pages());

        verify(mapper, times(1)).selectPage(any(Page.class), any());
        verify(converter, times(1)).toDomain(do1);
    }
}
