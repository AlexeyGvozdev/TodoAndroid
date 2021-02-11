package com.sinx.todo.ui.table

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sinx.todo.R
import com.sinx.todo.databinding.FragmentTableBinding
import com.sinx.todo.utils.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

class TableFragment : Fragment(R.layout.fragment_table) {

    private val binding by viewBinding(FragmentTableBinding::bind)
    private val taskAdapter = TaskAdapter() { id, checked ->
        viewModel.dispatch(TableMsg.CheckedTask(id, checked))
    }

    private val viewModel: TableViewModel by viewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
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
        binding.addTask.setOnClickListener {
            viewModel.dispatch(TableMsg.AddTaskPressed)
        }
        lifecycleScope.launchWhenCreated {
            viewModel.viewStates().filterNotNull().collect(::render)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.viewAction().filterNotNull().collect(::doAction)
        }

    }

    private fun doAction(action: TableAction) {
        when (action) {
            TableAction.ToAddTask -> findNavController().navigate(R.id.addTaskDialog)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun render(viewState: TableViewState) {
        with(viewState) {
            taskAdapter.submitList(tasks)
        }
    }
}