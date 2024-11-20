package com.xbcad.xbcad7319_physiotherapyapp.ui
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter
import android.widget.TextView;
import com.xbcad.xbcad7319_physiotherapyapp.R


class NotificationAdapter(
    context: Context,
    private val notifications: MutableList<Notification>
) : ArrayAdapter<Notification>(context, 0, notifications) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val notification = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false)

        val messageTextView: TextView = view.findViewById(R.id.tvNotificationMessage)
        val timeTextView: TextView = view.findViewById(R.id.tvNotificationTime)

        messageTextView.text = notification.message
        timeTextView.text = formatTime(notification.time)

        return view
    }

    private fun formatTime(time: String): String {
        // Format the time string to a more readable format if necessary
        return time // Update this to your preferred format
    }
}
