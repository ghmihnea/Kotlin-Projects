import kotlin.math.sign

class BigInt(value: String) : Comparable<BigInt> {

    private val digits: String  //we will store the number as strings
    private val sign: Int  //1 for positive value and -1 for a negative one

    init {
        require(value.matches(Regex("-?[0-9]+"))) { "Invalid number format: $value" }
        /*we require the numbers to be whether positive and negative and only character from 0 to 9
         */

        /*determine sign*/
        sign = when {
            value == "0" -> 0
            /*-> is essentialy saying that when value is 0 return 0*/
            value.startsWith("-") -> -1
            /*-> is saying that when we have a negative value return -1*/
            else -> 1
            /* this will return when we encounter a positive value*/
        }


        digits = value.trimStart('-').trimStart('0').ifEmpty { "0" }
        /*the first trimStart removes the leading minus signs(our class stores negative values separately) and the
        second trimStart will remove the leading zeros: for example -098 is being put as an input, the final result
        will be 98. if nothing is entered the default value 0 will be assigned*/
    }

    fun abs(): BigInt = if (sign < 0) -this else this
    /*if the sign is negative we negate it otherwise we encountered a positive value and can leave it as it is */

    operator fun unaryMinus(): BigInt = if (digits == "0") this //if the number is 0 we don t make any changes
    else BigInt((if (sign < 0) "" else "-") + digits)
    constructor(value: Int) : this(value.toString())
    constructor(value: Short) : this(value.toString())
    constructor(value: Byte) : this(value.toString())
    /*these constructor will turn any int short or byte value into strings*/

    operator fun plus(other: BigInt): BigInt {
        return if (sign == other.sign) {
            BigInt((if (sign < 0) "-" else "") + addStrings(digits, other.digits))
            /*if the 2 numbers have the same sign we will add the 2 strings and will add in front
            of the addition their sign
             */
        } else {
            if (abs() >= other.abs()) {
                BigInt((if (sign < 0) "-" else "") + subtractStrings(digits, other.digits))
                /*otherwise we compare their absolute values and substract the smallest number from the
                largest one and we add the sign of the dominant value in front
                 */
            } else {
                BigInt((if (other.sign < 0) "-" else "") + subtractStrings(other.digits, digits))
            }
        }
    }

    private fun addStrings(a: String, b: String) : String {
        val sb = StringBuilder()/*this will consist as the base for creating our string for the sum*/
        var carry = 0
        val x = a.reversed()
        val y = b.reversed()

        /*a and b have to be reversed such that their addition can start from
        the least significant bit(the units)
         */
        val maxLen = maxOf(x.length, y.length)

        for(i in 0 until maxLen) {
            val sum = (x.getOrNull(i)?.digitToInt() ?: 0) + (y.getOrNull(i)?.digitToInt() ?: 0) + carry
            sb.append(sum % 10)
            carry = sum / 10
        }
        if (carry > 0) sb.append(carry)
        return sb.reverse().toString()

    }
    private fun subtractStrings(a: String, b: String): String {
        val sb = StringBuilder()
        var borrow = 0

        /*reverse the strings to process from least significant digit*/
        val x = a.reversed()
        val y = b.reversed()

        /*ensuring both strings have the same length by padding with zeros*/
        val maxLen = maxOf(x.length, y.length)
        val paddedX = x.padEnd(maxLen, '0')
        val paddedY = y.padEnd(maxLen, '0')

        /*subtracting digit by digit, considering borrow*/
        for (i in 0 until maxLen) {
            var diff = (paddedX[i].toInt() - '0'.toInt()) - (paddedY[i].toInt() - '0'.toInt()) - borrow

            if (diff < 0) {
                diff += 10
                borrow = 1
            } else {
                borrow = 0
            }

            sb.append(diff)
        }

        /*removing leading zeros from the final result*/
        val result = sb.reverse().toString().trimStart('0')

        return if (result.isEmpty()) "0" else result
    }


    private fun multiplyStrings(a: String, b: String): String {
        val result = IntArray(a.length + b.length) { 0 }
        /*we create an array for the digits obtained in the multiplication*/

        for(i in a.indices.reversed())
        /*loop through each digit of 'a' from right to left*/
        {
            for(j in b.indices.reversed())
            /*loop through each digit of a from right to left*/
            {
                val mul = (a[i].digitToInt() * b[j].digitToInt()) + result[i + j + 1]
                /*multiply each digit and b add any previous carry(result[i + j + 1]) stored in the result array
                 */
                result[i + j + 1] = mul % 10
                /*storing the last digit of multiplication result*/
                result[i + j] += mul / 10
                /*here we store the the carry value from above*/

            }
        }
        return result.joinToString("").trimStart('0').ifEmpty {"0"}
        /*turning the vector into a string
        we trim the start if the 1st element is 0 and if the vector is empty
        the default value will be 0
         */
    }
    operator fun plus(other: Int): BigInt {
        return this + BigInt(other.toString())
        /*BigInt is a string so we have to convert other to string as well

         */
    }

