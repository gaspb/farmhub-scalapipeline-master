package org.highjack.scalapipeline.mock

/**
  * Created by High Jack on 19/10/2018.
  */
sealed class ConditionalApplicative[T] private(val value: T) { // if condition class wrapper
class ElseApplicative(value: T, elseCondition: Boolean) extends ConditionalApplicative[T](value) {
    // else condition class wrapper extends ConditionalApplicative to avoid double wrapping
    // in case: $if(condition1) { .. }. $if(condition2) { .. }
    def $else(f: T => T): T = if(elseCondition) f(value) else value
}

    // if method for external scope condition
    def $if(condition: => Boolean)(f: T => T): ElseApplicative =
        if(condition) new ElseApplicative(f(value), false)
        else new ElseApplicative(value, true)

    // if method for internal scope condition
    def $if(condition: T => Boolean) (f: T => T): ElseApplicative =
        if(condition(value)) new ElseApplicative(f(value), false)
        else new ElseApplicative(value, true)
}

object ConditionalApplicative { // Companion object for using ConditionalApplicative[T] generic
    implicit def lift2ConditionalApplicative[T](any: T): ConditionalApplicative[T] =
        new ConditionalApplicative(any)

    implicit def else2T[T](els: ConditionalApplicative[T]#ElseApplicative): T =
        els.value
}
