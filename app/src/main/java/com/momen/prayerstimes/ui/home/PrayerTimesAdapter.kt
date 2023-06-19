package com.momen.prayerstimes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.momen.prayerstimes.R

class PrayerTimesAdapter(
    context: Context,
    prayerTimes: List<Pair<String, String>>
) : ArrayAdapter<Pair<String, String>>(context, R.layout.item_prayer_time, prayerTimes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_prayer_time, parent, false)

        val prayerNameTextView = view.findViewById<TextView>(R.id.textViewPrayerName)
        val prayerTimeTextView = view.findViewById<TextView>(R.id.textViewPrayerTime)

        val prayerTime = getItem(position)
        prayerNameTextView.text = prayerTime?.first
        prayerTimeTextView.text = prayerTime?.second

        return view
    }
}
