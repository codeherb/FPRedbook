package io.funfun.redbook

import scala.annotation.tailrec

sealed trait FPList[+A] {

  @tailrec
  final def map[B](acc: FPList[B] = None)(f: A => B): FPList[B] = this match {
    case Cons(h, t) => t.map(Cons(f(h), acc))(f)
    case _ => acc.reverse()
  }

  @tailrec
  final def reverse[A](acc: FPList[A] = None): FPList[A] = this match {
    case Cons(h, t) => t.reverse(Cons(h, acc))
    case _ => acc
  }

  @tailrec
  final def filter[A](acc: FPList[A] = None)(predicate: A => Boolean): FPList[A] = this match {
    case Cons(h, t) => t.filter(if (predicate(h)) { Cons(h, acc) } else { acc })(predicate)
    case _ => acc
  }

}

case class Cons[+A](head: A, tail: FPList[A]) extends FPList[A]
case object None extends FPList[Nothing]

object FPList {

  def apply[A](as: A*): FPList[A] =
    if (as.isEmpty) None
    else Cons(as.head, apply(as.tail: _*))

}
