package com.rodrigodecastro.dentalflow.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation


fun mask(mask: String, text: String): String {
    val newText = StringBuilder()
    var maskIndex = 0
    var textIndex = 0
    while (textIndex < text.length && maskIndex < mask.length) {
        if (mask[maskIndex] == '#') {
            newText.append(text[textIndex])
            textIndex++
        } else {
            newText.append(mask[maskIndex])
        }
        maskIndex++
    }
    return newText.toString()
}

class MaskVisualTransformation(private val mask: String) : VisualTransformation {

    private val maxLength = mask.count { it == '#' }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = AnnotatedString.Builder().run {
            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != '#') {
                    val nextDigitIndex = mask.indexOf('#', maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
            toAnnotatedString()
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var noneDigitCount = 0
                var i = 0
                while (i < offset + noneDigitCount) {
                    if (mask[i++] != '#') {
                        noneDigitCount++
                    }
                }
                return offset + noneDigitCount
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset - mask.take(offset).count { it != '#' }
            }
        }

        return TransformedText(annotatedString, offsetMapping)
    }
}


class PhoneVisualTransformation : VisualTransformation by MaskVisualTransformation("(##) #####-####")
class DateVisualTransformation : VisualTransformation by MaskVisualTransformation("##/##/####")
class TimeVisualTransformation : VisualTransformation by MaskVisualTransformation("##:##")

