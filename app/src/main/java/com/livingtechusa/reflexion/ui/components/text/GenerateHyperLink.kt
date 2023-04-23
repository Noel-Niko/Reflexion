package com.livingtechusa.reflexion.ui.components.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun GenerateHyperlink(url: String, label: String): AnnotatedString {
    val annotatedText = buildAnnotatedString {
        append(label)
        addStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline
            ),
            start = 0,
            end = label.length
        )
    }
    return annotatedText
}

/*
Use:
Column {
        Text(
            text = annotatedText,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.clickable(onClick = {
                val downloadIntent = Intent(Intent.ACTION_VIEW)
                downloadIntent.data = Uri.parse(url)
                startActivity(context ,downloadIntent, null)
            })
        )
    }
 */