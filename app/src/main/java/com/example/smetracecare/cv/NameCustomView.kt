package com.example.smetracecare.cv

import android.view.View
import android.text.InputType
import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatEditText
import com.example.smetracecare.R

class NameCustomView: AppCompatEditText, View.OnFocusChangeListener {

    var nameValid = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        onFocusChangeListener = this
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        background = ContextCompat.getDrawable(context, R.drawable.bgedittext)
    }

    override fun onFocusChange(v: View?, focus: Boolean) {
        if (!focus) {
            onValidateName()
        }
    }

    private fun onValidateName() {
        nameValid = text.toString().trim().isNotEmpty()
        error = if (!nameValid){
            resources.getString(R.string.emptyName)
        } else {
            null
        }
    }
}