package util

fun convertKebabCaseToTitleCase(input: String): String {
    return input.split("-")
                .joinToString(" ") { it.capitalize() }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}