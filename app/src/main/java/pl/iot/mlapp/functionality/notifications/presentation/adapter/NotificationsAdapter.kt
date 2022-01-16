package pl.iot.mlapp.functionality.notifications.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.iot.mlapp.databinding.ItemNotificationBinding
import pl.iot.mlapp.functionality.notifications.presentation.model.NotificationUi

class NotificationsAdapter
    : ListAdapter<NotificationUi, NotificationsAdapter.NotificationViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        with(holder.binding) {
            val item = getItem(position)

            ImageViewCompat.setImageTintList(
                notificationIcon, ColorStateList.valueOf(
                    ContextCompat.getColor(notificationIcon.context, item.notificationColor)
                )
            )
            notificationText.text = item.message
        }
    }

    class NotificationViewHolder(
        val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private class DiffCallback : DiffUtil.ItemCallback<NotificationUi>() {

        override fun areItemsTheSame(oldItem: NotificationUi, newItem: NotificationUi) = false

        override fun areContentsTheSame(oldItem: NotificationUi, newItem: NotificationUi) = false
    }

}