import arrow.core.curried

sealed interface ParseResult<out I ,out O> {
    data object Done : ParseResult<Nothing, Nothing>
    data class More<I, O>(val rest: I, val parsed: O) : ParseResult<I, O>
}

fun interface Parser<I, O> {
    infix fun runParser(input: I): ParseResult<I, O>

    companion object
}

fun <I, A> Parser.Companion.pure(value: A): Parser<I, A> = Parser {
    ParseResult.More(it, value)
}

fun <I, A> Parser.Companion.empty(): Parser<I, A> = Parser { ParseResult.Done }

infix fun <I, A, B> Parser<I, A>.map(fn: (A) -> B): Parser<I, B> = Parser { input ->
    when(val r = runParser(input)) {
        is ParseResult.Done -> ParseResult.Done
        is ParseResult.More -> ParseResult.More(r.rest, fn(r.parsed))
    }
}

infix fun <I, A, B> Parser<I, A>.flatMap(fn: (A) -> Parser<I, B>): Parser<I, B> = Parser { input ->
    when(val r = runParser(input)) {
        is ParseResult.Done -> ParseResult.Done
        is ParseResult.More -> fn(r.parsed).runParser(r.rest)
    }
}

infix fun <I, A, B> Parser<I, (A) -> B>.ap(other: Parser<I, A>): Parser<I, B> = Parser { input ->
    when(val r = runParser(input)) {
        is ParseResult.Done -> ParseResult.Done
        is ParseResult.More -> {
            val (rest, f) = r

            (other map f) runParser rest
        }
    }
}

infix fun <I, A> Parser<I, A>.orElse(other: Parser<I, A>): Parser<I, A> = Parser { input ->
    when(val r = runParser(input)) {
        ParseResult.Done -> other.runParser(input)
        is ParseResult.More -> r
    }
}

fun char(char: Char): Parser<String, Char> =
    satisfy(char::equals)

fun satisfy(test: (Char) -> Boolean): Parser<String, Char> = Parser { input ->
    when {
        input.isNotEmpty() && test(input.first()) ->
            ParseResult.More(input.drop(1), input.first())
        else ->
            ParseResult.Done
    }
}

val digit: Parser<String, Int> =
    satisfy(Char::isDigit) map Char::code

fun string(str: String): Parser<String, String> =
    when(str) {
        "" -> Parser.pure("")
        else -> {
            val curried = { c: Char, cs: String -> c + cs }.curried()
            (char(str.first()) map curried) ap string(str.drop(1))
        }
    }


val nullParser: Parser<String, JsonValue.Null> =
    string("null") map { JsonValue.Null }

val boolParser: Parser<String, JsonValue.Boolean> =
    string("true").map { JsonValue.Boolean(true) } orElse
    string("false").map { JsonValue.Boolean(false) }

fun xx(): Parser<String, String>  = Parser { input ->
    val anyChar = satisfy { it.isLetterOrDigit() }

    when(val r = anyChar.runParser(input)) {
        is ParseResult.Done -> ParseResult.Done
        is ParseResult.More -> {
            val (rest, f)  = r
            if (f == '"') {
                ParseResult.More(rest, "")
            } else {
                when (val rr = xx().runParser(input.drop(1))) {
                    ParseResult.Done -> ParseResult.Done
                    is ParseResult.More -> ParseResult.More(rr.rest, f + rr.parsed)
                }

            }
        }
    }
}

val stringParser: Parser<String, JsonValue.String> =
    string("\"") flatMap { xx() } map JsonValue::String