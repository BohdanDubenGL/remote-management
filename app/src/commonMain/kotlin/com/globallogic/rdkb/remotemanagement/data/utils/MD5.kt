package com.globallogic.rdkb.remotemanagement.data.utils

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.MD5
import dev.whyoleg.cryptography.operations.Hasher

@OptIn(DelicateCryptographyApi::class)
private val md5Hasher: Hasher = CryptographyProvider.Default.get(MD5).hasher()

suspend fun String.md5(): String = md5Hasher.hash(encodeToByteArray()).decodeToString()
