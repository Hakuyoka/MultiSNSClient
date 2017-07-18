package com.kotato.multitimelineclient.TimeLine

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.kotato.multitimelineclient.R
import com.mopub.volley.toolbox.ImageLoader
import java.net.URL

class MediaFragment(val imageLoader : ImageLoader, url: String) : Fragment() {
    val url = url

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater?.inflate(R.layout.fragment_media, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view?.findViewById<View>(R.id.imageView) as ImageView
        Log.d("View Created", url)

//        imageView.setImageResource(R.drawable.tw__action_heart_on_default)
        //cancel
//        val imageContainer = imageView?.tag
//        if(imageContainer != null && imageContainer is ImageLoader.ImageContainer){
//            imageContainer.cancelRequest()
//        }
//
//        val imageListener = ImageLoader.getImageListener(imageView, R.color.tw__seekbar_thumb_outer_color, R.color.tw__seekbar_thumb_outer_color)
//        imageLoader.get(url, imageListener)
    }
}// Required empty public constructor
