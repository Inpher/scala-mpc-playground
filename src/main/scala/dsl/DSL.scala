package dsl

/**
 * We create a small language with scalars and vectors, containing
 * three simple ops, addition, multiplication and dot product
 */
trait DSL {
  type Vector
  type Long

  /**
   * We need an entry point into the DSL world from the normal world
   */
  def unit(l: scala.Long): Long
  def vec2XorVec(v: Array[scala.Long]): Vector

  def addXl(x: Long, y: Long): Long
  def addXv(x: Vector, y: Vector): Vector

  def mulXl(x: Long, y: Long): Long
  def hadamardProduct(x: Vector, y: Vector): Vector
  def dotProduct(x: Vector, y: Vector): Long

  implicit class LongOps(x: Long) {
    def + (y: Long): Long = addXl(x, y)
    def * (y: Long): Long = mulXl(x, y)
  }

  implicit class VectorOps(x: Vector) {
    def + (y: Vector): Vector = addXv(x, y)
    def * (y: Vector): Vector = hadamardProduct(x, y)
    def dotP (y: Vector): Long = dotProduct(x, y)
  }
}
