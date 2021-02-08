package com.sinx.todo.ui.table

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sinx.todo.R
import com.sinx.todo.databinding.ItemTaskBinding

class TaskAdapter(private val onItemChecked: (Int, Boolean) -> Unit) : ListAdapter<TaskItem, TaskAdapter.TaskHolder>(TaskItem) {

    class TaskHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun render(item: TaskItem, onItemChecked: (Int, Boolean) -> Unit) {
            with(binding) {
                titleTask.paintFlags =
                if (item.checked) {
                        titleTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    Paint.ANTI_ALIAS_FLAG
                }
                check.setOnCheckedChangeListener(null)
                check.isChecked = item.checked
                check.setOnCheckedChangeListener { _, checked ->
                    onItemChecked(item.id, checked)
                }
                titleTask.text = item.text
                val colorTitle = ContextCompat.getColor(
                    titleTask.context,
                    if (item.checked) R.color.colorBlueLight else R.color.colorBlueDark
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
        holder.render(getItem(position), onItemChecked)
    }
}

