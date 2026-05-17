package com.dark.aiagent.infrastructure.persistence.knowledge.converter;

import com.dark.aiagent.domain.knowledge.entity.KnowledgeDocument;
import com.dark.aiagent.infrastructure.persistence.knowledge.entity.KnowledgeDocumentDO;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeDocumentConverter {

    public KnowledgeDocumentDO toDO(KnowledgeDocument domain) {
        if (domain == null) return null;
        KnowledgeDocumentDO dataObject = new KnowledgeDocumentDO();
        dataObject.setId(domain.getId());
        dataObject.setTopicId(domain.getTopicId());
        dataObject.setTitle(domain.getTitle());
        dataObject.setStatus(domain.getStatus());
        dataObject.setAuthor(domain.getAuthor());
        dataObject.setFilePath(domain.getFilePath());
        dataObject.setConfigJson(domain.getConfig());
        dataObject.setFileHash(domain.getFileHash());
        dataObject.setDocType(domain.getDocType());
        dataObject.setCategory(domain.getCategory());
        dataObject.setMetadata(domain.getMetadata());
        dataObject.setCreateTime(domain.getCreateTime());
        dataObject.setUpdateTime(domain.getUpdateTime());
        return dataObject;
    }

    public KnowledgeDocument toDomain(KnowledgeDocumentDO dataObject) {
        if (dataObject == null) return null;
        return new KnowledgeDocument(
            dataObject.getId(),
            dataObject.getTopicId(),
            dataObject.getTitle(),
            dataObject.getStatus(),
            dataObject.getAuthor(),
            dataObject.getFilePath(),
            dataObject.getConfigJson(),
            dataObject.getFileHash(),
            dataObject.getDocType(),
            dataObject.getCategory(),
            dataObject.getMetadata(),
            dataObject.getCreateTime(),
            dataObject.getUpdateTime()
        );
    }
}

