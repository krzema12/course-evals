package io.github.opletter.courseevals.site

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.overflowX
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.components.style.breakpoint.BreakpointSizes
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vh

private const val COLOR_MODE_KEY = "course-evals:colorMode"

@InitSilk
fun setBreakpoints(ctx: InitSilkContext) {
    // Breakpoints defined where layout breaks
    // SideNav is MD+, while LG & XL are used
    ctx.theme.breakpoints = BreakpointSizes(
        sm = 30.cssRem,
        md = 48.cssRem,
        lg = 73.cssRem,
        xl = 79.cssRem,
    )
}

@App
@Composable
fun MyApp(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = getColorMode()
        remember(colorMode) {
            localStorage.setItem(COLOR_MODE_KEY, colorMode.name)
        }

        Surface(
            Modifier
                .minHeight(100.vh)
                .overflowX(Overflow.Clip)
        ) {
            content()
        }
    }
}