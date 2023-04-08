package com.github.nailkhaf.feature.account

import com.github.nailkhaf.feature.account.AddressValidator.Result.*
import com.github.nailkhaf.web3.models.decodeAddress
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddressValidatorTest : KoinTest {

    @BeforeTest
    fun setup() {
        startKoin { modules(accountModule) }
    }

    @AfterTest
    fun clean() {
        stopKoin()
    }

    @Test
    fun `expect wrong format`() {
        val addressValidator = get<AddressValidator>()

        assertEquals(WrongFormat, addressValidator("Ef1c6E67703c7BD7107eed8303Fbe6EC2554BF6B"))
        assertEquals(WrongFormat, addressValidator("Ef1c6E67703c7BD7107eed8303Fbe6EC2554BF6Z"))
        assertEquals(WrongFormat, addressValidator("0xEf1c6E67703c7BD7107eed8303Fbe6EC2554BF"))
    }

    @Test
    fun `expect wrong checksum`() {
        val addressValidator = get<AddressValidator>()

        assertEquals(WrongChecksum, addressValidator("0xEf1c6E67703c7BD7107eed8303Fbe6EC2554BF6b"))
        assertEquals(WrongChecksum, addressValidator("0xEf1c6E67703c7bD7107eed8303Fbe6EC2554BF6B"))
    }

    @Test
    fun `expect success`() {
        val addressValidator = get<AddressValidator>()
        val expectedAddress = "0xEf1c6E67703c7BD7107eed8303Fbe6EC2554BF6B".decodeAddress()

        assertEquals(
            Success(expectedAddress),
            addressValidator("0xEf1c6E67703c7BD7107eed8303Fbe6EC2554BF6B")
        )
    }
}