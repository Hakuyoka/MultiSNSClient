package com.kotato.multitimelineclient.Media

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ImageView
import com.kotato.multitimelineclient.ImageLoadManger
import com.kotato.multitimelineclient.ImageQue
import com.kotato.multitimelineclient.R
import com.mopub.volley.RequestQueue
import com.mopub.volley.toolbox.Volley
import java.util.ArrayList

/**
 * 画像の拡大用アクティビティ
 */
class MediaTabbedActivity : AppCompatActivity() {

    /**
     * 画像のURLのリスト
     */
    val uris: ArrayList<String> by lazy {
        intent.getStringArrayListExtra("uris")
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
            return PlaceholderFragment.newInstance(position + 1, uris[position], queue)
        }

        override fun getCount(): Int {
            return uris.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "Image No.$position"
        }
    }

    class PlaceholderFragment(val uri: String, val requestQueue: RequestQueue) : Fragment() {



        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_media_tabbed, container, false)
            val imageView = rootView.findViewById<View>(R.id.imageView) as ImageView
            ImageLoadManger.addImageQue(requestQueue, ImageQue(uri, imageView), android.R.color.transparent)

//            val detector: ScaleGestureDetector =
//                ScaleGestureDetector(imageView.context, object : ScaleGestureDetector.OnScaleGestureListener{
//
//                    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
//                        println("onScaleBegin")
//                        return true
//                    }
//
//                    override fun onScaleEnd(detector: ScaleGestureDetector?) {
//                        println("onScaleEnd")
//                    }
//
//                    override fun onScale(detector: ScaleGestureDetector?): Boolean {
//                        println("OnScale")
//                        return true
//                    }
//                })
//
//            imageView.setOnTouchListener { view, motionEvent ->
//                println("TouchEvent")
//                detector.onTouchEvent(motionEvent)
//                view.onTouchEvent(motionEvent)
//            }



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
}

class MyImageView(context: Context) :ImageView(context){

    val detector: ScaleGestureDetector =
            ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener{

                override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                    println("onScaleBegin")
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector?) {
                    println("onScaleEnd")
                }

                override fun onScale(detector: ScaleGestureDetector?): Boolean {
                    println("OnScale")
                    return true
                }
            })

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        println("TouchEvent")
        detector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}