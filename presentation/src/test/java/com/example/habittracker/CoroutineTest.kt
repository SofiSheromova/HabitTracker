package com.example.habittracker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule

@ExperimentalCoroutinesApi
interface CoroutineTest {

    @get:Rule
    val coroutineRule: CoroutineScopeRule

    fun test(
        block: suspend TestCoroutineDispatcher.() -> Unit
    ) = coroutineRule.testDispatcher.runBlockingTest { block(coroutineRule.testDispatcher) }
}