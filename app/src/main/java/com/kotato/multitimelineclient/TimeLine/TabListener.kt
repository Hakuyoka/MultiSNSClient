package com.kotato.multitimelineclient.TimeLine

import android.support.design.widget.TabLayout
import android.util.Log
import com.kotato.multitimelineclient.SNSService.TwitterService
import com.twitter.sdk.android.core.TwitterCore
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by kotato on 2017/07/18.
 */

class TabListener(val timeLineItemFragment: TimeLineItemFragment) : TabLayout.OnTabSelectedListener {
    companion object {
        var postion = TIME_LINE_TYPE.create(0)
    }

    val timeLineMap = mutableMapOf<Int, List<TimeLineItem>>()
    var maxIdMap = mutableMapOf<Int, Long?>()
    /**
     * Called when a tab enters the selected state.

     * @param tab The tab that was selected
     */
    override fun onTabSelected(tab: TabLayout.Tab?) {
        postion = TIME_LINE_TYPE.create(tab?.position ?: 0)
        Log.d("Tab", "First Selected " + tab?.position)
        if (tab != null) launch(CommonPool) {
            val timeLineList = timeLineMap[tab.position] ?: mutableListOf()
            val savedList = getTimeList(TwitterCore.getInstance().sessionManager.activeSession.userId, TIME_LINE_TYPE.create(tab.position)?.id ?: 0)
            val maxId = savedList.maxBy { it.id }?.id
            maxIdMap.put(tab.position, maxId)
            val timeLineItem = when (tab.position) {
                0 -> TwitterService.getTimeLine(maxId).await()
                1 -> TwitterService.getMentions(maxId).await()
                2 -> TwitterService.getFavoriteList { }.await()
                else -> mutableListOf()
            }
            Log.d("Tab", "First Get TimeLine " + timeLineItem.size)

            val margedList = timeLineList.plus(savedList).plus(timeLineItem).distinct()
            launch(UI) {
                timeLineItemFragment.removeAll()
                timeLineItemFragment.addAll(margedList.sortedBy { it.id }.reversed())
            }

        }
    }

    /**
     * Called when a tab exits the selected state.

     * @param tab The tab that was unselected
     */
    override fun onTabUnselected(tab: TabLayout.Tab?) {
        Log.d("Tab", "Unselected " + tab?.position)
        launch(CommonPool) {
            if (tab != null) {
                timeLineMap[tab.position] = timeLineItemFragment.getAll()
            }
        }
    }

    /**
     * Called when a tab that is already selected is chosen again by the user. Some applications
     * may use this action to return to the top level of a category.

     * @param tab The tab that was reselected.
     */
    override fun onTabReselected(tab: TabLayout.Tab?) {
        postion = TIME_LINE_TYPE.create(tab?.position ?: 0)
        Log.d("Tab", "Reselected " + tab?.position)
        if (tab != null) launch(CommonPool) {

            val maxId = maxIdMap.get(tab.position)
            val timeLineItem = when (tab.position) {
                0 -> TwitterService.getTimeLine(maxId).await()
                1 -> TwitterService.getMentions(maxId).await()
                2 -> TwitterService.getFavoriteList { }.await()
                else -> mutableListOf()
            }


            if (timeLineItem.isNotEmpty()) {
                launch(UI) {
                    var marged = timeLineItemFragment.margeTimeLine(timeLineItem)
                    timeLineItemFragment.addAll(marged)
                }
            }

        }

    }
}