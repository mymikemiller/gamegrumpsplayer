package com.mymikemiller.gamegrumpsplayer

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.api.client.util.DateTime

import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class RecyclerAdapter(private val mDetails: MutableList<Detail>, private val isSelectedCallback: (detail: Detail) -> Boolean) : RecyclerView.Adapter<RecyclerAdapter.DetailHolder>() {

    class DetailHolder
    (v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private val mRootLayout: LinearLayout
        private val mThumbnail: ImageView
        private val mGame: TextView
        private val mTitle: TextView
        private val mDate: TextView
        private lateinit var mDetail: Detail
        private lateinit var mIsSelectedCallback: (detail: Detail) -> Boolean;

        init {
            mRootLayout = v.findViewById(R.id.rootLayout)
            mThumbnail = v.findViewById<ImageView>(R.id.thumbnail)
            mGame = v.findViewById<TextView>(R.id.game)
            mTitle = v.findViewById<TextView>(R.id.title)
            mDate = v.findViewById<TextView>(R.id.date)
            v.setOnClickListener(this)
        }

        fun setIsSelectedCallback(callback: (detail: Detail) -> Boolean) {
            mIsSelectedCallback = callback
        }

        fun highlight() {
            mRootLayout.setBackgroundResource(R.color.orange)
        }
        fun unhighlight() {
            mRootLayout.setBackgroundResource(R.color.light_font)
        }

        override fun onClick(v: View) {
            // Play the current video
        }

        fun bindDetail(detail: Detail) {
            mDetail = detail
            Picasso.with(mThumbnail.context).load(detail.thumbnail).into(mThumbnail)

            val part = if (detail.part.length > 0) " (" + detail.part + ")" else ""
            val fullTitle = detail.game + part
            mGame.setText(fullTitle)
            mTitle.setText(detail.title)

            val format = SimpleDateFormat("E MMM dd, yyyy", Locale.US)
            val date = Date(detail.dateUploaded.value)
            val dateString = format.format(date)
            mDate.setText(dateString)

            if (mIsSelectedCallback(mDetail)) {
                highlight()
            } else {
                unhighlight()
            }
        }

        companion object {
            private val DETAIL_KEY = "DETAIL"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.DetailHolder {
        val inflatedView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item_row, parent, false)

        return DetailHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.DetailHolder, position: Int) {
        val itemDetail = mDetails[position]
        holder.setIsSelectedCallback(isSelectedCallback)
        holder.bindDetail(itemDetail)
    }

    override fun getItemCount(): Int {
        return mDetails.size
    }
}
