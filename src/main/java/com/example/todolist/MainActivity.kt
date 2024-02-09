package com.example.todolist
import DatabaseHelper
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var taskEditText: EditText
    private lateinit var addButton: Button
    private lateinit var taskListView: ListView

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var taskAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskEditText = findViewById(R.id.taskEditText)
        addButton = findViewById(R.id.addButton)
        taskListView = findViewById(R.id.taskListView)

        dbHelper = DatabaseHelper(this)
        taskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dbHelper.getAllTasks())
        taskListView.adapter = taskAdapter

        addButton.setOnClickListener {
            val taskDescription = taskEditText.text.toString()
            if (taskDescription.isNotBlank()) {
                dbHelper.insertTask(taskDescription)
                updateTaskList()
                taskEditText.text.clear()
            }
        }

        taskListView.setOnItemClickListener { _, _, position, _ ->
            val taskId = dbHelper.getTaskId(position)
            dbHelper.markTaskAsCompleted(taskId)
            updateTaskList()
        }

        taskListView.setOnItemLongClickListener { _, _, position, _ ->
            val taskId = dbHelper.getTaskId(position)
            dbHelper.deleteTask(taskId)
            updateTaskList()
            true
        }
    }

    private fun updateTaskList() {
        taskAdapter.clear()
        taskAdapter.addAll(dbHelper.getAllTasks())
        taskAdapter.notifyDataSetChanged()
    }
}
