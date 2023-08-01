package io.github.opletter.courseevals.site.core.misc

import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.textEllipsis(): Modifier =
    overflow(Overflow.Hidden)
        .whiteSpace(WhiteSpace.NoWrap)
        .textOverflow(TextOverflow.Ellipsis)

fun Modifier.smallCapsFont(): Modifier =
    styleModifier { property("font-variant", "small-caps") }

private external fun balanceText(selector: String, options: dynamic)

fun jsBalanceTextById(id: String) {
    if (jsTypeOf(js("balanceText")) != "function") return
    balanceText("#$id", js("{watch: true}"))
}

fun Double.toFixed(decDigits: Int): String = asDynamic().toFixed(decDigits) as String