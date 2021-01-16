package com.app.goratest

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class PhotosAdapter(private val context: Activity, private val photos: List<Photo>) :
    BaseAdapter() {

    private val cacheSize = Runtime.getRuntime().maxMemory() / 8
    private val cache = object : LruCache<String, Bitmap>(cacheSize.toInt()) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.byteCount / 8
        }
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = p1 ?: LayoutInflater.from(context).inflate(R.layout.list_photo, null, false)
        val image = view.findViewById(R.id.imageView) as ImageView
        val title = view.findViewById(R.id.title) as TextView
        val progressBar = view.findViewById(R.id.progressBar) as ProgressBar
        title.text = photos[p0].title
        thread(start = true) {
            val bitmap = getBitmapFromURL(photos[p0].url)
            context.runOnUiThread {
                progressBar.visibility = View.GONE
                image.setImageBitmap(bitmap)
            }
        }

        return view
    }

    override fun getItem(p0: Int) = photos[p0]

    override fun getItemId(p0: Int) = p0.toLong()

    override fun getCount() = photos.size

    @Synchronized
    private fun getBitmapFromURL(src: String): Bitmap? {
        val url = URL(src)
        return cache[src] ?: run {
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.apply {
                readTimeout = 3000
                connectTimeout = 3000
                requestMethod = "GET"
                addRequestProperty("User-Agent", "my-user-agent")
            }
            try {
                val input: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                cache.put(src, bitmap)
                bitmap
            } catch (e: Throwable) {
                context.runOnUiThread { Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show() }
                null
            }
        }
    }
}