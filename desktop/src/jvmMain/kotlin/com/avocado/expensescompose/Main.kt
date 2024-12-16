import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.material.*
import com.avocado.expenses.shared.Greeting


// TODO migrate the app to be usable in desktop environments, maybe Ill be also add the web version.
fun main() {
  application {
    val windowState = rememberWindowState()

    Window(
      onCloseRequest = ::exitApplication,
      state = windowState,
      title = "My Project"
    ) {
      Surface(modifier = Modifier.fillMaxSize()) {
        Text(text = "Welcome to my Project " + Greeting().greet())
      }
    }
  }
}