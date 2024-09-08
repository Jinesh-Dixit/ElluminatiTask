package com.example.servicelist.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.servicelist.model.AddCartItem
import com.example.servicelist.model.ServiceResponse
import com.example.servicelist.model.SpecificationsItem


class ServiceListViewModel : ViewModel() {

    lateinit var serviceResponse: ServiceResponse
    var specifications: List<SpecificationsItem> = arrayListOf()
    var addToCartItem: ArrayList<AddCartItem> = arrayListOf()
    var currentPrice = 0.00
    var mainCount = 1
}