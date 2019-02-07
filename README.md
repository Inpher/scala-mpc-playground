### An MPC playground in Scala

Welcome! In this repository you will find a basic embedding of secure multi-party computations in Scala.

#### MPC

[MPC](https://en.wikipedia.org/wiki/Secure_multi-party_computation) is a subfield in cryptography with the goal of creating methods for parties to jointly compute a function over their inputs while keeping those inputs private. In the [MPC.scala](https://github.com/Inpher/scala-mpc-playground/blob/master/src/main/scala/mpc/MPC.scala) file we provide an abstract implementation of such a setting. Of particular interest:

 - We define abstract types `PublicNum` from `SecretNum` which distinguish the format of plaintext values from secret shared ones. Both these formats allow for addition and multiplication via their respective implicit typeclasses.
 - The `SharedNum` type represents __secret shares__ of a number that are distributed amongst multiple players. We use an Array to represent the various players. In reality the value of each index would reside at the site of a given player.
 - We define conversions from `PublicNum` to `SharedNum`, and vice-versa. The act of converting a `SharedNum` to a `PublicNum` is known as `reveal`.
  - Our model of computation has a trusted dealer, represented here by a global random generator. Its job is to create secret shares for given numbers (`createShares`).
  - We then define addition and multiplication on `SharedNum`.

 An implementation of the `MPC` interface can be found in [LongMPC.scala]](https://github.com/Inpher/scala-mpc-playground/blob/master/src/main/scala/mpc/LongMPC.scala), where we set the format of both public and secret values to `Long`. A basic test of multiplication can be found in the `LongMPCImpl` object.

 ### DSL
 We define a very simple DSL that allows trivial manipulations of Vectors ([DSL.scala](https://github.com/Inpher/scala-mpc-playground/blob/master/src/main/scala/dsl/DSL.scala)). In this DSL, we have two _abstract_ types, `Long` (different from the actual `scala.Long`) and `Vector`. Exercise: can you add Matrices?
 We can implement this interface two ways:

  - The classic implementation, i.e. [plaintext only](https://github.com/Inpher/scala-mpc-playground/blob/master/src/main/scala/dsl/ClassicImpl.scala). Here, both abstract types take traditional values (the DSL `Long` becomes `scala.Long`, and the DSL `Vector` becomes a simple `scala.Array`).
  - The [MPC implementation](https://github.com/Inpher/scala-mpc-playground/blob/master/src/main/scala/dsl/MPCImpl.scala).

Finally, a [basic test](https://github.com/Inpher/scala-mpc-playground/blob/master/src/main/scala/dsl/DSLTest.scala) shows how to run a program in different contexts.
