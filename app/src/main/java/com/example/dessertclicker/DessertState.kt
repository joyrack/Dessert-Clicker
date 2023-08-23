package com.example.dessertclicker

import com.example.dessertclicker.data.Datasource.dessertList as desserts

data class DessertState(
    val revenue: Int = 0,
    val dessertsSold: Int = 0,
    val currentDessertPrice: Int = desserts[0].price,
    val currentDessertImageId: Int = desserts[0].imageId,
)