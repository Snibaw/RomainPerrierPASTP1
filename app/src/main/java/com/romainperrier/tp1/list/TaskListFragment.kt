package com.romainperrier.tp1.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.romainperrier.tp1.R
import com.romainperrier.tp1.databinding.FragmentTaskListBinding
import java.util.UUID

class TaskListFragment : Fragment() {
    private val adapter = TaskListAdapter()
    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )
    private var _binding: FragmentTaskListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater,container, false);
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.taskList?.adapter = adapter

        _binding?.floatingActionButton?.setOnClickListener {
            val newTask = Task(id = UUID.randomUUID().toString(), title = "New task")
            taskList = taskList + newTask
            adapter.submitList(taskList)
        }

        adapter.submitList(taskList)
    }
}
