package com.example.sebastian.kotlin_utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/*
Values
 */
const val TYPE_HEADER = 0
const val TYPE_ITEM = 1

/*
System
 */
fun Long.delayMillis(block: () -> Unit) {
    Handler().postDelayed({
        block()
    }, this)
}

/*
Date
 */
fun Date.getString(separator: String = "-",
                   parseHours: Boolean = false,
                   hourSeparator: String = ":") = with(this) {
    val format = SimpleDateFormat("dd${separator}MM${separator}YYYY"
            .let {
                if (parseHours) {
                    "$it mm${hourSeparator}HH${hourSeparator}ss"
                } else it
            }, Locale.getDefault())
    format.format(this) ?: ""
}

fun Long.toDate() = Date(this)

fun Long.toStringDate(separator: String = "-",
                      parseHours: Boolean = false,
                      hourSeparator: String = ":") = toDate()
        .getString(separator, parseHours, hourSeparator)

/*
Log
 */
fun String.log(tag: String = "MyLog") {
    Log.i(tag, this)
}

/*
Toast
 */
fun String.toast(context: Context, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(context, this, length).show()

/*
String
 */
fun Int.string(context: Context) = context.getString(this) ?: ""

/*
Drawable
 */
fun Int.drawable(context: Context) = ContextCompat.getDrawable(context, this)

/*
Color
 */
fun Int.color(context: Context) = ContextCompat.getColor(context, this) ?: Color.BLACK

fun String.toIntColor() = Color.parseColor(this)

fun Int.setAlpha(alpha: Int): Int? {
    return try {
        val r = Color.red(this)
        val g = Color.green(this)
        val b = Color.blue(this)
        Color.argb(Math.abs(alpha).let { if (it > 255) 255 else it }, r, g, b)
    } catch (e: RuntimeException) {
        null
    }
}

/*
ViewPager
 */
fun ViewPager.goLeft(smooth: Boolean = true) = setCurrentItem(currentItem - 1, smooth)

fun ViewPager.goRight(smooth: Boolean = true) = setCurrentItem(currentItem + 1, smooth)

/*
RecyclerView
 */
fun RecyclerView.basicSetup(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null, divider: Boolean = false,
                            block: ((RecyclerView) -> Unit)? = null) {
    val lm = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    itemAnimator = DefaultItemAnimator()
    layoutManager = lm
    adapter?.let { this.adapter = it }
    if (divider)
        addItemDecoration(DividerItemDecoration(context, lm.orientation))
    block?.invoke(this)
}

/*
Async
 */
fun doAsync(block: () -> Unit) {
    MyAsyncTask(block).execute()
}

class MyAsyncTask(private val block: () -> Unit) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg p0: Unit?) {
        block()
    }
}

/*
Fragment
 */
fun AppCompatActivity.addFragment(vararg fragments: Fragment,
                                  container: Int,
                                  replace: Boolean = false,
                                  stackTag: String? = null) {
    with(supportFragmentManager.beginTransaction()) {
        fragments.forEach { fragment ->
            if (!replace) {
                add(container, fragment)
                stackTag?.let { addToBackStack(it) }
            } else replace(container, fragment)
        }
        commit()
    }
}


/*
Activity
 */
fun <T : Activity> Context.goTo(clazz: Class<T>, intent: Intent? = null) {
    startActivity(intent ?: Intent(this, clazz))
}

/*
Metrics
 */
fun Float.pxToDp(context: Context) =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this,
                context.resources.displayMetrics
        )

fun Float.dpToPx(context: Context) =
        TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                this,
                context.resources.displayMetrics)

/*
Preferences
 */
fun String.getPrefsValue(context: Context) = PreferenceManager.getDefaultSharedPreferences(context).all[this]

/*
View
 */
fun Int.inflate(context: Context, root: ViewGroup? = null, attachToRoot: Boolean = false) =
        LayoutInflater.from(context).inflate(this, root, attachToRoot)


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide(gone: Boolean = false) {
    visibility = View.INVISIBLE
}

fun AppCompatActivity.setToolbar(toolbarResId: Int, title: String? = null, displayHome: Boolean = false): Toolbar? {
    return findViewById<Toolbar>(toolbarResId)?.let { toolbar ->
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            this.title = title
            setDisplayHomeAsUpEnabled(displayHome)
        }
        toolbar
    }
}

/*
Bitmap
 */

fun Bitmap.toBase64String(quality: Int = 100, format: Bitmap.CompressFormat) = let {
    val byteArrayOutStream = ByteArrayOutputStream()
    compress(format, quality, byteArrayOutStream)
    val bitmapBytes = byteArrayOutStream.toByteArray()
    Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
}

fun String.base64StringToBitmap(): Bitmap? {
    val byteArray = Base64.decode(this, Base64.DEFAULT)
    val stream = ByteArrayInputStream(byteArray)
    return BitmapFactory.decodeStream(stream)
}
