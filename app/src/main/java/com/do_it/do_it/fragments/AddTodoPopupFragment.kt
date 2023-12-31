package com.do_it.do_it.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.do_it.do_it.databinding.FragmentAddTodoPopupBinding
import com.do_it.do_it.fragments.utils.ToData
import com.google.android.material.textfield.TextInputEditText

class AddTodoPopupFragment : DialogFragment() {

    private lateinit var binding: FragmentAddTodoPopupBinding
    private lateinit var listener: DialogNextButtonClickListeners
    private var toData: ToData?=null

    fun setListener(listener: DialogNextButtonClickListeners){
        this.listener = listener
    }

    companion object{
        const val TAG = "AddToDoPopupFragment"
        @JvmStatic
        fun newInstance(taskId: String, title: String, task: String) = AddTodoPopupFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("title", title)
                putString("task", task)
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTodoPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments != null){
            toData = ToData(arguments?.getString("taskId").toString(),
                arguments?.getString("title").toString(),
                arguments?.getString("task").toString())
            binding.todoEt1.setText(toData?.title)
            binding.todoEt2.setText(toData?.task)
        }
        registerEvent()
    }

    private fun registerEvent(){
        binding.todoNextBtn.setOnClickListener{
            val titleTask = binding.todoEt1.text.toString()
            val todoTask = binding.todoEt2.text.toString()

            if (todoTask.isNotEmpty() && titleTask.isNotEmpty()){
                if(toData == null){
                    listener?.onSaveTask(todoTask, titleTask, binding.todoEt1, binding.todoEt2)
                }else{
                    toData?.task = todoTask
                    listener.onUpdateTask(toData!!,todoTask, titleTask, binding.todoEt1, binding.todoEt2)
                }
            }else {
                Toast.makeText(context, "Failed, Write your tasks", Toast.LENGTH_SHORT).show()
            }
        }
        binding.todoClose.setOnClickListener{
            dismiss()
        }
    }

    interface DialogNextButtonClickListeners{
        fun onSaveTask(todo: String,title:String, todoEt1: TextInputEditText, todoEt2: TextInputEditText)
        fun onDeleteTaskBtnClicked(toDoData: ToData)
        fun onEditTaskBtnClicked(toDoData: ToData)
        fun onUpdateTask(toData: ToData,todo: String,title:String, todoEt1: TextInputEditText, todoEt2: TextInputEditText)
    }

}