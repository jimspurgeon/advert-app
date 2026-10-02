package com.retarget.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Hilt root; nothing else belongs here — feature
 * initialization happens lazily as phases land.
 */
@HiltAndroidApp
class RetargetApplication : Application()
