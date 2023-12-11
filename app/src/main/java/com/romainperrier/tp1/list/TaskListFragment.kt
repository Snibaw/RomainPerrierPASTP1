package com.romainperrier.tp1.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.romainperrier.tp1.databinding.FragmentTaskListBinding
import com.romainperrier.tp1.detail.DetailActivity

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
}

class TaskListFragment : Fragment() {
    private var taskList = emptyList<Task>()
    private var counter = 0
    private var _binding: FragmentTaskListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    val adapterListener : TaskListListener = object : TaskListListener {
        override fun onClickDelete(it: Task) {
            taskList = taskList.filter { task -> task.id != it.id }
            adapter.submitList(taskList)}
        override fun onClickEdit(it: Task) {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("task", it)
            }
            editTask.launch(intent)}
    }
    private val adapter = TaskListAdapter(adapterListener)

    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task?
        taskList = taskList + task!!
        counter++
        adapter.submitList(taskList)
    }

    val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task?
        if (task != null) {
            taskList = taskList.map { if (it.id == task.id) task else it }
            adapter.submitList(taskList)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("taskList", taskList as ArrayList<Task>)
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

        taskList = savedInstanceState?.getSerializable("taskList") as? MutableList<Task> ?: mutableListOf()

        _binding?.taskList?.adapter = adapter

        _binding?.floatingActionButton?.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent)
        }


        adapter.submitList(taskList)
    }
}
