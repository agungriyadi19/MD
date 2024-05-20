package com.example.smetracecare.cv

import android.view.View
import android.content.Context
import android.text.TextWatcher
import android.text.InputType
import android.text.Editable
import android.util.Patterns
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatEditText
import com.example.smetracecare.R

class EmailCustomView : AppCompatEditText, View.OnFocusChangeListener {

    private lateinit var sameEmail: String
    private var emailTaken = false
    var emailValid = false

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
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onValidateEmail()
                if (emailTaken){
                    onValidateEmailTaken()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onFocusChange(v: View?, focus: Boolean) {
        if (!focus) {
            onValidateEmail()
            if (emailTaken) {
                onValidateEmailTaken()
            }
        }
    }

    private fun onValidateEmail() {
        emailValid = Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches()
        error = if (!emailValid) {
            resources.getString(R.string.wrongEmailFormat)
        } else {
            null
        }
    }
    private fun onValidateEmailTaken() {
        error = if (emailTaken && text.toString().trim() == sameEmail) {
            resources.getString(R.string.emailTaken)
        } else {
            null
        }
    }

    fun setErrorMsg(msg: String, email:String){
        emailTaken = true
        sameEmail = email
        error = if (text.toString().trim() == sameEmail) {
            msg
        } else {
            null
        }
    }
}