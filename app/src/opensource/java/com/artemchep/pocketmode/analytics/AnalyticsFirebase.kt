package com.artemchep.pocketmode.analytics

import android.app.Application

fun createAnalytics(application: Application): Analytics =
    AnalyticsStub()