    operator fun plus(other: Short): BigInt {
        return this + BigInt(other.toString())
    }

    operator fun plus(other: Byte): BigInt {
        return this + BigInt(other.toString())
    }

    operator fun minus(other: BigInt): BigInt {
        return this + (-other)
    }

    operator fun minus(other: Int): BigInt {
        return this - BigInt(other.toString())
    }

    operator fun minus(other: Short): BigInt {
        return this - BigInt(other.toString())
    }

    operator fun minus(other: Byte): BigInt {
        return this - BigInt(other.toString())
    }

    operator fun times(other: BigInt): BigInt {
        return BigInt((if (sign * other.sign < 0) "-" else "") + multiplyStrings(digits, other.digits))
    }

    operator fun times(other: Int): BigInt {
        val otherSign = if (other <  0) -1 else 1
        /*we store the sign of the other value*/
        return BigInt((if( sign * otherSign < 0) "-" else "") + multiplyStrings(digits, other.toString()))
        /*we implement our multiplyStrings function made for this situation*/

    }

    operator fun times(other: Short): BigInt {
        val otherSign = if (other < 0) -1 else 1
        return BigInt((if(sign *otherSign < 0) "-" else "") + multiplyStrings(digits, other.toString()))
    }

    operator fun times(other: Byte): BigInt {
        val otherSign = if(other < 0) -1 else 1
        return BigInt((if(sign * otherSign < 0) "-" else "") + multiplyStrings(digits, other.toString()))
    }

    operator fun div(other: BigInt): BigInt {
        require(other.digits != "0") { throw ArithmeticException("Division by zero") }
        /* we throw an arihtmetic exception if we are in the case of division by 0*/

        val quotient = BigInt((if (sign * other.sign < 0) "-" else "") + divideStrings(digits, other.digits).first)
        /*we make our quotient resulted as a bigint value , decide the sign in the if condition and place it
        front of our result of the division*/

        return quotient
    }

    operator fun div(other: Int): BigInt {
        require(other != 0) { throw ArithmeticException("Division by zero") }
        val otherSign = if(other < 0) -1 else 1
        /*we store the sign of the other value
         */
        return BigInt((if(sign * otherSign < 0) "-" else "") + divideStrings(digits, other.toString()).first)
        /*when implementing the divideStrings function we have to be careful to have the convesion to string for
        other which is currently type int
         */
    }

    operator fun div(other: Short): BigInt {
        require(other.toInt() != 0) { throw ArithmeticException("Division by zero") }
        val otherSign = if(other < 0) -1 else 1
        return BigInt((if(sign * otherSign < 0) "-" else "") + divideStrings(digits, other.toString()).first)
    }

    operator fun div(other: Byte): BigInt {
        require(other.toInt() != 0) { throw ArithmeticException("Division by zero") }
        val otherSign = if(other < 0) -1 else 1
        return BigInt((if(sign * otherSign < 0)"-" else "") + divideStrings(digits, other.toString()).first)
    }


    operator fun rem(other: BigInt): BigInt {
        require(other.digits != "0") { throw ArithmeticException("Division by zero") }

        val remainder = BigInt(divideStrings(digits, other.digits).second)
        /*we store the remainder as a BigInt
         */
        return if (remainder.digits == "0") remainder
        /*if the remainder is 0 we return it as it is

         */
        else BigInt((if (this.sign < 0) "-" else "") + remainder.digits)
        /*otherwise, we apply the correct sign based on 'this' and return the remainder

         */
    }




    operator fun rem(other: Int): BigInt {
        require(other != 0) { throw ArithmeticException("Division by zero") }
        return BigInt(divideStrings(digits, other.toString()).second)
        /*same reasoning as above but because other is of type int we convert it to other.toString() */

    }

    operator fun rem(other: Short): BigInt {
        require(other.toInt() != 0) { throw ArithmeticException("Division by zero") }
        return BigInt(divideStrings(digits, other.toString()).second)
    }

    operator fun rem(other: Byte): BigInt {
        require(other.toInt() != 0) { throw ArithmeticException("Division by zero") }
        return BigInt(divideStrings(digits, other.toString()).second)
    }

    fun pow(exp: BigInt): BigInt {
        require (exp >= 0) { "Exponent must be non negative" }
        var result = BigInt("1")
        /*initialising the result with 1 because any number at power of 0 will be 1
        this will also be stored as a base value that gets repeatedly multiplied
         */
        var base = this
        /*we store the value of 'this' in base*/
        var exponent = exp


        while(exponent > BigInt("0")) {
            result *= base
            /*this keeps multiplying the exponent by istelf
            while the exponent is not 0
             */
            exponent -= BigInt("1")
            /*decrementing the exponent */
        }

        return result
    }

