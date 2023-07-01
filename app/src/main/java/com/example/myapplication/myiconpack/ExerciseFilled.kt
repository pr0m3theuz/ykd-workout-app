package com.example.myapplication.myiconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val MyIconPack.ExerciseFilled: ImageVector
    get() {
        if (`_exercise-24px` != null) {
            return `_exercise-24px`!!
        }
        `_exercise-24px` = Builder(
            name = "ExerciseFilled",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(839.0f, 361.0f)
                lineTo(597.0f, 119.0f)
                lineTo(614.0f, 102.0f)
                quadTo(637.0f, 79.0f, 671.0f, 79.5f)
                quadTo(705.0f, 80.0f, 728.0f, 103.0f)
                lineTo(857.0f, 232.0f)
                quadTo(880.0f, 255.0f, 880.0f, 288.5f)
                quadTo(880.0f, 322.0f, 857.0f, 345.0f)
                lineTo(839.0f, 361.0f)
                close()
                moveTo(346.0f, 856.0f)
                quadTo(323.0f, 879.0f, 289.5f, 879.0f)
                quadTo(256.0f, 879.0f, 233.0f, 856.0f)
                lineTo(104.0f, 727.0f)
                quadTo(81.0f, 704.0f, 81.0f, 670.5f)
                quadTo(81.0f, 637.0f, 104.0f, 614.0f)
                lineTo(120.0f, 598.0f)
                lineTo(362.0f, 840.0f)
                lineTo(346.0f, 856.0f)
                close()
                moveTo(491.0f, 828.0f)
                quadTo(479.0f, 840.0f, 463.0f, 840.0f)
                quadTo(447.0f, 840.0f, 435.0f, 828.0f)
                lineTo(132.0f, 525.0f)
                quadTo(120.0f, 513.0f, 120.0f, 497.0f)
                quadTo(120.0f, 481.0f, 132.0f, 469.0f)
                lineTo(189.0f, 411.0f)
                quadTo(201.0f, 399.0f, 217.5f, 399.0f)
                quadTo(234.0f, 399.0f, 246.0f, 411.0f)
                lineTo(309.0f, 474.0f)
                lineTo(475.0f, 308.0f)
                lineTo(412.0f, 245.0f)
                quadTo(400.0f, 233.0f, 400.0f, 217.0f)
                quadTo(400.0f, 201.0f, 412.0f, 189.0f)
                lineTo(469.0f, 131.0f)
                quadTo(481.0f, 119.0f, 497.5f, 119.0f)
                quadTo(514.0f, 119.0f, 526.0f, 131.0f)
                lineTo(829.0f, 434.0f)
                quadTo(841.0f, 446.0f, 841.0f, 462.5f)
                quadTo(841.0f, 479.0f, 829.0f, 491.0f)
                lineTo(771.0f, 548.0f)
                quadTo(759.0f, 560.0f, 743.0f, 560.0f)
                quadTo(727.0f, 560.0f, 715.0f, 548.0f)
                lineTo(652.0f, 485.0f)
                lineTo(486.0f, 651.0f)
                lineTo(549.0f, 714.0f)
                quadTo(561.0f, 726.0f, 561.0f, 742.5f)
                quadTo(561.0f, 759.0f, 549.0f, 771.0f)
                lineTo(491.0f, 828.0f)
                close()
            }
        }
            .build()
        return `_exercise-24px`!!
    }

private var `_exercise-24px`: ImageVector? = null
