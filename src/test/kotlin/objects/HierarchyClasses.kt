package objects

data class ClassA(
    var classB: ClassB,
    var classC: ClassC,
    var classD: ClassD
)

data class ClassB (
    var classA: ClassA? = null,
    var classD: ClassD? = null
)

data class ClassC (var classD: ClassD)

data class ClassD (var anInt: Int = 42)