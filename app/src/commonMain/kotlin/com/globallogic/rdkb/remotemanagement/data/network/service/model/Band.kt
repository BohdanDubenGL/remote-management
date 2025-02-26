package com.globallogic.rdkb.remotemanagement.data.network.service.model

enum class Band(val displayedName: String, val radio: Int) {
    Band_2_4("2.4GHz", 10_000),
    Band_5("5GHz", 10_100),
    Band_6("6GHz", 10_200);
}
