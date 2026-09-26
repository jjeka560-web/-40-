package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AtlasExercise
import com.example.model.AtlasRepository
import com.example.model.KnowledgeArticle
import com.example.model.KnowledgeRepository
import com.example.model.KnowledgeSubSection
import com.example.ui.components.AtlasDiagramIllustration
import com.example.ui.theme.appSurfaceStyle

@Composable
fun KnowledgeBaseScreen(
    onAddExerciseToPlan: (String, Int, Int, Int, String) -> Unit = { _, _, _, _, _ -> },
    onLaunchExerciseInTimer: (AtlasExercise) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTopTab by remember { mutableIntStateOf(0) } // 0 = Наукова База, 1 = Ілюстрований Атлас

    Column(modifier = modifier.fillMaxSize()) {
        // Top Secondary Navigation TabRow
        TabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTopTab == 0,
                onClick = { selectedTopTab = 0 },
                text = {
                    Text(
                        text = "📖 Наукова База 40+",
                        fontSize = 13.sp,
                        fontWeight = if (selectedTopTab == 0) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                modifier = Modifier.testTag("tab_knowledge_articles")
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = {
                    Text(
                        text = "🏋️‍♂️ Ілюстрований Атлас",
                        fontSize = 13.sp,
                        fontWeight = if (selectedTopTab == 1) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                modifier = Modifier.testTag("tab_knowledge_atlas")
            )
        }

        if (selectedTopTab == 0) {
            ArticlesSection(modifier = Modifier.fillMaxSize())
        } else {
            AtlasSection(
                onAddToPlan = onAddExerciseToPlan,
                onLaunchInTimer = onLaunchExerciseInTimer,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ==========================================
// 1. НАУКОВА БАЗА СТАТЕЙ
// ==========================================
@Composable
private fun ArticlesSection(modifier: Modifier = Modifier) {
    val allArticles = KnowledgeRepository.articles
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Всі") }
    var selectedArticle by remember { mutableStateOf<KnowledgeArticle?>(null) }

    val categories = listOf("Всі") + allArticles.map { it.category }.distinct()

    val filteredArticles = allArticles.filter { article ->
        val matchesCategory = (selectedCategory == "Всі" || article.category == selectedCategory)
        val matchesQuery = searchQuery.isBlank() ||
                article.title.contains(searchQuery, ignoreCase = true) ||
                article.summary.contains(searchQuery, ignoreCase = true) ||
                article.sections.any { it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesQuery
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 768.dp

        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                    SearchAndFilterHeader(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelect = { selectedCategory = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredArticles) { article ->
                            ArticleSummaryCard(
                                article = article,
                                isSelected = article == (selectedArticle ?: filteredArticles.firstOrNull()),
                                onClick = { selectedArticle = article }
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1.3f).fillMaxSize()) {
                    val active = selectedArticle ?: filteredArticles.firstOrNull() ?: allArticles.first()
                    ArticleReaderView(
                        article = active,
                        onBack = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            if (selectedArticle != null) {
                BackHandler { selectedArticle = null }
                ArticleReaderView(
                    article = selectedArticle!!,
                    onBack = { selectedArticle = null },
                    modifier = Modifier.fillMaxSize().padding(14.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SearchAndFilterHeader(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onCategorySelect = { selectedCategory = it }
                        )
                    }

                    items(filteredArticles) { article ->
                        ArticleSummaryCard(
                            article = article,
                            isSelected = false,
                            onClick = { selectedArticle = article }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. ІЛЮСТРОВАНИЙ АТЛАС ВПРАВ (PDF +200)
// ==========================================
@Composable
private fun AtlasSection(
    onAddToPlan: (String, Int, Int, Int, String) -> Unit,
    onLaunchInTimer: (AtlasExercise) -> Unit,
    modifier: Modifier = Modifier
) {
    val allAtlasExercises = AtlasRepository.allExercises
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("Всі") }
    var selectedExercise by remember { mutableStateOf<AtlasExercise?>(null) }

    val groups = listOf("Всі") + allAtlasExercises.map { it.group }.distinct()

    val filteredAtlas = allAtlasExercises.filter { ex ->
        val matchesGroup = (selectedGroup == "Всі" || ex.group == selectedGroup)
        val matchesQuery = searchQuery.isBlank() ||
                ex.name.contains(searchQuery, ignoreCase = true) ||
                ex.targetMuscles.contains(searchQuery, ignoreCase = true) ||
                ex.initialPose.contains(searchQuery, ignoreCase = true) ||
                ex.steps.any { it.contains(searchQuery, ignoreCase = true) } ||
                (ex.historicalRecord != null && ex.historicalRecord.contains(searchQuery, ignoreCase = true))
        matchesGroup && matchesQuery
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 768.dp

        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column: Search & List
                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                    AtlasFilterHeader(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        groups = groups,
                        selectedGroup = selectedGroup,
                        onGroupSelect = { selectedGroup = it }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredAtlas) { ex ->
                            AtlasExerciseItemMini(
                                exercise = ex,
                                isSelected = ex == (selectedExercise ?: filteredAtlas.firstOrNull()),
                                onClick = { selectedExercise = ex }
                            )
                        }
                    }
                }

                // Right Column: Detailed View with Illustrated Diagram
                Column(modifier = Modifier.weight(1.3f).fillMaxSize()) {
                    val active = selectedExercise ?: filteredAtlas.firstOrNull() ?: allAtlasExercises.first()
                    AtlasExerciseDetailCard(
                        exercise = active,
                        onBack = null,
                        onAddToPlan = { onAddToPlan(active.name, 3, 10, 60, active.group) },
                        onLaunchInTimer = { onLaunchInTimer(active) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Mobile: List or Detail Screen
            if (selectedExercise != null) {
                BackHandler { selectedExercise = null }
                AtlasExerciseDetailCard(
                    exercise = selectedExercise!!,
                    onBack = { selectedExercise = null },
                    onAddToPlan = { onAddToPlan(selectedExercise!!.name, 3, 10, 60, selectedExercise!!.group) },
                    onLaunchInTimer = { onLaunchInTimer(selectedExercise!!) },
                    modifier = Modifier.fillMaxSize().padding(14.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "Атлас Вправ від Силачів Минулого",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Повна енциклопедія рухів з векторними ілюстраціями та біомеханікою",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        AtlasFilterHeader(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            groups = groups,
                            selectedGroup = selectedGroup,
                            onGroupSelect = { selectedGroup = it }
                        )
                    }

                    items(filteredAtlas) { ex ->
                        AtlasExerciseItemMini(
                            exercise = ex,
                            isSelected = false,
                            onClick = { selectedExercise = ex }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AtlasFilterHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    groups: List<String>,
    selectedGroup: String,
    onGroupSelect: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Пошук вправи (напр. Гаккеншмідт, ривок, жим)...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("atlas_search_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(groups) { group ->
                FilterChip(
                    selected = (group == selectedGroup),
                    onClick = { onGroupSelect(group) },
                    label = { Text(group, fontSize = 11.sp, maxLines = 1, softWrap = false) },
                    modifier = Modifier.testTag("atlas_filter_$group")
                )
            }
        }
    }
}

@Composable
private fun AtlasExerciseItemMini(
    exercise: AtlasExercise,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .appSurfaceStyle(cornerRadius = 12.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("atlas_item_${exercise.number}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "№${exercise.number}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${exercise.group} • ${exercise.targetMuscles}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AtlasExerciseDetailCard(
    exercise: AtlasExercise,
    onBack: (() -> Unit)?,
    onAddToPlan: () -> Unit,
    onLaunchInTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var planAddedSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .appSurfaceStyle(cornerRadius = 16.dp)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("atlas_detail_back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Вправа №${exercise.number}: ${exercise.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
                Text(
                    text = "Група: ${exercise.group}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Illustrated Canvas Vector Diagram
        AtlasDiagramIllustration(
            diagramType = exercise.diagramType,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons Row (Add to plan & Launch in timer)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    onAddToPlan()
                    planAddedSuccess = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("atlas_add_to_plan"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (planAddedSuccess) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (planAddedSuccess) "Додано!" else "+ Додати в план",
                    fontSize = 12.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }

            OutlinedButton(
                onClick = onLaunchInTimer,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("atlas_launch_timer")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("В таймер", fontSize = 12.sp, maxLines = 1, softWrap = false)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Exercise Step-by-Step Info in Scrollable Column
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Historical record banner if exists
            exercise.historicalRecord?.let { record ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD97706).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFD97706).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = record,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Initial Pose
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Column {
                    Text("📍 Вихідне положення:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(exercise.initialPose, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            // Steps
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("📋 Покрокове виконання:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    exercise.steps.forEach { step ->
                        Text(step, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Target Muscles & Breathing
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("🎯 М'язи:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(exercise.targetMuscles, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("💨 Дихання:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(exercise.breathingTip, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Biomechanics note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Column {
                    Text("🔬 Біомеханіка & Безпека:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(exercise.biomechanicsNote, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilterHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Пошук теми (напр. Засс, колаген, ангіогенез)...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("knowledge_search_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = (cat == selectedCategory),
                    onClick = { onCategorySelect(cat) },
                    label = { Text(cat, fontSize = 11.sp, maxLines = 1, softWrap = false) },
                    modifier = Modifier.testTag("filter_chip_$cat")
                )
            }
        }
    }
}

@Composable
private fun ArticleSummaryCard(
    article: KnowledgeArticle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (article.iconName) {
        "BloodPressure" -> Icons.Default.Favorite
        "Accessibility" -> Icons.Default.Accessibility
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "EmojiEvents" -> Icons.Default.EmojiEvents
        "MenuBook" -> Icons.Default.MenuBook
        "Restaurant" -> Icons.Default.Restaurant
        "Warning" -> Icons.Default.Warning
        else -> Icons.Default.Book
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .appSurfaceStyle(cornerRadius = 14.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("article_card_${article.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = article.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    fontSize = 11.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ArticleReaderView(
    article: KnowledgeArticle,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .appSurfaceStyle(cornerRadius = 16.dp)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("article_reader_back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = article.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• ~${article.readingTimeMin} хв",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            article.sections.forEach { section ->
                ArticleSubSectionCard(section = section)
            }
        }
    }
}

@Composable
private fun ArticleSubSectionCard(section: KnowledgeSubSection) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            section.highlightBadge?.let { badge ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = section.content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 18.sp
        )
    }
}
