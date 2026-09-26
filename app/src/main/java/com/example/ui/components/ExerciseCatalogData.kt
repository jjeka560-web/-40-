package com.example.ui.components

data class ExerciseCatalogOption(
    val category: String,
    val name: String,
    val defaultSets: Int = 3,
    val defaultReps: Int = 10,
    val defaultRest: Int = 90
)

object ExerciseCatalogData {
    val options: List<ExerciseCatalogOption> = listOf(
        // 🦵 Ноги
        ExerciseCatalogOption("🦵 Ноги", "Кубкові присідання (Goblet Squat)", 3, 12, 90),
        ExerciseCatalogOption("🦵 Ноги", "Випади з гирями", 3, 10, 60),
        ExerciseCatalogOption("🦵 Ноги", "Румунська тяга з гирею", 3, 10, 75),
        ExerciseCatalogOption("🦵 Ноги", "Присідання Сумо з гирею", 3, 12, 60),

        // 🏋️‍♂️ Спина
        ExerciseCatalogOption("🏋️‍♂️ Спина", "Мах гирею двома руками", 4, 20, 60),
        ExerciseCatalogOption("🏋️‍♂️ Спина", "Тяга гирі в нахилі", 3, 10, 60),
        ExerciseCatalogOption("🏋️‍♂️ Спина", "Тяга ренегата", 3, 8, 60),
        ExerciseCatalogOption("🏋️‍♂️ Спина", "Мертва тяга з 2 гирями", 3, 10, 90),

        // 🛡 Прес та Кор
        ExerciseCatalogOption("🛡 Прес та Кор", "Турецький підйом", 3, 3, 60),
        ExerciseCatalogOption("🛡 Прес та Кор", "Прогулянка фермера", 4, 1, 60),
        ExerciseCatalogOption("🛡 Прес та Кор", "Російська скрутка з гирею", 3, 15, 45),
        ExerciseCatalogOption("🛡 Прес та Кор", "Вітряк (Windmill) з гирею", 3, 6, 60),

        // 🦍 Грудні м'язи
        ExerciseCatalogOption("🦍 Грудні м'язи", "Жим гирь лежачи на підлозі (Floor Press)", 3, 12, 60),
        ExerciseCatalogOption("🦍 Грудні м'язи", "Віджимання на гирях (глибокі)", 3, 12, 60),
        ExerciseCatalogOption("🦍 Грудні м'язи", "Пулловер з гирею", 3, 10, 60),

        // 💪 Руки
        ExerciseCatalogOption("💪 Руки", "Згинання рук на біцепс з гирею", 3, 12, 45),
        ExerciseCatalogOption("💪 Руки", "Французький жим з гирею (трицепс)", 3, 10, 45),
        ExerciseCatalogOption("💪 Руки", "Взяття гирі на груди (Clean)", 3, 10, 60),

        // 🗿 Плечі
        ExerciseCatalogOption("🗿 Плечі", "Жим гирі стоячи", 3, 8, 60),
        ExerciseCatalogOption("🗿 Плечі", "Жим гирі дном догори (Bottoms-Up)", 3, 6, 90),
        ExerciseCatalogOption("🗿 Плечі", "Обертання навколо голови (Halo)", 3, 10, 45),
        ExerciseCatalogOption("🗿 Плечі", "Тяга гирі до підборіддя", 3, 10, 60)
    )
}
