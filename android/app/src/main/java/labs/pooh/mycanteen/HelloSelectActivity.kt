package labs.pooh.mycanteen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_hello_select.*

class HelloSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_select)

        fabSearch.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        fabMap.setOnClickListener {
            startActivity(Intent(Intent(applicationContext, MapSearchActivity::class.java)))
        }
    }
}
