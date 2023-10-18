package ch.usi.si.seart.model.code;

/**
 * @see Comparable#compareTo(Object)
 * @see Object#clone()
 * @see Object#equals(Object)
 * @see Object#hashCode()
 * @see Object#toString()
 * @see
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Serializable.html">
 *     Interface Serializable
 * </a>
 * @see
 * <a href="https://docs.python.org/3/reference/datamodel.html">
 *     The Python Language Reference - Data model
 * </a>
 */
public enum Boilerplate {

    /**
     * Methods that create new class instances.
     */
    CONSTRUCTOR,
    /**
     * Methods that initialize member values of new class instances.
     */
    INITIALIZER,
    /**
     * Methods that retrieve instance member values.
     */
    GETTER,
    /**
     * Methods that mutate instance member values.
     */
    SETTER,
    /**
     * Methods that <em>print</em> the instance out as a string.
     */
    STRING_CONVERSION,
    /**
     * Methods that return a string <em>representation</em> of the instance.
     */
    STRING_REPRESENTATION,
    /**
     * Includes implementations of methods that
     * return a hash value for an object instance.
     */
    HASHER,
    /**
     * Includes implementations of instance comparison operations:
     * <ul>
     *     <li>Equality</li>
     *     <li>Less-than</li>
     *     <li>Greater-than</li>
     *     <li>Less-than-or-equal</li>
     *     <li>Greater-than-or-equal</li>
     * </ul>
     * for arbitrary types.
     */
    COMPARISON,
    /**
     * Includes implementations of instance:
     * <ul>
     *     <li>Round</li>
     *     <li>Floor</li>
     *     <li>Ceiling</li>
     *     <li>Truncation</li>
     *     <li>Negative</li>
     *     <li>Positive</li>
     *     <li>Absolute</li>
     *     <li>Invert</li>
     * </ul>
     * for arbitrary types.
     */
    UNARY_ARITHMETIC,
    /**
     * Includes implementations of instance:
     * <ul>
     *     <li>Addition</li>
     *     <li>Subtraction</li>
     *     <li>Multiplication</li>
     *     <li>Division</li>
     *     <li>Modulo</li>
     *     <li>Power</li>
     *     <li>Shift</li>
     *     <li>And</li>
     *     <li>Xor</li>
     *     <li>Or</li>
     * </ul>
     * for arbitrary types.
     */
    BINARY_ARITHMETIC,
    /**
     * Includes implementations of the following assignment operations:
     * <ul>
     *     <li>{@code +=}</li>
     *     <li>{@code -=}</li>
     *     <li>{@code *=}</li>
     *     <li>{@code @=}</li>
     *     <li>{@code /=}</li>
     *     <li>{@code //=}</li>
     *     <li>{@code %=}</li>
     *     <li>{@code **=}</li>
     *     <li>{@code <<=}</li>
     *     <li>{@code >>=}</li>
     *     <li>{@code &=}</li>
     *     <li>{@code ^=}</li>
     *     <li>{@code |=}</li>
     * </ul>
     * for any arbitrary type.
     */
    AUGMENTED_ASSIGNMENT,
    /**
     * Methods that return copies of the original instance.
     */
    CLONER,
    /**
     * Methods called prior to instance garbage collection.
     */
    FINALIZER,
    /**
     * Methods that convert the instance into a storable format.
     */
    SERIALIZER,
    /**
     * Methods that reverse serialization.
     */
    DESERIALIZER
}
