package com.example.model

enum class WorkoutType(
    val title: String,
    val subtitle: String,
    val description: String,
    val recommendedWorkSec: Int,
    val recommendedRestSec: Int,
    val recommendedRounds: Int
) {
    CARDIO(
        title = "Кардіо",
        subtitle = "Балістика та витривалість",
        description = "Високоінтенсивні протоколи з гирями для тренування міокарда, спалювання вісцерального жиру та підвищення VO2 max без ударного навантаження на колінні суглоби.",
        recommendedWorkSec = 20,
        recommendedRestSec = 10,
        recommendedRounds = 8
    ),
    BODY_FLUSH(
        title = "Промивка організму",
        subtitle = "Капіляризація та васкуляризація кожного м'язу",
        description = "Безперервний помірний рух, що стимулює синтез оксиду азоту (NO) та фактора росту судин VEGF. Відкриває колатеральні капіляри та омиває тканини свіжою кров'ю.",
        recommendedWorkSec = 30,
        recommendedRestSec = 15,
        recommendedRounds = 10
    ),
    WARMUP(
        title = "Розминка",
        subtitle = "Мобільність та синовіальне живлення",
        description = "Суглобова гімнастика та ротаційні рухи для вироблення синовіальної рідини, прогріву ротаторної манжети та підготовки зв'язкового апарату до навантаження.",
        recommendedWorkSec = 30,
        recommendedRestSec = 10,
        recommendedRounds = 6
    ),
    MORNING(
        title = "Ранкова зарядка",
        subtitle = "Пробудження та нейронний тонус",
        description = "М'яка комбінація вправ для зняття нічної скутості хребта, вирівнювання постави та зарядження енергією без викиду надмірного кортизолу.",
        recommendedWorkSec = 25,
        recommendedRestSec = 15,
        recommendedRounds = 6
    ),
    STRENGTH(
        title = "Розвиток сили",
        subtitle = "Щільність кісток та саркопенічний захист",
        description = "Базові силові рухи з акцентом на тазостегновий шарнір (hip-hinge), вертикальний корсет спини та потужний хват. Запобігає віковій атрофії м'язових волокон II типу.",
        recommendedWorkSec = 40,
        recommendedRestSec = 60,
        recommendedRounds = 5
    ),
    ISOMETRIC_STATIC(
        title = "Ізометричні вправи Засса",
        subtitle = "Статична сила та сухожильний захист",
        description = "Наукова система подолання ізометрії атлета Олександра Засса. Сухожилля загартовуються за рахунок 90% зусилля без компресійного зносу суглобів.",
        recommendedWorkSec = 12,
        recommendedRestSec = 45,
        recommendedRounds = 6
    )
}
