package com.do_it.do_it.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.do_it.do_it.R
import com.do_it.do_it.databinding.FragmentAddTodoPopupBinding
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
            val todoTask = binding.todoEt.text.toString()
            if (todoTask.isNotEmpty()){
                Toast.makeText(context, "Its working", Toast.LENGTH_SHORT)
                listener.onSaveTask(todoTask, binding.todoEt)
            }else {
                Toast.makeText(context, "Write your tasks", Toast.LENGTH_SHORT).show()
            }
        }
        binding.todoClose.setOnClickListener{
            dismiss()
        }
    }

    interface DialogNextButtonClickListeners{
        fun onSaveTask(todo: String, todoEt: TextInputEditText)
    }

}