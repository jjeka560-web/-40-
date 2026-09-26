package com.example.model

data class KnowledgeSubSection(
    val title: String,
    val content: String,
    val highlightBadge: String? = null
)

data class KnowledgeArticle(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val readingTimeMin: Int,
    val iconName: String,
    val sections: List<KnowledgeSubSection>
)
