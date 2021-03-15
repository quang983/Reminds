package com.example.reminds.utils

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.LayoutRes
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.layout_time.view.*
import kotlin.math.max
import kotlin.math.min

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.setGone(gone: Boolean) {
    this.visibility = if (gone) View.GONE else View.VISIBLE
}

fun View.setVisible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.setInvisible(isInvisible: Boolean) {
    this.visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.visibleOrInvisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isGone() = visibility == View.GONE

fun View.isInVisible() = visibility == View.INVISIBLE

fun View.resize(width: Int = -3, height: Int = -3, weight: Float = 0f) {
    var update = false

    val params = layoutParams as ViewGroup.LayoutParams

    if (params.width != width && width >= -2) {
        params.width = width
        update = true
    }

    if (params is LinearLayout.LayoutParams && params.weight != weight) {
        params.weight = weight
        update = true
    }

    if (params.height != height && height >= -2) {
        params.height = height
        update = true
    }
    if (update) {
        layoutParams = layoutParams
    }
}

fun View.getHeightVisible(): Int {
    val r = getLocalVisibleRect()
    return r.bottom - r.top
}

fun View.getLocalVisibleRect(): Rect {
    val r = Rect()
    getLocalVisibleRect(r)
    return r
}

fun ViewGroup.inflate(@LayoutRes l: Int): View {
    return LayoutInflater.from(context).inflate(l, this, false)
}

/**
 * thiết lập icon cho textview
 */
/*fun TextView.setDrawable(@DrawableRes drawableRes: Int?, location: Location) {
    setDrawable(
        if (drawableRes != null) Utils.getVectorDrawable(context, drawableRes) else null,
        location
    )
}*/

fun TextView.toggleText(data: String?) {
    when (data.isNullOrEmpty()) {
        true -> this.setVisible(false)
        false -> {
            this.setVisible(true)
            this.text = data
        }
    }
}

/**
 * thiết lập icon cho textview
 */
fun TextView.setDrawable(drawable: Drawable?, location: Location) {
    when (location) {
        Location.TOP -> {
            setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        }
        Location.BOTTOM -> {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
        }
        Location.RIGHT -> {
            setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        }
        Location.LEFT -> {
            setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }
}

/**
 * vị trí thiết lập icon cho app
 */
enum class Location {
    TOP, BOTTOM, RIGHT, LEFT
}

fun EditText.setTextChangedListener(
    onlyWhenFocused: Boolean = false,
    onChanged: (EditText) -> Unit
): TextWatcher {
    val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (onlyWhenFocused && !isFocusable) return
            onChanged(this@setTextChangedListener)
        }
    }
    addTextChangedListener(textWatcher)
    return textWatcher
}

fun EditText.setOnDoneClickedListener(onDoneClicked: (EditText) -> Unit) {
    this.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onDoneClicked(this)
            return@setOnEditorActionListener true
        } else {
            return@setOnEditorActionListener false
        }
    }
}

fun TextView.setTextHtml(text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY))
    } else {
        setText(Html.fromHtml(text))
    }
}

fun View.setOnClickListenerBlock(block: () -> Unit) {
    setOnClickListener {
        isEnabled = false
        postDelayed({
            isEnabled = true
        }, 200)
        block()
    }
}
fun TimePicker.getTime(): Pair<Int, Int> {
    return if (Build.VERSION.SDK_INT >= 23) {
        Pair(this.hour, this.minute)
    } else {
        Pair(this.currentHour, this.currentMinute)
    }
}

fun TimePicker.setTime(time: Pair<Int, Int>) {
    if (Build.VERSION.SDK_INT >= 23) {
        this.time.hour = time.first
        this.time.minute = time.second
    } else {
        this.time.currentHour = time.first
        this.time.currentMinute = time.second
    }
}

fun EditText.setMultiLineCapSentencesAndDoneAction() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
}

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun TextView.underLine(){
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun TextView.removeUnderLine(){
    paintFlags = 0
}


inline fun EditText.onChangeFormat(crossinline format: (text: String) -> String) {
    var textWatcher: TextWatcher? = null
    textWatcher = doAfterTextChanged {
        this@onChangeFormat.removeTextChangedListener(textWatcher)
        checkDiffAndSetText(format(text.toString()))
        this@onChangeFormat.addTextChangedListener(textWatcher)
    }
}

inline fun EditText.onUserEnter(crossinline action: (text: String) -> Unit) {
    var oldText = ""
    doAfterTextChanged {
        error = error
        val newText = text?.toString() ?: ""
        if (oldText != newText && isFocused) {
            action.invoke(newText)
        }
        oldText = newText
    }
}

fun EditText.setFocus(focus: Boolean) {
    if (focus) {
        requestFocus()
    } else {
        clearFocus()
    }
}

fun EditText.checkDiffAndSetText(newText: String?) {
    newText?.let { _newText ->
        val currentText = this.text.toString()
        if (_newText != currentText) {
            val currentSelected = selectionEnd
            val newSelected = min(max(0, newText.length - currentText.length + currentSelected), newText.length)

            this.setText(newText)
            if (this.isFocused) {
                this.setSelection(newSelected)
            }
        }
    }
}

fun EditText.checkDiffAndSetText2(newText: String?) {
    newText?.let { _newText ->
        val currentText = this.text.toString()
        if (_newText != currentText) {
            val currentSelected = selectionEnd
            val newSelected = min(max(0, newText.length - currentText.length + currentSelected), newText.length - 1)

            this.setText(newText)
            if (this.isFocused) {
                this.setSelection(newSelected)
            }
        }
    }
}

