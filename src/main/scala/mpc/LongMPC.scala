package mpc

import scala.math.Numeric._

/**
 * An instantiation of MPC, where both the public and secret number types are fixed
 * to `scala.Long`.
 */
trait LongMPC extends MPC {
  type PublicNum = scala.Long
  type SecretNum = scala.Long

  val rand = scala.util.Random
  def randGen = new {
    def next(): Long = rand.nextLong()
  }

  def p2s(p: Long): Long = p

  def reveal(secrets: Array[Long]) = secrets.sum
}

/**
 * And an actual instance, runnable etc.
 */
object LongMPCImpl extends LongMPC {

  implicit val publicNumNumeric = implicitly[Numeric[scala.Long]]
  implicit val secretNumNumeric = implicitly[Numeric[scala.Long]]
  implicit val secretNumManifest = implicitly[Manifest[scala.Long]]

  def numPlayers = 3

  def main(args: Array[String]) {
    val secretData: Array[Long] = Array(1L,2L,0L)

    println("Sum in public")
    println(secretData.sum)

    val maskedData = secretData.map(x => p2s(x))

    //offlinePhase
    val shares1 = createZeroSumShares
    println(shares1.toList)
    val shares2 = createZeroSumShares
    println(shares2.toList)
    val shares3 = createZeroSumShares
    println(shares3.toList)

    val secretSum = add(add(add(maskedData, shares1), shares2), shares3)

    println("Sum in private")
    println(reveal(secretSum))

    //multiplication
    val secretDataX: Array[Long] = Array(24, 0, 0)
    val secretDataY: Array[Long] = Array(0, 42, 0)
    println("product in public")
    println(24L * 42L)

    //offline phase
    val sharesX = add(createZeroSumShares, secretDataX)
    println(sharesX.toList)
    val sharesY = add(createZeroSumShares, secretDataY)
    println(sharesY.toList)

    val prod = mul(sharesX, sharesY)

    println("product in secret")
    println(reveal(prod))

  }
}