    fun pow(exp: Int): BigInt {
        require (exp >= 0) { "Exponent must be non negative" }
        var result = BigInt("1")

        repeat(exp) { result *= this }
        return result
    }

    fun pow(exp: Short): BigInt {
        require(exp >= 0) { "Exponent must be non negative" }
        var result = BigInt("1")
        repeat(exp.toInt()) {result *= this }
        return result
    }

    fun pow(exp: Byte): BigInt {
        require(exp >= 0) { "Exponent must be non negative" }
        var result = BigInt("1")
        repeat(exp.toInt()) { result *= this }
        return result
    }

    fun sign(): Int {
        return sign
    }

    override fun toString(): String = (if(sign < 0)"-" else "") + digits

    override operator fun compareTo(other: BigInt): Int {
        if (sign != other.sign) return sign - other.sign
        /*if both variables have different signs return the comparison based on signs*/
        return if (sign > 0) {
            compareStrings(digits, other.digits)
            /*compare normally for positive numbers*/
        } else {
            compareStrings(other.digits, digits)
            /*reverse the order when comparing the negative values because with the minus sign,
            * the largest value is the one closer to 0*/
        }
    }

    operator fun compareTo(other: Int): Int {
        val otherBigInt = BigInt(other.toString())
        /*convert the Int 'other' to a BigInt by first converting it to a string*/
        return this.compareTo(otherBigInt)
        /*use the compareTo function for comparing 'this' BigInt to the other BigInt
         */
    }

    operator fun compareTo(other: Short): Int {
        val otherSign = if (other < 0) -1 else 1
        /*check the sign of 'other' (the Short value) to know if it's positive or negative*/

        return if (sign > 0) return digits.compareTo(other.toString())
        /*if 'this' BigInt is positive, compare its 'digits' with the string representation of 'other'*/

        else return other.toString().compareTo(digits)
        /*if 'this' BigInt is negative, reverse the order and compare the string representation of 'other' with 'digits'*/
    }

    operator fun compareTo(other: Byte): Int {
        val otherSign = if (other < 0) -1 else 1
        /*determine the sign of the other variable*/

        return if (sign > 0) {
            digits.compareTo(other.toString())
            /*if the numbers are positive we compare digits to the other, without
            forgetting to convert the byte variable into string
             */
        } else {
            other.toString().compareTo(digits)
            /*for negative value is the other way around*/
        }
    }

    override fun equals(other: Any?): Boolean = other is BigInt && digits == other.digits && sign == other.sign

    override fun hashCode(): Int {
        return digits.hashCode() * sign
        /*the hashCode function generates a numeric representation of the object.
        by default, kotlin provides a hashCode() implementation for basic types,
        but here we override it for the BigInt class.
         */
    }
    private fun divideStrings(a: String, b: String): Pair<String, String> {
        if(b == "0") throw ArithmeticException("Division by zero")

        var remainder = ""
        /*this will store the remainder during the division*/
        var quotient = StringBuilder()
        /*this will be used to builf the qutient result*/

        for(digit in a) {
            remainder += digit
            /*we add the current digit to the remainder string from left to right*/
            remainder = remainder.trimStart('0')
            /*removing any leading zeros in the remainder*/
            var count = 0
            /*in here we will store how many times b can be substracted
            from the remainder
             */

            while (compareStrings(remainder, b) >= 0)
            /*while remainder is greater or equal to b, keep substracting b from remainder */
            {
                val newRemainder = subtractStrings(remainder, b)
                /*substract b from remainder*/
                if(newRemainder == remainder) break
                /*if there is no change we break the loop*/
                remainder = newRemainder
                /*update the remainder*/
                count ++
                /*increase the count as we ve succesfully substracted b*/
            }
            quotient.append(count)
            /*add the current count to quotient*/
        }

        return Pair(quotient.toString().trimStart('0').ifEmpty{ "0" }, remainder.ifEmpty {"0"})
        /*we return the quotient and remainder pair, we trim the leading 0 s and return "0" if empty*/
    }

}
private fun compareStrings(a: String, b: String): Int {
    if(a.length > b.length)
    /*we compare the lengths of the 2 strings*/
    {
        return 1
        /*if a is longer han b, a is considered larger, so we return 1*/
    } else if(a.length < b.length) {
        return -1
        /*if a is shorter than b, a is smaller, so return -1*/
    } else {
        return a.compareTo(b)
        /*if both strings are of the same length, we compare them lexicographically*/
    }
}

