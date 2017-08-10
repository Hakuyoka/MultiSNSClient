package com.kotato.multitimelineclient.Media

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.SNSService.TwitterService
import com.mopub.volley.RequestQueue
import com.mopub.volley.toolbox.Volley
import java.util.*


/**
 * 画像の拡大用アクティビティ
 */
class MediaTabbedActivity : AppCompatActivity() {

    /**
     * メディアのURLのリスト
     */
    val uris: ArrayList<String> by lazy {
        intent.getStringArrayListExtra("uris")
    }

    val type: String by lazy {
        intent.getStringExtra("type")
    }

    /**
     *
     */
    val queue: RequestQueue by lazy {
        //今の所アクテビティごとにQueueを生成
        Volley.newRequestQueue(this)
    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_tabbed)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager?.adapter = mSectionsPagerAdapter

    }



    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {


        override fun getItem(position: Int): Fragment {

//            return PlaceholderFragment.newInstance(position + 1, uris[position], queue)
            return when (type) {
                TwitterService.MEDIA_TYPE.IMAGE_TYPE.cagotery -> PlaceholderFragment.newInstance(position + 1, uris[position], queue)
                TwitterService.MEDIA_TYPE.MOVIE_TYPE.cagotery -> VideoFragment.newInstance(uris[position])
                else -> PlaceholderFragment.newInstance(position + 1, uris[position], queue)
            }
        }

        override fun getCount(): Int {
            return uris.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "Image No.$position"
        }
    }

    class PlaceholderFragment(val uri: String, val requestQueue: RequestQueue) : Fragment() {

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_media_tabbed, container, false)
            val imageView = rootView.findViewById<View>(R.id.photo_view) as PhotoView
//            ImageLoadManger.addImageQue(requestQueue, ImageQue(uri, imageView), android.R.color.transparent)
            Glide.with(context)
                    .load(uri)
                    .apply(RequestOptions().fitCenter())
                    .into(imageView)
            return rootView
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int, uri: String, requestQueue: RequestQueue): PlaceholderFragment {
                val fragment = PlaceholderFragment(uri, requestQueue)
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    class VideoFragment(val uri: String) : Fragment() {
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val player: SimpleExoPlayer by lazy {
            val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(defaultBandwidthMeter)
            val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            ExoPlayerFactory.newSimpleInstance(context, trackSelector)

        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_media_tabbed, container, false)
            val playerView = rootView.findViewById<View>(R.id.player_view) as SimpleExoPlayerView
            val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name)), defaultBandwidthMeter)
            val extractorsFactory = DefaultExtractorsFactory()
            val videoSource = ExtractorMediaSource(Uri.parse(uri), dataSourceFactory, extractorsFactory, null, null)

            player.prepare(videoSource)

            playerView.player = player
            playerView.visibility = View.VISIBLE

            return rootView
        }

        override fun onDestroy() {
            super.onDestroy()
            player.release()
        }

        companion object {
            fun newInstance(uri: String): VideoFragment {
                val fragment = VideoFragment(uri)
                val args = Bundle()
                fragment.arguments = args
                return fragment
            }
        }
    }
}
