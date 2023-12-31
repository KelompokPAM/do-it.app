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

    fun setListener(listener: DialogNextButtonClickListeners){
        this.listener = listener
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

        registerEvent()
    }

    private fun registerEvent(){
        binding.todoNextBtn.setOnClickListener{
            val titleTask = binding.todoEt1.text.toString()
            val todoTask = binding.todoEt2.text.toString()

            if (todoTask.isNotEmpty()){
                listener?.onSaveTask(todoTask, titleTask, binding.todoEt1, binding.todoEt2)
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
    }

}