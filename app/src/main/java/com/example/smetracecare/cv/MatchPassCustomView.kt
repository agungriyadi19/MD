package com.example.smetracecare.cv

import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.util.AttributeSet
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.smetracecare.R

class MatchPassCustomView: AppCompatEditText, View.OnFocusChangeListener {

    var passwordConfirmValid = false

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
        transformationMethod = PasswordTransformationMethod.getInstance()
        background = ContextCompat.getDrawable(context, R.drawable.textbox_background)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onValidatePass()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun onValidatePass() {
        val passConfirm = (parent as ViewGroup).findViewById<PasswordCustomView>(R.id.ed_register_password).text.toString().trim()
        val pass = text.toString().trim()

        passwordConfirmValid = pass.length >= 8 && pass == passConfirm
        error = if (!passwordConfirmValid) {
            resources.getString(R.string.noMatchPass)
        } else {
            null
        }
    }

    override fun onFocusChange(p0: View?, focused: Boolean) {
        if (!focused) {
            onValidatePass()
        }
    }
}