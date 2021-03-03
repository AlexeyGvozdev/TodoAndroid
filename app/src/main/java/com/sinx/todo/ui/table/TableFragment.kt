package com.sinx.todo.ui.table

import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sinx.todo.R
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.core.initViewModel
import com.sinx.todo.databinding.FragmentTableBinding
import com.sinx.todo.repository.TableRepository
import com.sinx.todo.utils.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

class TableFragment(tea: TableTea) : Fragment(R.layout.fragment_table) {

    private val binding by viewBinding(FragmentTableBinding::bind)
    private val taskAdapter = TaskAdapter() { id, checked ->
        viewModel.dispatch(TableMsg.CheckedTask(id, checked))
    }

    private val viewModel by initViewModel(tea)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listTask.apply {
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    if (parent.getChildAdapterPosition(view) > 0) {
                        outRect.top = 15.dp
                    }
                }
            })
            adapter = taskAdapter
        }
        binding.toolbar.subtitle = "subtitle"
        binding.addTask.setOnClickListener {
            viewModel.dispatch(TableMsg.AddTaskPressed)
        }
        lifecycleScope.launchWhenCreated {
            combine(
                viewModel.viewStates().filterNotNull().onEach(::render),
                viewModel.viewAction().filterNotNull().onEach(::doAction)
            ) { _, _ -> }.collect()
        }
    }

    private fun doAction(action: TableAction) {
        when (action) {
            is TableAction.ToAddTask -> findNavController().navigate(R.id.addTaskDialog)
        }
    }

    private fun render(viewState: TableViewState) {
        with(viewState) {
            taskAdapter.submitList(tasks)
            binding.toolbar.subtitle = if (connection) null else getString(R.string.disconnection)
        }
    }
}