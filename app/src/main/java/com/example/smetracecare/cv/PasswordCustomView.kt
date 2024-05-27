package com.example.smetracecare.cv

import android.view.View
import android.view.MotionEvent
import android.util.AttributeSet
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatEditText
import android.text.method.PasswordTransformationMethod
import com.example.smetracecare.R

class PasswordCustomView : AppCompatEditText, View.OnTouchListener{

    var passwordValid = false

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

    private fun onValidatePass() {
        passwordValid = (text?.length ?: 0) >= 8
        error = if (!passwordValid) {
            resources.getString(R.string.eightCharPass)
        } else {
            null
        }
    }

    private fun init() {
        background = ContextCompat.getDrawable(context, R.drawable.textbox_background)
        transformationMethod = PasswordTransformationMethod.getInstance()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onValidatePass()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) {
            onValidatePass()
        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }
}