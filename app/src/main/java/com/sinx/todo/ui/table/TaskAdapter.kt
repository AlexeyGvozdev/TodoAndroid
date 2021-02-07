package com.sinx.todo.ui.table

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sinx.todo.R
import com.sinx.todo.databinding.ItemTaskBinding

class TaskAdapter : ListAdapter<TaskItem, TaskAdapter.TaskHolder>(TaskItem) {

    class TaskHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun render(item: TaskItem) {
            with(binding) {
                if (item.checked) {
                    titleTask.paintFlags = titleTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                titleTask.text = item.text
                check.isChecked = item.checked
                val colorTitle = ContextCompat.getColor(
                    titleTask.context,
                    if (item.checked) R.color.colorMainBlue else R.color.colorBlueDark
                )
                titleTask.setTextColor(colorTitle)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        return TaskHolder(
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.render(getItem(position))
    }
}

