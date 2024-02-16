package com.example.cryptoapp.data.network.model

import com.google.gson.annotations.SerializedName

data class CoinNameContainerDto (
    @SerializedName("CoinInfo")
    val coinNameDto: CoinNameDto? = null
)
