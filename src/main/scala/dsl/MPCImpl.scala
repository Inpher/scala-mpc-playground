package dsl

import mpc._

/**
 * And here is an implementation in the MPC world, where all computations are done
 * using MPC primitives. Note that there is no clear distinction between the offline and online worlds:
 * we have a direct/shallow embedding here. To properly distinguish between these phases we would
 * need a more involved compiler pass (leading to the xor compiler).
 */
trait MPCImpl extends DSL with MPC {
  type Long = SharedNum
  type Vector = Array[Array[SecretNum]] // is better modelled be Shared[Array[Long]]

  def long2PublicNum(l: scala.Long): PublicNum

  /**
   * We should be able to specify which player this long comes from
   */
  def unit(l: scala.Long): Long = {
    val zeroes = createZeroSumShares
    add(zeroes, shareConst(long2PublicNum(l)))
  }

  def vec2XorVec(v: Array[scala.Long]): Vector = {
    val sharesPerElem = v.map(vi => createZeroSumShares)
    val res = Array.ofDim[Array[SecretNum]](numPlayers)
    //initialize vectors
    (0 until numPlayers).foreach { i =>
        res(i) = Array.ofDim[SecretNum](v.size)
    }

    //fill with secret shares
    (0 until v.size).foreach { i =>
        val vi = v(i)
        val sharedScalar = unit(vi)
        (0 until numPlayers).foreach { n =>
            res(i)(n) = sharedScalar(n)
        }
    }
    res
  }

  def addXl(x: Long, y: Long): Long = add(x, y)
  def addXv(x: Vector, y: Vector): Vector =
    x.zip(y).map { case (x, y) => add(x, y) }

  def mulXl(x: Long, y: Long): Long = mul(x, y)

  def hadamardProduct(x: Vector, y: Vector): Vector = x.zip(y).map {
    case (x, y) => mul(x, y)
  }

  def dotProduct(x: Vector, y: Vector): Long = {
    hadamardProduct(x, y).foldLeft[Long](createZeroSumShares) {
      case (sum, elem) => add(sum, elem)
    }
  }
}
