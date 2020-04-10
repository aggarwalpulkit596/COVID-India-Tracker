package com.pulkit.covidindiatracker

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

class SpannableDelta(langName: String, langColor: String, start: Int) : SpannableString(langName) {
    init {
        setSpan(
            ForegroundColorSpan(Color.parseColor(langColor)),
            start,
            langName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}