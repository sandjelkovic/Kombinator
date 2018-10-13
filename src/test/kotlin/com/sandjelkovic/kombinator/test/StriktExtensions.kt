package com.sandjelkovic.kombinator.test

import arrow.core.Either
import arrow.core.Option
import arrow.core.Some
import strikt.api.Assertion
import strikt.assertions.isA

/**
 * @author sandjelkovic
 * @date 2018-10-05
 */

// Arrow
fun <T> Assertion.Builder<Option<T>>.isDefined(valueAssertions: Assertion.Builder<T>.() -> Unit = {}): Assertion.Builder<T> =
    assertThat("Value is not present") { it.isDefined() }
        .isA<Some<T>>()
        .get { t }
        .and(valueAssertions)

fun <T> Assertion.Builder<Option<T>>.isEmpty(): Assertion.Builder<Option<T>> =
    assertThat("Value is present") { it.isEmpty() }

fun <T> Assertion.Builder<Option<T>>.isNone(): Assertion.Builder<Option<T>> = isEmpty()

fun <L, R> Assertion.Builder<Either<L, R>>.isRight(valueAssertions: Assertion.Builder<R>.() -> Unit = {}): Assertion.Builder<R> =
    assertThat("Either is Left instead of Right") { it.isRight() }
        .isA<Either.Right<R>>()
        .get { b }
        .and(valueAssertions)

fun <L, R> Assertion.Builder<Either<L, R>>.isLeft(valueAssertions: Assertion.Builder<L>.() -> Unit = {}): Assertion.Builder<L> =
    assertThat("Either is Right instead of Left") { it.isLeft() }
        .isA<Either.Left<L>>()
        .get { a }
        .and(valueAssertions)
