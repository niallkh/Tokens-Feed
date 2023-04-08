package com.github.nailkhaf.feature.account

import com.github.nailkhaf.web3.models.Address
import com.github.nailkhaf.web3.models.decodeAddress
import com.github.nailkhaf.web3.models.formatChecksum

val addressRegex = Regex("^0[xX][0-9a-fA-F]{40}$")
val upperCaseRegex = Regex("[A-F]")

fun interface AddressValidator {
    operator fun invoke(text: String): Result

    sealed interface Result {
        object WrongFormat : Result
        object WrongChecksum : Result
        data class Success(val address: Address) : Result
    }
}

fun addressValidator(): AddressValidator = AddressValidator { text ->
    if (text.matches(addressRegex).not()) {
        return@AddressValidator AddressValidator.Result.WrongFormat
    }

    val address = text.decodeAddress()

    if (text.contains(upperCaseRegex) && address.formatChecksum() != text) {
        return@AddressValidator AddressValidator.Result.WrongChecksum
    }

    AddressValidator.Result.Success(address)
}