package io.funfun.redbook

import scala.annotation.tailrec

sealed trait FPList[+A]

case class Cons[+A](head: A, tail: FPList[A]) extends FPList[A]
case object None extends FPList[Nothing]

object FPList {

  @tailrec
  def map[A, B](as: FPList[A], acc: FPList[B] = None)(f: A => B): FPList[B] = as match {
    case Cons(h, t) => map(t, Cons(f(h), acc))(f)
    case _ => reverse(acc)
  }

  @tailrec
  def reverse[A](as: FPList[A], acc: FPList[A] = None): FPList[A] = as match {
    case Cons(h, t) => reverse(t, Cons(h, acc))
    case _ => acc
  }

  @tailrec
  def filter[A](as: FPList[A], acc: FPList[A] = None)(predicate: A => Boolean): FPList[A] = as match {
    case Cons(h, t) => filter(t, if (predicate(h)) Cons(h, acc) else acc )(predicate)
    case _ => acc
  }

  def apply[A](as: A*): FPList[A] =
    if (as.isEmpty) None
    else Cons(as.head, apply(as.tail: _*))

}
