package com.example.servicelist.model


data class AddCartItem(
    var serviceResponse: ServiceResponse,
    var price : Double = 0.00,
    var isRepeat : Boolean = false
)
