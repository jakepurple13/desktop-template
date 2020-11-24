@file:Suppress("FunctionName")

import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.ExperimentalKeyInput
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.shortcuts
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import manga.Sources
import java.net.URL

val currentTheme = darkColors()

@ExperimentalKeyInput
@ExperimentalMaterialApi
fun main() = Window(title = "Otaku Viewer") {
    MaterialTheme(colors = currentTheme) {
        Row(Modifier.background(currentTheme.background)) {
            Button(onClick = { uiViewer(Sources.NINE_ANIME) }) { Text("Manga") }
            Button(onClick = { uiViewer(anime.Sources.GOGOANIME) }) { Text("Anime") }
        }
    }
}

@ExperimentalKeyInput
@ExperimentalMaterialApi
fun uiViewer(info: GenericInfo) = Window(title = "NineAnime Viewer", centered = true) {
    var textValue by remember { mutableStateOf(TextFieldValue("")) }
    var page = 1
    var progressAlpha by remember { mutableStateOf(1f) }
    var currentList by remember { mutableStateOf(info.getItems(page).toMutableList()) }
    MaterialTheme(colors = currentTheme) {
        Column(Modifier.background(currentTheme.background)) {
            TextField(
                value = textValue,
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                // Update value of textValue with the latest value of the text field
                onValueChange = { textValue = it },
                trailingIcon = { Icon(Icons.Filled.Search) },
                label = { Text("${currentList.size} Search") },
                singleLine = true,
                placeholder = { Text("Search") }
            )
            CircularProgressIndicator(Modifier.alpha(progressAlpha))
            val state = rememberLazyListState()
            progressAlpha = 0f
            LazyColumnForIndexed(
                currentList,
                state = state,
                modifier = Modifier
                    .fillMaxHeight()
                    .shortcuts {
                        on(Key.Spacebar) { println("Hello there!!!") }
                    }
            ) { index, item ->
                if (currentList.lastIndex == index) {
                    onActive {
                        //fetch more items here
                        progressAlpha = 1f
                        currentList = currentList.apply { addAll(Sources.NINE_ANIME.getManga(++page)) }
                        println("New items - $page - ${currentList.size}")
                        progressAlpha = 0f
                    }
                }
                MangaItem(item)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MangaItem(item: GenericData) {
    Card(
        shape = RoundedCornerShape(4.dp),
        border = cardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { GlobalScope.launch { item.listOfData?.let { MangaInfo(it) } } }
    ) {
        Text(
            item.title,
            style = MaterialTheme
                .typography
                .h6
                .copy(textAlign = TextAlign.Center),
            modifier = Modifier.padding(16.dp)
        )
    }
}

const val WIDTH_DEFAULT = 360 / 2
const val HEIGHT_DEFAULT = 480 / 2

@ExperimentalMaterialApi
fun MangaInfo(item: GenericInformation) = GlobalScope.launch {
    Window(title = item.title) {
        MaterialTheme(colors = currentTheme) {
            Box {
                Column(Modifier.background(currentTheme.background)) {
                    MangaTitleArea(item)
                    MangaChapterRows(item)
                }
            }
        }
    }
}

@Composable
fun MangaTitleArea(item: GenericInformation) = Card(modifier = Modifier.padding(5.dp), border = cardBorder()) {
    Row(modifier = Modifier.padding(5.dp)) {
        Image(
            bitmap = org.jetbrains.skija.Image.makeFromEncoded(
                URL(item.imageUrl).openConnection().getInputStream().readAllBytes()
            ).asImageBitmap(),
            modifier = Modifier
                .size(WIDTH_DEFAULT.dp, HEIGHT_DEFAULT.dp)
                .border(BorderStroke(1.dp, currentTheme.background), shape = RoundedCornerShape(5.dp))
        )
        Column(modifier = Modifier.padding(5.dp).height(HEIGHT_DEFAULT.dp)) {
            Text(
                item.title, style = MaterialTheme
                    .typography
                    .h3
                    .copy(textAlign = TextAlign.Center)
            )
            ScrollableRow(modifier = Modifier.padding(5.dp)) {
                item.genres.forEach { Text(it, modifier = Modifier.padding(5.dp), style = MaterialTheme.typography.subtitle2) }
            }
            ScrollableColumn { Text(item.description.orEmpty(), style = MaterialTheme.typography.body1) }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MangaChapterRows(item: GenericInformation) = LazyColumnFor(item.rowData(), modifier = Modifier.fillMaxHeight()) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable { MangaReader(it.getItem(), it.name) },
        border = cardBorder()
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Text(it.name)
            Text(it.uploaded, modifier = Modifier.align(Alignment.End))
        }
    }
}

fun MangaReader(model: List<String>, title: String) = Window(title = title) {
    var alpha by remember { mutableStateOf(1f) }
    var pageList by remember { mutableStateOf(emptyList<ImageBitmap>()) }
    MaterialTheme {
        CircularProgressIndicator(Modifier.alpha(alpha))
        LazyColumnFor(pageList, modifier = Modifier.fillMaxHeight().background(currentTheme.background)) {
            Image(bitmap = it)
            println(it)
        }
    }

    GlobalScope.launch {
        val pages = withContext(Dispatchers.Default) {
            model
                .also { println(it) }
                .map { org.jetbrains.skija.Image.makeFromEncoded(URL(it).openConnection().getInputStream().readAllBytes()).asImageBitmap() }
        }
        pageList = pages
        alpha = 0f
    }
}

fun cardBorder() = BorderStroke(1.dp, currentTheme.onBackground)
