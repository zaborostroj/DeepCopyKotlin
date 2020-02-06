package objects

data class ClassA(val classB: ClassB, val classC: ClassC, val classD: ClassD)

data class ClassB (val classA: ClassA, val classD: ClassD)

data class ClassC (val classD: ClassD)

data class ClassD (val anInt: Int = 42)