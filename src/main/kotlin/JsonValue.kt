

sealed interface JsonValue {
    data class Number(val value: Int, val frac: List<Int>, val exponent: Int) : JsonValue
    data class Boolean(val value: kotlin.Boolean) : JsonValue
    data class String(val value: kotlin.String) : JsonValue
    data class Array(val values: List<JsonValue>) : JsonValue
    data class Object(val values: Map<kotlin.String, JsonValue>) : JsonValue
    data object Null : JsonValue
}