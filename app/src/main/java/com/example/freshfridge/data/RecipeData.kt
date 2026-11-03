package com.example.freshfridge.data

import com.example.freshfridge.Recipe

object RecipeData {

    val recipes = listOf(

        Recipe(
            id = 1,
            name = "西红柿炒鸡蛋",
            description = "经典家常菜，简单美味",
            ingredients = listOf("西红柿", "鸡蛋"),
            steps = listOf(
                "将西红柿洗净切块",
                "将鸡蛋打入碗中搅拌均匀",
                "锅中倒油，炒熟鸡蛋后盛出",
                "放入西红柿翻炒",
                "加入鸡蛋和适量调料，翻炒均匀即可"
            ),
            cookingTime = 15,
            difficulty = "简单"
        ),

        Recipe(
            id = 2,
            name = "西兰花炒鸡蛋",
            description = "营养丰富的家常菜",
            ingredients = listOf("西兰花", "鸡蛋"),
            steps = listOf(
                "将西兰花洗净切成小块",
                "将西兰花放入沸水中焯水",
                "鸡蛋打散后炒熟",
                "加入西兰花翻炒",
                "加入调料后翻炒均匀即可"
            ),
            cookingTime = 15,
            difficulty = "简单"
        ),

        Recipe(
            id = 3,
            name = "西红柿鸡蛋汤",
            description = "清淡爽口，制作方便",
            ingredients = listOf("西红柿", "鸡蛋"),
            steps = listOf(
                "西红柿洗净切块",
                "鸡蛋打散备用",
                "锅中加水烧开",
                "放入西红柿煮几分钟",
                "倒入鸡蛋液，加入调料即可"
            ),
            cookingTime = 10,
            difficulty = "简单"
        ),

        Recipe(
            id = 4,
            name = "牛奶鸡蛋羹",
            description = "口感细腻，营养丰富",
            ingredients = listOf("牛奶", "鸡蛋"),
            steps = listOf(
                "将鸡蛋打入碗中",
                "加入牛奶搅拌均匀",
                "过滤蛋液中的气泡",
                "盖上保鲜膜并扎几个小孔",
                "放入蒸锅蒸熟即可"
            ),
            cookingTime = 15,
            difficulty = "简单"
        ),

        Recipe(
            id = 5,
            name = "苹果牛奶",
            description = "清爽健康的水果饮品",
            ingredients = listOf("苹果", "牛奶"),
            steps = listOf(
                "苹果洗净去核切块",
                "将苹果放入搅拌机",
                "倒入牛奶",
                "搅拌均匀即可"
            ),
            cookingTime = 5,
            difficulty = "简单"
        ),

        Recipe(
            id = 6,
            name = "清炒西兰花",
            description = "清淡爽口的绿色蔬菜",
            ingredients = listOf("西兰花"),
            steps = listOf(
                "西兰花洗净切块",
                "锅中烧水，将西兰花焯水",
                "锅中倒油",
                "放入西兰花翻炒",
                "加入适量盐调味即可"
            ),
            cookingTime = 10,
            difficulty = "简单"
        )
    )
}