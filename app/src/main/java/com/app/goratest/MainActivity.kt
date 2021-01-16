package com.app.goratest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object {
        const val ARG_ALBUM_ID = "albumId"
        private const val URL_USERS = "https://jsonplaceholder.typicode.com/users"
        private const val URL_ALBUMS = "https://jsonplaceholder.typicode.com/albums"
        private const val VALUE_ID = "id"
        private const val VALUE_NAME = "name"
        private const val VALUE_USER_ID = "userId"
        private const val VALUE_TITLE = "title"
    }

    private val listOfUsers = mutableListOf<User>()
    private val listOfAlbums = mutableListOf<Album>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expandableList.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val albumId =
                listOfAlbums.filter { it.userId == listOfUsers[groupPosition].id }[childPosition].id
            val intent =
                Intent(this, PhotosActivity::class.java).apply { putExtra(ARG_ALBUM_ID, albumId) }
            startActivity(intent)
            true
        }

        val urlConnection1 = URL(URL_USERS).openConnection() as HttpURLConnection
        val urlConnection2 = URL(URL_ALBUMS).openConnection() as HttpURLConnection
        urlConnection1.apply {
            readTimeout = 3000
            connectTimeout = 3000
            requestMethod = "GET"
        }
        urlConnection2.apply {
            readTimeout = 3000
            connectTimeout = 3000
            requestMethod = "GET"
        }

        thread(start = true) {
            try {
                urlConnection1.inputStream.bufferedReader().use { reader1 ->
                    val result1 = reader1.readText()
                    val users = JSONArray(result1)
                    for (i in 0 until users.length())
                        listOfUsers.add(
                            User(
                                (users[i] as JSONObject).get(VALUE_ID) as Int,
                                (users[i] as JSONObject).get(VALUE_NAME) as String
                            )
                        )

                    urlConnection2.inputStream.bufferedReader().use { reader2 ->
                        val result2 = reader2.readText()
                        val albums = JSONArray(result2)
                        for (i in 0 until albums.length())
                            listOfAlbums.add(
                                Album(
                                    (albums[i] as JSONObject).get(VALUE_ID) as Int,
                                    (albums[i] as JSONObject).get(VALUE_USER_ID) as Int,
                                    (albums[i] as JSONObject).get(VALUE_TITLE) as String
                                )
                            )

                        val expandableListAdapter = ListAdapter(this, listOfUsers, listOfAlbums)
                        runOnUiThread {
                            expandableList.setAdapter(expandableListAdapter)
                        }
                    }
                }
            } catch (e: Throwable) {
                runOnUiThread { Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show() }
            }
        }

    }
}