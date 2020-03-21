package labs.pooh.eaterslab.ui.gallery

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_gallery.*
import labs.pooh.eaterslab.R

class GalleryFragment : Fragment() {

    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textGallery.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageViewCamera.setOnClickListener { dispatchTakePictureIntent(view.context) }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun dispatchTakePictureIntent(context: Context) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(context.packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                (data?.extras?.get("data") as? Bitmap)?.let { imageBitmap ->
                    imageViewCamera.setImageBitmap(imageBitmap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}
