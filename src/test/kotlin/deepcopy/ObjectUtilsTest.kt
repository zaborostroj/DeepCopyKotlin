package deepcopy

import objects.ClassA
import objects.ClassB
import objects.ClassC
import objects.ClassD
import objects.ObjectWithArray
import objects.ObjectWithFinal
import objects.ObjectWithSimpleFields
import objects.ObjectWithStaticField
import org.junit.Test

internal class ObjectUtilsTest {

    @Test
    fun `copy object with simple fields`() {
        val objectUtils = ObjectUtils()

        val input = ObjectWithSimpleFields()

        val output = objectUtils.copy(input) as ObjectWithSimpleFields

        input.int = 43
        input.long = 43
        input.float = 43.0f
        input.double = 43.0
        input.char = '3'
        input.string = "43"
        input.boolean = false

        assert(output.int == 42)
        assert(output.long == 42L)
        assert(output.float == 42.0F)
        assert(output.double == 42.0)
        assert(output.char == '2')
        assert(output.string == "42")
        assert(output.boolean)
    }

    @Test
    fun `copy object with inner objects and self link`() {
        val objectUtils = ObjectUtils()

        val instanceOfD1 = ClassD(42)
        val instanceOfD2 = ClassD(43)
        val instanceOfC = ClassC(instanceOfD1)
        val instanceOfB = ClassB()
        val inputA = ClassA(instanceOfB, instanceOfC, instanceOfD2)
        instanceOfB.classD = instanceOfD1
        instanceOfB.classA = inputA

        val outputA = objectUtils.copy(inputA) as ClassA

        assert(getHexCode(inputA.classB) != getHexCode(outputA.classB))
        assert(getHexCode(inputA.classC) != getHexCode(outputA.classB))
        assert(getHexCode(inputA.classD) != getHexCode(outputA.classD))
        assert(getHexCode(inputA.classB.classD) != getHexCode(outputA.classB.classD))

        // ссылки на разные вложенные объекты одного типа
        assert(getHexCode(outputA.classB.classD) != getHexCode(outputA.classD))
        assert(getHexCode(outputA.classB.classD) == getHexCode(outputA.classC.classD))

        // обратная ссылка из вложенного объекта на внешний
        assert(getHexCode(outputA) == getHexCode(outputA.classB.classA))
    }

    private fun getHexCode(a: Any?): String {
        return Integer.toHexString(System.identityHashCode(a))
    }

    @Test
    fun `copy object with static field`() {
        val objectUtils = ObjectUtils()

        val input = ObjectWithStaticField(42)

        val output = objectUtils.copy(input) as ObjectWithStaticField

        input.int = 43

        assert(output.int == 42)
    }

    @Test
    fun `copy object with array`() {
        val objectUtils = ObjectUtils()

        val input = ObjectWithArray(intArrayOf(42, 43))

        val output = objectUtils.copy(input) as ObjectWithArray

        input.array = intArrayOf(44, 45)

        assert(output.array[0] == 42)
        assert(output.array[1] == 43)
    }

    @Test
    fun `copy object with final field`() {
        val objectUtils = ObjectUtils()

        val input = ObjectWithFinal(ClassD(42))

        val output = objectUtils.copy(input) as ObjectWithFinal

        assert(getHexCode(input.final) != getHexCode(output.final))
    }
}