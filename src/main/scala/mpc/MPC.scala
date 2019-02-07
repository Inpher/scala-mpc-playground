package mpc

/**
 * Here we model the computations in the MPC world.
 * We can think of programs written in this context as operating
 * with MPC primitives, such as Beaver triplet creation, secret shares, etc.
 *
 * The `MPC` trait represents computation at the mask/reveal level.
 * This is one level lower than the DSL level
 *
 * We consider only operations on Longs at the moment.
 * (i.e. no floating point computations)
 */
trait MPC {
  /**
   * In the MPC world, there are 3 kinds of values:
   * - `PublicNum`, numbers at the user level (i.e. original data format)
   * - `SecretNum`, numbers at the secret value level (i.e. the format which each
   *    player secretly computes on) (analogous to Serializable Value)
   * - `SharedNum`, representing secret shares of a value. Here we use Arrays,
   *    each index representing a player (similar to Morten Dahl's version
   *    at https://mortendahl.github.io/2017/04/17/private-deep-learning-with-mpc/).
   */
  type PublicNum
  type SecretNum
  type SharedNum = Array[SecretNum]

  /**
   * There must be ways to convert from a public to a shared number, and vice versa
   */
  def reveal(secrets: SharedNum): PublicNum
  def p2s(p:PublicNum): SecretNum

  //Some accompanying implicits
  implicit val secretNumManifest: Manifest[SecretNum]

  implicit val secretNumNumeric: Numeric[SecretNum]
  implicit val publicNumNumeric: Numeric[PublicNum]

  /**
   * An MPC computation is performed amongst many players
   */
  def numPlayers: Int

  /**
   * In the MPC world we also have a random generator (in our case held by the
   * trusted dealer), used to create shares
   */
  type RandGen = {
    def next(): SecretNum
  }
  def randGen: RandGen

  def shareConst(p: PublicNum): SharedNum = {
    val res = Array.ofDim[SecretNum](numPlayers)
    res(0) = p2s(p)
    (1 until numPlayers).foreach { i => res(i) = secretNumNumeric.zero }
    res
  }

  def createShares(numPlayers: Int, requiredSum: SecretNum): SharedNum = {
    val res = Array.ofDim[SecretNum](numPlayers)
      var sum = requiredSum
      (1 to numPlayers - 1).foreach { i =>
        val mask: SecretNum = randGen.next()
        res(i - 1) = mask
        sum = secretNumNumeric.plus(sum, mask)
      }
      res(numPlayers - 1) = secretNumNumeric.minus(requiredSum, sum)
      res
  }

  def createZeroSumShares = createShares(numPlayers, secretNumNumeric.zero)

  def createBeaverTriplet: (SharedNum, SharedNum, SharedNum) = {
    val a = randGen.next()
    val b = randGen.next()
    val ab = secretNumNumeric.times(a, b)

    (createShares(numPlayers, a), createShares(numPlayers, b), createShares(numPlayers, ab))
  }

  /**
   * Basic operations on secret shares that we must support
   */
  def negate(x: SharedNum): SharedNum = x.map(xi => secretNumNumeric.negate(xi))
  def add(x: SharedNum, y: SharedNum): SharedNum = x.zip(y).map {
    case (xi, yi) => secretNumNumeric.plus(xi, yi)
  }
  def addConst(s: SharedNum, p: PublicNum): SharedNum = add(s, shareConst(p))
  def sub(x: SharedNum, y: SharedNum) = add(x, negate(y))

  //xy = lm + ay + bx - ab
  //could also use xy = lm + ab - am - bl
  def mul(x: SharedNum, y: SharedNum): SharedNum = {
    val (lambda, mu, lm) = createBeaverTriplet

    val a = add(x, lambda)
    val b = add(y, mu)
    val publicA = reveal(a)
    val publicB = reveal(b)

    val ab = shareConst(publicNumNumeric.times(publicA,publicB))
    val bx = mulConst(x, publicB)
    val ay = mulConst(y, publicA)

    add(lm, add(ay, sub(bx, ab)))

  }

  def mulConst(s: SharedNum, p: PublicNum): SharedNum = {
    val secretP = p2s(p)
    s.map(xi => secretNumNumeric.times(secretP, xi))
  }
}
