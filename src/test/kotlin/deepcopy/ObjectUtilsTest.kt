package deepcopy

import objects.ObjectWithSimpleFields
import org.junit.Test

internal class ObjectUtilsTest {

    @Test
    fun simpleFieldsCopy() {
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
}