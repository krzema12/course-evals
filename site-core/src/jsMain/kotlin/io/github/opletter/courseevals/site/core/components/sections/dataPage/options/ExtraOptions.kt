package io.github.opletter.courseevals.site.core.components.sections.dataPage.options

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import io.github.opletter.courseevals.site.core.components.widgets.ClosableTransitionObject
import io.github.opletter.courseevals.site.core.misc.ExtraOptions
import io.github.opletter.courseevals.site.core.states.DataPageVM
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.s

// should be used by all extra options
val ExtraOptionStyle by ComponentStyle {
    base {
        Modifier
            .fillMaxWidth()
            .backgroundColor(Colors.Black.copyf(alpha = 0.5f))
            .borderRadius(12.px)
            .padding(0.5.cssRem)
            .flexBasis(100.percent)
    }
}

@Composable
fun ExtraOptions(viewModel: DataPageVM, open: Boolean) {
    ClosableTransitionObject(
        open = open,
        openModifier = Modifier.opacity(1),
        closedModifier = Modifier.opacity(0),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .overflowY(Overflow.Auto)
                .rowGap(0.5.cssRem)
                .transition(
                    CSSTransition(
                        "opacity",
                        0.2.s,
                        if (open) TransitionTimingFunction.EaseIn else TransitionTimingFunction.EaseOut
                    )
                ).then(it),
        ) {
            viewModel.college.options.forEach { option ->
                when (option) {
                    ExtraOptions.CAMPUS -> CampusOption(viewModel.campusVM, viewModel.levelOfStudyVM)
                    ExtraOptions.MIN_SEM -> MinSemOption(viewModel.minSemVM)
                }
            }
        }
    }
}