package dsl

import mpc._
import scala.math.Numeric._

object DSLTest {

  implicit val pLNumeric  = implicitly[Numeric[scala.Long]]
  implicit val sLNumeric  = implicitly[Numeric[scala.Long]]
  implicit val sLManifest = implicitly[Manifest[scala.Long]]

  val plainDSL = new ClassicImpl { }
  val mpcDSL = new MPCImpl with LongMPC {
    implicit val publicNumNumeric: Numeric[scala.Long] = pLNumeric
    implicit val secretNumNumeric: Numeric[scala.Long] = sLNumeric
    implicit val secretNumManifest: Manifest[scala.Long] = sLManifest

    def numPlayers = 3
    def long2PublicNum(l: scala.Long) = l
  }

  def main(args: Array[String]) {
    println("Well hello!")

    println("Doing stuff in the normal world")
    val a = plainDSL.vec2XorVec(Array(1,2,3))
    val b = plainDSL.vec2XorVec(Array(1,2,3))
    val dotP = plainDSL.dotProduct(a, b)
    println(dotP)

    println("Doing stuff in the MPC world")
    val a2 = mpcDSL.vec2XorVec(Array(1,2,3))
    val b2 = mpcDSL.vec2XorVec(Array(1,2,3))
    val dotP2 = mpcDSL.dotProduct(a2, b2)
    println(mpcDSL.reveal(dotP2))
  }
}
