package com.globallogic.rdkb.remotemanagement.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class GreetingTest {

    @Test fun test() {
        assertEquals("Hello, Test!", Greeting(object : Platform {
            override val name: String = "Test"
        }).greet())
    }
}
