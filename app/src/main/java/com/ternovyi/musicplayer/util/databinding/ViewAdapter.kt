package com.ternovyi.musicplayer.util.databinding

import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.File

object ViewAdapter {

    private const val ATTR_IV_SRC = "webSrc"
    private const val ATTR_LOCAL_URI = "localUriSrc"
    private const val ATTR_ET_ERROR = "error"
    private const val ATTR_EDITABLE = "editable"
    private const val ATTR_VISIBILITY = "visibility"
    private const val ATTR_VISIBILITY_GONE = "visibilityGone"
    private const val ATTR_INVISIBILITY_GONE = "inVisibilityGone"

    @BindingAdapter(value = [ATTR_IV_SRC, ATTR_LOCAL_URI], requireAll = false)
    fun bindUriSrc(imageView: ImageView, path: Any?, uri: Uri?) {
        uri?.let {
            Glide.with(imageView.context)
                .load(File(it.path))
                .into(imageView)
            return
        }

        if (path == null) {
            return
        }

        (path as? Uri)?.let {
            Glide.with(imageView.context)
                .load(it)
                .into(imageView)
        }

        (path as? String)?.let {
            Glide.with(imageView.context)
                .load(it)
                .into(imageView)
        }
    }

    @BindingAdapter(value = [ATTR_ET_ERROR], requireAll = false)
    fun bindError(editText: EditText, string: String?) {
        editText.error = string
    }

    @BindingAdapter(value = [ATTR_EDITABLE], requireAll = false)
    fun bindEditable(view: View, isEditable: Boolean) {
        view.isClickable = isEditable
        view.isFocusable = isEditable
        view.isFocusableInTouchMode = isEditable

        (view as? EditText)?.let {
            it.isCursorVisible = isEditable
        }
    }

    @BindingAdapter(value = [ATTR_VISIBILITY], requireAll = false)
    fun bindVisibility(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    @BindingAdapter(value = [ATTR_VISIBILITY_GONE], requireAll = false)
    fun bindVisibilityGone(view: View, ob: Any?) {
        if (ob == null) {
            view.visibility = View.GONE
        } else if ((ob as String?).isNullOrEmpty()) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }


    @BindingAdapter(value = [ATTR_INVISIBILITY_GONE], requireAll = false)
    fun bindInVisibilityGone(view: View, ob: Any?) {
        view.visibility = if (ob == null) View.VISIBLE else View.GONE
    }
}