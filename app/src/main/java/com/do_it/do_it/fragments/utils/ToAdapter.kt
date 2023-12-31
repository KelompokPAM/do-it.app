package com.do_it.do_it.fragments.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.do_it.do_it.databinding.EachDataTaskBinding
import com.do_it.do_it.fragments.HomeFragment

class ToAdapter(private val list:MutableList<ToData>):
    RecyclerView.Adapter<ToAdapter.ToDoViewHolder>(){

    inner class ToDoViewHolder(val binding: EachDataTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding =
            EachDataTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.taskText.text = this.title
                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }
                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }
            }
        }
    }

    private var listener: ToAdapterClicksInterface? = null
    fun setlistener(listener: HomeFragment) {
        this.listener = listener
    }

    interface  ToAdapterClicksInterface {
        fun onDeleteTaskBtnClicked(toDoData: ToData)
        fun onEditTaskBtnClicked(toDoData: ToData)
    }

}
