package io.github.opletter.courseevals.site

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.overflowX
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.theme.colors.getColorMode
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.vh

private const val COLOR_MODE_KEY = "course-evals:colorMode"

@InitKobweb
fun initKobweb(ctx: InitKobwebContext) {
    val parentPaths = listOf("fsu", "rutgers", "course-evals")
    ctx.router.addRouteInterceptor {
        val a = path.removeSuffix("/")
        if (parentPaths.any { a.endsWith(it) }) {
            if (!path.endsWith("/")) path += "/"
        } else if (path.endsWith("/"))
            path = path.removeSuffix("/")
    }
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
