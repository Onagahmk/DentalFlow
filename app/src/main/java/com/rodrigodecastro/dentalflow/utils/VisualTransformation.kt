package com.rodrigodecastro.dentalflow.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Uma `VisualTransformation` genérica que aplica uma máscara a um texto.
 * A máscara é definida por um padrão onde '#' representa um caractere a ser digitado.
 * Ex: "(##) #####-####"
 */
class MaskVisualTransformation(private val mask: String) : VisualTransformation {

    // O comprimento máximo do texto é o número de '#' na máscara.
    private val maxLength = mask.count { it == '#' }

    override fun filter(text: AnnotatedString): TransformedText {
        // Garante que o texto não exceda o comprimento máximo permitido pela máscara.
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = AnnotatedString.Builder().run {
            var maskIndex = 0
            var textIndex = 0
            // Itera sobre o texto e a máscara para construir a string formatada.
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != '#') {
                    // Se o caractere da máscara não for '#', adiciona o caractere da máscara (ex: '(', ')', '-')
                    // e avança o índice da máscara até o próximo '#'.
                    val nextDigitIndex = mask.indexOf('#', maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                // Adiciona o caractere digitado pelo usuário.
                append(trimmed[textIndex++])
                maskIndex++
            }
            toAnnotatedString()
        }

        // O `OffsetMapping` é crucial para mapear corretamente a posição do cursor
        // entre o texto original (só números) e o texto transformado (com a máscara).
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

/**
 * Uma implementação específica de `VisualTransformation` para formatar números de telefone no padrão brasileiro.
 * Esta classe simplesmente delega a lógica para a `MaskVisualTransformation` genérica, 
 * passando a máscara "(##) #####-####" como parâmetro.
 */
class PhoneVisualTransformation : VisualTransformation by MaskVisualTransformation("(##) #####-####")
