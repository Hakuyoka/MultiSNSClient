package com.kotato.multitimelineclient.TimeLine

import android.support.design.widget.TabLayout
import android.util.Log
import com.kotato.multitimelineclient.SNSService.TwitterService
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Created by kotato on 2017/07/18.
 */

class TabListener(val timeLineItemFragment: TimeLineItemFragment): TabLayout.OnTabSelectedListener{
    val timeLineMap = mutableMapOf<Int, List<TimeLineItem>>()
    var currentTab = 0
    /**
     * Called when a tab enters the selected state.

     * @param tab The tab that was selected
     */
    override fun onTabSelected(tab: TabLayout.Tab?) {
        Log.d("Tab","First Selected "+ tab?.position)
        if(tab != null) launch(CommonPool){
            Log.d("Tab","Start get time line type:"+ tab.position)
            val timeLineItem = when (tab.position){
                0 -> TwitterService.getTimeLine().await()
                1 -> TwitterService.getMentions{}.await()
                2 -> TwitterService.getFavoriteList {  }.await()
                else -> arrayListOf()
            }
            Log.d("Tab","End get time line type:"+ timeLineItem)

            if (timeLineItem != null){
                async(UI){
                    if(currentTab != tab.position)
                        timeLineItemFragment.removeAll()

                    timeLineMap.put(tab.position, timeLineItem)
                    timeLineItemFragment.addAll(timeLineItem)
                    currentTab = tab.position
                }
            }
        }

    }

    /**
     * Called when a tab exits the selected state.

     * @param tab The tab that was unselected
     */
    override fun onTabUnselected(tab: TabLayout.Tab?) {
        Log.d("Tab","Unselected "+ tab?.position)
        launch(CommonPool){
            if(tab != null){
                val timeLineList = timeLineMap.get(tab.position)
                timeLineMap.set(tab.position, timeLineItemFragment.getAll())
            }
        }
    }

    /**
     * Called when a tab that is already selected is chosen again by the user. Some applications
     * may use this action to return to the top level of a category.

     * @param tab The tab that was reselected.
     */
    override fun onTabReselected(tab: TabLayout.Tab?) {
        Log.d("Tab","Reselected "+ tab?.position)
        if(tab != null) launch(CommonPool){
            val timeLineList = timeLineMap.get(tab.position)

            val timeLineItem = when (tab.position){
                0 -> TwitterService.getTimeLine().await()
                1 -> TwitterService.getMentions {  }.await()
                2 -> TwitterService.getFavoriteList {  }.await()
                else -> mutableListOf()
            }


            if (timeLineItem != null && timeLineList != null){
                async(UI){
                    if(currentTab != tab.position)
                        timeLineItemFragment.removeAll()

                    var marged = timeLineItemFragment.margeTimeLine(timeLineItem)
                    timeLineItemFragment.addAll(timeLineList.plus(marged))
                    timeLineMap.put(tab.position, timeLineList.plus(marged))
                    currentTab = tab?.position
                }

            }

        }

    }
}