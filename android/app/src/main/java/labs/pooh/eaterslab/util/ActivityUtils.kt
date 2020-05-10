package labs.pooh.eaterslab.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.material.snackbar.Snackbar

inline fun <reified T : Activity> Activity.start(intentBlock: Intent.() -> Unit = {})
        = startActivity(Intent(this, T::class.java).apply(intentBlock))


inline fun <reified T : Activity> Context.start(intentBlock: Intent.() -> Unit = {})
        = startActivity(Intent(this, T::class.java).apply(intentBlock))

fun Activity.snack(message: String, duration: Int = Snackbar.LENGTH_SHORT)
        = Snackbar.make(findViewById(android.R.id.content), message, duration).show()
