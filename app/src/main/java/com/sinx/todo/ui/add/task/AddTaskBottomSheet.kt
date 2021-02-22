package com.sinx.todo.ui.add.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sinx.todo.R
import com.sinx.todo.core.bottomSheetViewBinding
import com.sinx.todo.databinding.FragmentAddTaskBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull

class AddTaskBottomSheet : BottomSheetDialogFragment() {

    private val binding: FragmentAddTaskBinding by bottomSheetViewBinding(FragmentAddTaskBinding::bind)
    private val viewModel by viewModels<AddTaskViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.close.setOnClickListener {
//            viewModel.dispatch(AddTaskMsg.OnClosePressed)
        }
        binding.create.setOnClickListener {
//            viewModel.dispatch(AddTaskMsg.OnCreatePressed(binding.taskName.text.toString()))
        }
        lifecycleScope.launchWhenCreated {
//            viewModel.viewAction().filterNotNull().collect(::doAction)
        }
        lifecycleScope.launchWhenStarted {
//            viewModel.viewStates().filterNotNull().collect(::render)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    private fun render(state: AddTaskViewState) {

    }

    private fun doAction(action: AddTaskAction) {
        when (action) {
            AddTaskAction.CloseDialog -> dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}