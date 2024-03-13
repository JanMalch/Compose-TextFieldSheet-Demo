package io.github.janmalch.textfieldsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextFieldSheetContent(
    textValue: String,
    placeholder: String,
    buttonText: String,
    onValueChange: (String) -> Unit,
    onTextValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .navigationBarsPadding() // TODO: overlap still broken
    ) {

        SimpleTextField(
            value = textValue,
            onValueChange = onTextValueChange,
            placeholder = placeholder,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )

        TextButton(
            modifier = Modifier.align(Alignment.End),
            enabled = textValue.isNotBlank(),
            onClick = {
                focusRequester.freeFocus()
                onValueChange(textValue)
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onDismissRequest()
                    }
                }
            }
        ) {
            Text(text = buttonText)
        }

        LaunchedEffect(Unit) {
            focusManager.clearFocus(force = true)
            focusRequester.requestFocus()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldSheet(
    value: String,
    placeholder: String,
    buttonText: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var textValue by remember {
        mutableStateOf(value)
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        windowInsets = WindowInsets.ime // TODO: overlap still broken
    ) {

        TextFieldSheetContent(
            textValue = textValue,
            placeholder = placeholder,
            buttonText = buttonText,
            onValueChange = onValueChange,
            onTextValueChange = { textValue = it },
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldSheet(
    value: String,
    placeholder: String,
    buttonText: String,
    discardContent: @Composable (TextFieldSheetDirtyState.() -> Unit),
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var textValue by remember {
        mutableStateOf(value)
    }
    // TODO: something's wonky here..?
    val dirtyState = remember(onDismissRequest) {
        TextFieldSheetDirtyState(onDismissRequest = onDismissRequest)
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            val isDirty = textValue != value
            if (isDirty) {
                dirtyState.isVisible = true
            } else {
                onDismissRequest()
            }
        },
        windowInsets = WindowInsets.ime // TODO: overlap still broken
    ) {
        TextFieldSheetContent(
            textValue = textValue,
            placeholder = placeholder,
            buttonText = buttonText,
            onValueChange = onValueChange,
            onTextValueChange = { textValue = it },
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        )
    }

    if (dirtyState.isVisible) {
        dirtyState.discardContent()
    }
}

@Stable
class TextFieldSheetDirtyState(private val onDismissRequest: () -> Unit) {

    internal var isVisible by mutableStateOf(false)

    fun cancelDiscardingSheet() {
        isVisible = false
    }

    fun discardSheetChanges() {
        onDismissRequest()
        isVisible = false
    }
}

@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
) {
    val textColor = LocalTextStyle.current.color.takeOrElse {
        LocalContentColor.current
    }
    Box {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(color = textColor),
            cursorBrush = SolidColor(textColor),
            singleLine = singleLine,
            modifier = modifier,
        )
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}


