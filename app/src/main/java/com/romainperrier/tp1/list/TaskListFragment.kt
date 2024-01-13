package com.romainperrier.tp1.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.romainperrier.tp1.R
import com.romainperrier.tp1.data.API
import com.romainperrier.tp1.databinding.FragmentTaskListBinding
import com.romainperrier.tp1.detail.DetailActivity
import com.romainperrier.tp1.user.UserActivity
import kotlinx.coroutines.launch

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


    val adapterListener: TaskListListener = object : TaskListListener {
        override fun onClickDelete(it: Task) {
            viewModel.remove(it)
        }

        override fun onClickEdit(it: Task) {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("task", it)
            }
            editTask.launch(intent)
        }
    }
    private val adapter = TaskListAdapter(adapterListener)

    val createTask =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            /*val task = result.data?.getSerializableExtra("task") as Task?
            taskList = taskList + task!!
            counter++
            adapter.submitList(taskList)*/
            Log.d("TaskListFragment", "createTask" + result.data?.getSerializableExtra("task"))
            viewModel.add(result.data?.getSerializableExtra("task") as Task)
        }

    val editTask =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            val task = result.data?.getSerializableExtra("task") as Task?
//            if (task != null) {
//                taskList = taskList.map { if (it.id == task.id) task else it }
//                adapter.submitList(taskList)
//            }
            viewModel.edit(result.data?.getSerializableExtra("task") as Task)
        }

    private val viewModel: TaskListViewModel by viewModels()


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("taskList", taskList as java.io.Serializable)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskList =
            savedInstanceState?.getSerializable("taskList") as? MutableList<Task> ?: mutableListOf()


        _binding?.floatingActionButton?.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent)
        }


        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est exécutée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                _binding?.taskList?.adapter = adapter
                taskList = newList
                adapter.submitList(taskList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ici on ne va pas gérer les cas d'erreur donc on force le crash avec "!!"
        lifecycleScope.launch {
            val user = API.userWebService.fetchUser().body()!!
            _binding?.userTextView?.text = user.name
            _binding?.userImageView?.load("https://goo.gl/gEgYUd")

            _binding?.userImageView?.setOnClickListener {
                val intent = Intent(context, UserActivity::class.java)
                startActivity(intent)
            }
            _binding?.userImageView?.load(user.avatar) {
                error(R.drawable.ic_launcher_background) // image par défaut en cas d'erreur
            }
            viewModel.refresh() // on demande de rafraîchir les données sans attendre le retour directement
        }
    }


}
