package labs.pooh.eaterslab.util

import android.app.Activity
import android.content.Context
import android.content.Intent

inline fun <reified T : Activity> Activity.start(intentBlock: Intent.() -> Unit = {})
        = startActivity(Intent(this, T::class.java).apply(intentBlock))


inline fun <reified T : Activity> Context.start(intentBlock: Intent.() -> Unit = {})
        = startActivity(Intent(this, T::class.java).apply(intentBlock))
