package com.livingtechusa.reflexion.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.livingtechusa.reflexion.util.Constants
import java.sql.DriverManager

@Composable
fun WebViewer() {
    // Declare a string that contains a url
    val mUrl = "https://www.geeksforgeeks.org"

    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl(mUrl)
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}


class WebClient : WebViewClient() {
//    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
//        view.loadUrl(request.url.toString())
//        return true
//    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        //Save the last visited URL to shared preference
        DriverManager.println(url)
    }
}

class WebResourceBuild(text: String): WebResourceRequest {

    private var Url = text.toUri()

    fun setUrl(newText: String) {
        Url = newText.toUri()
    }
    override fun getUrl(): Uri {
        return Url
    }

    override fun isForMainFrame(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isRedirect(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasGesture(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMethod(): String {
        TODO("Not yet implemented")
    }

    override fun getRequestHeaders(): MutableMap<String, String> {
        TODO("Not yet implemented")
    }

}