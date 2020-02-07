package deepcopy

import java.lang.reflect.Array
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.HashMap

@Suppress("UNCHECKED_CAST")
class ObjectUtils {

    private val objectsStorage: MutableMap<String, Any> = HashMap()

    fun copy(initialObject: Any): Any? {
        val initialObjectId = Integer.toHexString(System.identityHashCode(initialObject))
        val initialClass = initialObject.javaClass

        if (objectsStorage.containsKey(initialObjectId)) {
            return objectsStorage[initialObjectId]
        }

        val resultObject = getNewInstance(initialClass)
        objectsStorage[initialObjectId] = resultObject
        for (field in initialClass.declaredFields) {
            if (Modifier.isStatic(field.modifiers)) {
                continue
            }

            field.isAccessible = true

            // TODO: проверить необходимость изменения модификатора final;
            //  в ubuntu тест проходил с этим ифом
            //  if (Modifier.isFinal(field.modifiers)) {
            //      val modifiers = Field::class.java.getDeclaredField("modifiers")
            //      modifiers.isAccessible = true
            //      modifiers.setInt(field, field.modifiers and Modifier.FINAL.inv() )
            //  }

            field.set(resultObject, copyField(field, initialObject))
        }

        return resultObject
    }

    /**
     * Инстанцирование объекта при отсутствии дефолтного конструктора.
     * Подсмотрено в http://objenesis.org/
     * Метод создаёт экземпляр объекта без вызова конструктора.
     */
    private fun <T> getNewInstance(type: Class<T>): T {
        val objectConstructor: Constructor<Any> = Any::class.java.getConstructor()
        val mungedConstructor: Constructor<T> = newConstructorForSerialization(type, objectConstructor)
        mungedConstructor.isAccessible = true
        return mungedConstructor.newInstance()
    }

    private fun <T> newConstructorForSerialization(type: Class<T>, constructor: Constructor<Any>): Constructor<T> {
        val reflectionFactoryClass = Class.forName("sun.reflect.ReflectionFactory")
        val reflectionFactory = reflectionFactoryClass
            .getDeclaredMethod("getReflectionFactory")
            .invoke(null)
        val newConstructorForSerialization = reflectionFactoryClass
            .getDeclaredMethod("newConstructorForSerialization", Class::class.java, Constructor::class.java)

        return newConstructorForSerialization.invoke(reflectionFactory, type, constructor) as Constructor<T>
    }

    private fun <T> copyField(field: Field, initialObject: Any): T? {
        val fieldClass = field.type
        val fieldToCopy = field.get(initialObject) as T ?: return null

        if (fieldClass.isArray) {
            return copyArrayField(field, fieldToCopy)
        }

        if (fieldClass.isPrimitive) {
            return fieldToCopy
        }

        return copy(fieldToCopy) as T
    }

    private fun <T> copyArrayField(field: Field, fieldToCopy: T): T {
        val fieldClass = field.type
        val arraySize = Array.getLength(fieldToCopy)
        val arrayFieldCopy = Array.newInstance(fieldClass.componentType, arraySize)
        for (i in 0 until arraySize) {
            Array.set(arrayFieldCopy, i, Array.get(fieldToCopy, i))
        }

        return arrayFieldCopy as T
    }
}