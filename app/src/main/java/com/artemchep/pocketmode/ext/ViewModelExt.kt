package com.artemchep.pocketmode.ext

import android.content.Context
import androidx.lifecycle.AndroidViewModel

val AndroidViewModel.context: Context
    get() = getApplication()
