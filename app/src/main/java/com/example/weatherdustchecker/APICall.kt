package com.example.weatherdustchecker

import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL

class APICall(val callback: APICall.APICallback) :
    AsyncTask<URL, Void, String>() {

    interface APICallback {
        fun onComplete(result: String)
    }

    override fun doInBackground(vararg params: URL?): String {
        val url = params[0]
        val conn = url?.openConnection() as HttpURLConnection
        conn.connect()

        val body = conn?.inputStream.bufferedReader().use{
            it.readText()
        }

        conn.disconnect()

        return body //json data
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        callback.onComplete(result!!)
    }

}
