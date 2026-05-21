package com.dark.aiagent.infrastructure.persistence.knowledge.repository;

import com.dark.aiagent.domain.knowledge.entity.KnowledgeDocument;
import com.dark.aiagent.domain.knowledge.repository.KnowledgeDocumentRepository;
import com.dark.aiagent.domain.common.PageResult;
import com.dark.aiagent.infrastructure.persistence.knowledge.converter.KnowledgeDocumentConverter;
import com.dark.aiagent.infrastructure.persistence.knowledge.entity.KnowledgeDocumentDO;
import com.dark.aiagent.infrastructure.persistence.knowledge.mapper.KnowledgeDocumentMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;

@Repository
public class KnowledgeDocumentRepositoryImpl implements KnowledgeDocumentRepository {

    private final KnowledgeDocumentMapper mapper;
    private final KnowledgeDocumentConverter converter;

    public KnowledgeDocumentRepositoryImpl(KnowledgeDocumentMapper mapper, KnowledgeDocumentConverter converter) {
        this.mapper = mapper;
        this.converter = converter;
    }

    @Override
    public void save(KnowledgeDocument document) {
        KnowledgeDocumentDO dataObject = converter.toDO(document);
        if (mapper.selectById(dataObject.getId()) != null) {
            mapper.updateById(dataObject);
        } else {
            mapper.insert(dataObject);
        }
    }

    @Override
    public Optional<KnowledgeDocument> findById(String id) {
        KnowledgeDocumentDO dataObject = mapper.selectById(id);
        return Optional.ofNullable(converter.toDomain(dataObject));
    }

    @Override
    public void update(KnowledgeDocument document) {
        KnowledgeDocumentDO dataObject = converter.toDO(document);
        mapper.updateById(dataObject);
    }

    @Override
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public List<KnowledgeDocument> findByTopicId(String topicId) {
        QueryWrapper<KnowledgeDocumentDO> query = new QueryWrapper<>();
        query.eq("topic_id", topicId);
        return mapper.selectList(query).stream().map(converter::toDomain).collect(Collectors.toList());
    }

    @Override
    public PageResult<KnowledgeDocument> findByTopicIdPaged(String topicId, int page, int size) {
        Page<KnowledgeDocumentDO> mpPage = new Page<>(page, size);
        QueryWrapper<KnowledgeDocumentDO> query = new QueryWrapper<>();
        query.eq("topic_id", topicId);
        query.orderByDesc("create_time"); // Order by create_time descending for best UX
        IPage<KnowledgeDocumentDO> resultPage = mapper.selectPage(mpPage, query);
        List<KnowledgeDocument> records = resultPage.getRecords().stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
        return new PageResult<>(
                records,
                resultPage.getTotal(),
                resultPage.getSize(),
                resultPage.getCurrent(),
                resultPage.getPages()
        );
    }
}
