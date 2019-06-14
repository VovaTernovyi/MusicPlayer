package com.ternovyi.musicplayer

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent

class CustomTouchListener(context: Context, var clickListener: OnItemClickListener?) :
    RecyclerView.OnItemTouchListener {

    //Gesture detector to intercept the touch events
    var gestureDetector: GestureDetector? = null

    init {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })
    }

    override fun onInterceptTouchEvent(recyclerView: RecyclerView, e: MotionEvent): Boolean {

        val child = recyclerView.findChildViewUnder(e.x, e.y)
        if (child != null && clickListener != null && gestureDetector!!.onTouchEvent(e)) {
            clickListener!!.onClick(child, recyclerView.getChildLayoutPosition(child))
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}
