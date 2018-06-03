package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.DialogFragment;
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.progress_dialog.*
import android.os.Handler
import com.privategallery.akscorp.privategalleryandroid.R


val LOAD_DIALOG_TAG = "LOAD_DIALOG_TAG"

class LoadDialog : DialogFragment() {

    override fun onStart() {
        super.onStart()

        dialog.setCancelable(false)

        if (dialog != null) {
            dialog.window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun delayDismiss() {
        Handler().postDelayed({
            dialog.dismiss()
        }, 1500)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        super.onCreateView(inflater, parent, state)

        return activity!!.layoutInflater.inflate(R.layout.progress_dialog, null, false)
    }


}

val PROGRESS_BROADCAST_RECEIVER_TAG = "PROGRESS_BROADCAST_RECEIVER_TAG"
val CURRENT_PROGRESS_BROADCAST_RECEIVER = "CURRENT_PROGRESS_BROADCAST_RECEIVER"

class ProgressBroadcastReceiver(val dialog: LoadDialog) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val progress = intent.getIntExtra(CURRENT_PROGRESS_BROADCAST_RECEIVER, 100)
        if (dialog.dialog != null) {
            dialog.dialog.progressBar.progress = progress

            if (progress == 100) {
                val animAccelerate = AnimationUtils.loadAnimation(context,
                    R.anim.done_anim
                )
                dialog.dialog.progress_status.text = context.getString(R.string.done)
                dialog.dialog.done_point.startAnimation(animAccelerate)
            }
        }
    }
}