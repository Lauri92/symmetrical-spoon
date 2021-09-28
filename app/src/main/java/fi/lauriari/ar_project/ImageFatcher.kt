//package fi.lauriari.ar_project
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.util.Log
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.async
//
//import androidx.lifecycle.lifecycleScope
//import kotlinx.coroutines.GlobalScope
//
//import kotlinx.coroutines.launch
//import java.net.URL
//
//class ImageFatcher {
//    private val IMG_URL = URL("http://users.metropolia.fi/~minjic/AR_project/emerald.png")
//
//    private fun getImgBitmap(){
//        GlobalScope.launch(context = Dispatchers.Main) {
//            val img = async(Dispatchers.IO) { getImg(IMG_URL) }
//            showImg(img.await())
//        }
//    }
//
//    private suspend fun getImg(imgUrl: URL):Bitmap {
//        val inputStream = imgUrl.openStream()
//        Log.d("image","${inputStream}")
//        return BitmapFactory.decodeStream(inputStream)
//    }
//
//    private fun showImg(serverImg: Bitmap){
//
//    }
//}