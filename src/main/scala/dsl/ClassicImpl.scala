package dsl

import scala.math.Numeric._

/**
 * A classic, plaintext implementation of the DSL
 */
trait ClassicImpl extends DSL {
    type Vector = Array[scala.Long]
    type Long = scala.Long

    def unit(l: scala.Long): Long = l
    def vec2XorVec(v: Array[scala.Long]): Vector = v

    def addXl(x: Long, y: Long): Long = x + y
    def addXv(x: Vector, y: Vector): Vector = x.zip(y).map {
        case (x, y) => x + y
    }

    def mulXl(x: Long, y: Long): Long = x * y
    def hadamardProduct(x: Vector, y: Vector): Vector = x.zip(y).map {
        case (x, y) => x * y
    }
    def dotProduct(x: Vector, y: Vector): Long = hadamardProduct(x, y).sum
}
