package com.artemchep.pocketmode.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.artemchep.pocketmode.ext.context
import com.artemchep.pocketmode.sensors.ProximityLabLiveData


/**
 * @author Artem Chepurnoy
 */
class LabViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "LabViewModel"
    }

    val proximityLabLiveData = ProximityLabLiveData(context)

}