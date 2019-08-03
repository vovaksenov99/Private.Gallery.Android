package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.progress_dialog.done_point
import kotlinx.android.synthetic.main.progress_dialog.progressBar
import kotlinx.android.synthetic.main.progress_dialog.progress_dialog_root
import kotlinx.android.synthetic.main.progress_dialog.progress_status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.runOnUiThread

val LOAD_DIALOG_TAG = "LOAD_DIALOG_TAG"

class LoadDialog : DialogFragment() {
    var progressBarShow: Boolean = true
    var currentReceiver: ProgressBroadcastReceiver? = null

    override fun onStart() {
        super.onStart()

        dialog.setCancelable(false)

        if (!progressBarShow)
            (dialog.progress_dialog_root as ConstraintLayout).removeView(dialog.progressBar)

        if (dialog != null) {
            dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun delayDismiss(action: () -> Unit = {}) {
        GlobalScope.launch(Dispatchers.Main) {
            Handler().postDelayed({
                try {
                    dialog.dismiss()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                action()
            }, 1500)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)
        progressBroadcastReceiverInit()
        return activity!!.layoutInflater.inflate(R.layout.progress_dialog, null)
    }

    private fun progressBroadcastReceiverInit() {
        val intentFilter = IntentFilter(PROGRESS_BROADCAST_RECEIVER_TAG)
        currentReceiver = ProgressBroadcastReceiver(this)
        currentReceiver?.register(activity!!, intentFilter)
    }

    fun sentProgressToReceiver(progress: Int) {
        val intent = Intent()
        intent.action = PROGRESS_BROADCAST_RECEIVER_TAG
        intent.putExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, progress)
        activity?.sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentReceiver?.unregister(activity!!)
        dismiss()
    }
}

const val PROGRESS_BROADCAST_RECEIVER_TAG = "PROGRESS_BROADCAST_RECEIVER_TAG"
const val CURRENT_PROGRESS_BROADCAST_RECEIVER = "CURRENT_PROGRESS_BROADCAST_RECEIVER"

class ProgressBroadcastReceiver(val dialog: LoadDialog) : BroadcastReceiver() {
    var isRegistered: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {
        context.runOnUiThread {
            val progress = intent.getIntExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, 100)
            if (dialog.dialog != null) {
                if (dialog.dialog.progressBar != null) {
                    dialog.dialog.progressBar.progress = progress

                    if (progress == 100) {
                        val animAccelerate = AnimationUtils.loadAnimation(
                                context,
                                R.anim.done_anim
                        )
                        dialog.dialog.progress_status.text = context.getString(R.string.done)
                        dialog.dialog.done_point.startAnimation(animAccelerate)
                    }
                }
            }
        }
    }

    fun register(context: Context, filter: IntentFilter): Intent? {
        try {
            return if (!isRegistered)
                context.registerReceiver(this, filter)
            else
                null
        } finally {
            isRegistered = true
        }
    }

    fun unregister(context: Context): Boolean {
        return isRegistered && unregisterInternal(context)
    }

    private fun unregisterInternal(context: Context): Boolean {
        context.unregisterReceiver(this)
        isRegistered = false
        return true
    }

}