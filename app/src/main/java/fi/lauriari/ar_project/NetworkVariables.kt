package fi.lauriari.ar_project

import android.util.Log
import kotlin.properties.Delegates

/**
 * Network status available to anywhere in the application
 */
object NetworkVariables {
    var isNetworkConnected: Boolean by Delegates.observable(false) { _, _, newValue ->
        Log.d("networktets", "new value: $newValue")
    }
}