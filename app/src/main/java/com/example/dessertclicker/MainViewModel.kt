package com.example.dessertclicker

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.data.Datasource.dessertList
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    // initial state of the app
    private val _dessertAppState = MutableStateFlow(DessertState())
    val dessertAppState = _dessertAppState.asStateFlow()

    fun onDessertClick() {
        // determine which dessert to show
        val dessertToShow = determineDessertToShow()

        // update the state of the app
        _dessertAppState.update { currentState ->
            currentState.copy(
                revenue = currentState.revenue + currentState.currentDessertPrice,   // new revenue
                dessertsSold = currentState.dessertsSold + 1,
                currentDessertPrice = dessertToShow.price,
                currentDessertImageId = dessertToShow.imageId
            )
        }
    }

    private fun determineDessertToShow(): Dessert {
        var dessertToShow = dessertList.first()
        val updatedDessertsSold = _dessertAppState.value.dessertsSold + 1

        for (dessert in dessertList) {
            if (updatedDessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }
        return dessertToShow
    }

    fun shareSoldDessertsInformation(intentContext: Context) {
        val dessertsSold = _dessertAppState.value.dessertsSold
        val revenue = _dessertAppState.value.revenue

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                intentContext.getString(R.string.share_text, dessertsSold, revenue)
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        try {
            ContextCompat.startActivity(intentContext, shareIntent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                intentContext,
                intentContext.getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}