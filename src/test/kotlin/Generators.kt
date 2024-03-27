import io.kotest.property.Arb
import io.kotest.property.arbitrary.*


internal val genNull: Arb<JsonValue.Null> =
    Arb.constant(JsonValue.Null)

internal val genBool: Arb<JsonValue.Boolean> =
    Arb.boolean().map(JsonValue::Boolean)

internal val genNumber: Arb<JsonValue.Number> =
    arbitrary {
        JsonValue.Number(
            value = Arb.int().bind(),
            frac = Arb.list(Arb.int()).bind(),
            exponent = Arb.int().bind()
        )
    }

internal val genString: Arb<JsonValue.String> =
    Arb.string().map(JsonValue::String)

internal val genArray: Arb<JsonValue.Array> =
    Arb.list(Arb.choice(generators())).map(JsonValue::Array)

internal val genObject: Arb<JsonValue.Object> =
    Arb.map(
        keyArb = Arb.string(),
        valueArb = Arb.choice(generators())
    ).map(JsonValue::Object)


internal fun generators(): List<Arb<JsonValue>> = listOf(
    genNull,
    genBool,
    genNumber,
    genString,
    genArray,
    genObject
)

