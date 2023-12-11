package com.romainperrier.tp1.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.romainperrier.tp1.R
import com.romainperrier.tp1.databinding.FragmentTaskListBinding
import com.romainperrier.tp1.detail.DetailActivity
import java.util.UUID

class TaskListFragment : Fragment() {
    private val adapter = TaskListAdapter()
    private var taskList = emptyList<Task>()
    private var counter = 0
    private var _binding: FragmentTaskListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task?
        taskList = taskList + task!!
        counter++
        adapter.submitList(taskList)
    }
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
            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent)
        }

        adapter.submitList(taskList)

        adapter.onClickDelete = {
            taskList = taskList.filter { task -> task.id != it.id }
            adapter.submitList(taskList)
        }
    }
}
