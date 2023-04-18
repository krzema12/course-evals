package io.github.opletter.courseevals.site.core.components.widgets

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.RadialGradient
import com.varabyte.kobweb.compose.css.functions.min
import com.varabyte.kobweb.compose.css.functions.radialGradient
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.toSilkPalette
import io.github.opletter.courseevals.site.core.SitePalettes
import io.github.opletter.courseevals.site.core.misc.jsFormatNum
import io.github.opletter.courseevals.site.core.misc.smallCapsFont
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Text

val BarGraphStyle by ComponentStyle {
    base {
        Modifier
            .fillMaxWidth()
            .fontSize(min(4.5.vw, 1.25.cssRem))
            .styleModifier { property("aspect-ratio", "4 / 3") }
            .padding(topBottom = 0.33.cssRem, leftRight = 0.5.cssRem)
            .borderRadius(12.px)
            .color(colorMode.toSilkPalette().background)
            .backgroundImage(
                radialGradient(
                    RadialGradient.Shape.Circle,
                    rgb(14, 14, 42),
                    rgb(50, 57, 84),
                    CSSPosition(Edge.CenterX, Edge.Bottom),
                )
            )
//            .backgroundImage(
//                radialGradient(
//                    RadialGradient.Shape.Circle,
//                    rgb(41, 41, 46),
//                    rgb(25, 25, 28)
//                )
//            )
    }
    Breakpoint.LG {
        Modifier
            .width(Width.Unset)
            .minHeight(100.percent)
            .fontSize(1.5.cssRem)
            .styleModifier { property("aspect-ratio", "3.5 / 3") }
    }
    Breakpoint.XL {
        Modifier
            .styleModifier { property("aspect-ratio", "4 / 3") }
    }
}

val BarGraphBarStyle by ComponentStyle {
    val barColor = colorMode.toSilkPalette().background
    base {
        Modifier
            .width(75.percent)
            .backgroundColor(barColor)
            .borderBottom(1.px, LineStyle.Solid, barColor) // needed for when num is 0
            .transition(CSSTransition("height", 0.3.s, TransitionTimingFunction.EaseOut))
    }
}

@Composable
fun BarGraph(
    ratings: List<Int>,
    label: String,
    modifier: Modifier = Modifier,
    max: Int = ratings.maxOrNull() ?: 0,
) {
    var barAnimHeight by remember { mutableStateOf(0.percent) }
    var mouseOver: Boolean by remember { mutableStateOf(false) }

    val labelTextColor = SitePalettes[ColorMode.LIGHT].accent

    Column(BarGraphStyle.toModifier().then(modifier)) {
        SpanText(
            label,
            Modifier
                .margin(left = 0.75.cssRem)
                .color(labelTextColor)
                .fontSize(175.percent)
                .fontWeight(FontWeight.Bolder)
                .smallCapsFont()
        )

        Row(Modifier.fillMaxWidth().flexGrow(1)) {
            ratings.forEachIndexed { index, num ->
                Column(
                    Modifier.fillMaxHeight().flex(1),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    val barHeight = num.toDouble() / max * barAnimHeight
                    Box(Modifier.height(100.percent - barHeight)) // needed so that real bar is correct height
                    Text(
                        if (mouseOver) num.toString()
                        else jsFormatNum(num = num.toDouble() / ratings.sum() * 100, decDigits = 0).let { "$it%" }
                    )
                    Box(
                        BarGraphBarStyle.toModifier()
                            .height(barHeight)
                            .onMouseEnter { mouseOver = true }
                            .onMouseLeave { mouseOver = false }
                    )
                    SpanText(
                        (index + 1).toString(),
                        Modifier
                            .fontSize(133.percent)
                            .fontWeight(FontWeight.Bolder)
                            .color(labelTextColor)
                    )
                }
            }
        }
    }
    SideEffect { // needed for animation to run AFTER initial recomposition
        barAnimHeight = 100.percent
    }
}
