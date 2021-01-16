package com.app.goratest

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.goratest.MainActivity.Companion.ARG_ALBUM_ID
import kotlinx.android.synthetic.main.activity_photos.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class PhotosActivity : AppCompatActivity() {

    companion object {
        private const val URL_PHOTOS = "https://jsonplaceholder.typicode.com/photos?albumId="
        private const val VALUE_TITLE = "title"
        private const val VALUE_URL = "url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        val albumId = intent.getIntExtra(ARG_ALBUM_ID, 0)

        val urlConnection = URL(URL_PHOTOS + albumId).openConnection() as HttpURLConnection
        urlConnection.apply {
            readTimeout = 3000
            connectTimeout = 3000
            requestMethod = "GET"
        }

        thread(start = true) {
            try {
                urlConnection.inputStream.bufferedReader().use { reader ->
                    val result = reader.readText()
                    val photos = JSONArray(result)
                    val listOfPhotos = mutableListOf<Photo>()
                    for (i in 0 until photos.length())
                        listOfPhotos.add(
                            Photo(
                                (photos[i] as JSONObject).get(VALUE_TITLE) as String,
                                (photos[i] as JSONObject).get(VALUE_URL) as String
                            )
                        )

                    runOnUiThread {
                        listView.adapter = PhotosAdapter(this, listOfPhotos)
                    }
                }
            } catch (e: Throwable) {
                runOnUiThread { Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show() }
            }
        }
    }
}