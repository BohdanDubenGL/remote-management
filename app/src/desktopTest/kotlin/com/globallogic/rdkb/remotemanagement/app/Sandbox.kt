package com.globallogic.rdkb.remotemanagement.app

import com.globallogic.rdkb.remotemanagement.data.network.RdkCentralHttpClient
import com.globallogic.rdkb.remotemanagement.data.network.service.impl.RdkCentralPropertyServiceImpl
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class Sandbox {

    @Test
    fun test() {
        runBlocking {
            val mac = "dca6320eb8bb"

            val service = RdkCentralPropertyServiceImpl(RdkCentralHttpClient())
        }
    }
}
