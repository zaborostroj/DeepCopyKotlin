package objects

data class ObjectWithSimpleFields(
    var int: Int = 42,
    var long: Long = 42,
    var float: Float = 42.0f,
    var double: Double = 42.0,

    var char: Char = '2',
    var string: String = "42",

    var boolean: Boolean = true
)