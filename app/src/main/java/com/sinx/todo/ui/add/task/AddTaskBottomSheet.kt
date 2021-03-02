package com.sinx.todo.ui.add.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sinx.todo.R
import com.sinx.todo.base.Tea
import com.sinx.todo.base.TeaVM
import com.sinx.todo.core.bottomSheetViewBinding
import com.sinx.todo.core.initViewModel
import com.sinx.todo.databinding.FragmentAddTaskBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull

class AddTaskBottomSheet(tea: AddTaskTea) : BottomSheetDialogFragment() {

    private val binding: FragmentAddTaskBinding by bottomSheetViewBinding(FragmentAddTaskBinding::bind)
    private val teaVM : AddTaskTeaVM by initViewModel(tea)

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
            teaVM.dispatch(AddTaskMsg.OnClosePressed)
        }
        binding.create.setOnClickListener {
            teaVM.dispatch(AddTaskMsg.OnCreatePressed(binding.taskName.text.toString()))
        }
        lifecycleScope.launchWhenCreated {
            teaVM.viewAction().filterNotNull().collect(::doAction)
        }
        lifecycleScope.launchWhenStarted {
            teaVM.viewStates().filterNotNull().collect(::render)
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
}