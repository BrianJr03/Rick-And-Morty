package jr.brian.rickandmortyrest.view.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable

@Composable
fun CustomDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {},
            text = {
                content()
            }
        )
    }
}