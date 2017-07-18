package com.kotato.multitimelineclient.Media

import android.content.ContentUris
import android.net.Uri
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout

import android.widget.TextView

import com.kotato.multitimelineclient.R
import com.kotato.multitimelineclient.TimeLine.IMAGE_CHACH_MAX_SIZE
import com.kotato.multitimelineclient.TimeLine.MyImageCache
import com.mopub.volley.toolbox.ImageLoader
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class MediaTabbedActivity : AppCompatActivity() {

    val uris by lazy {
        intent.getStringArrayListExtra("uris")
    }

    val imageLoader by lazy {
        var bais = ByteArrayInputStream(intent.getByteArrayExtra("imageLoaderByteArray"))
        val ois = ObjectInputStream(bais)
        ois.readObject() as? ImageLoader
    }

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The [ViewPager] that will host the section contents.
     */
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_tabbed)


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_media_tabbed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, uris[position], imageLoader)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return uris.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "SECTION 1"
                1 -> return "SECTION 2"
                2 -> return "SECTION 3"
            }
            return null
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment(val uri: String, val imageLoader: ImageLoader?) : Fragment() {


        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_media_tabbed, container, false)
            val imageView = rootView.findViewById<View>(R.id.imageView) as ImageView

            //cancel
            val imageContainer = imageView.tag
            if(imageContainer != null && imageContainer is ImageLoader.ImageContainer){
                imageContainer.cancelRequest()
            }
            val imageListener = ImageLoader.getImageListener(imageView, R.color.tw__seekbar_thumb_outer_color, R.color.tw__seekbar_thumb_outer_color)
            imageLoader?.get(uri, imageListener)
            imageView.setImageURI(Uri.parse(uri))
            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int, uri: String, imageLoader: ImageLoader?): PlaceholderFragment {
                val fragment = PlaceholderFragment(uri, imageLoader)
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
