package manga

import GenericUi
import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skija.Image
import java.net.URL

object MangaUi : GenericUi {
    override fun playUi(list: List<String>, title: String, theme: MutableState<Colors>) = Window(title = title) {
        var alpha by remember { mutableStateOf(1f) }
        var pageList by remember { mutableStateOf(emptyList<ImageBitmap>()) }
        MaterialTheme(colors = theme.value) {
            CircularProgressIndicator(Modifier.alpha(alpha))
            LazyColumnFor(pageList, modifier = Modifier.fillMaxHeight().fillMaxWidth().background(theme.value.background)) {
                Image(bitmap = it)
                println(it)
            }
        }

        GlobalScope.launch {
            val pages = withContext(Dispatchers.Default) {
                list
                    .also { println(it) }
                    .map { Image.makeFromEncoded(URL(it).openConnection().getInputStream().readAllBytes()).asImageBitmap() }
            }
            pageList = pages
            alpha = 0f
        }
    }
}

