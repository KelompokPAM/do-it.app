package com.do_it.do_it.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.do_it.do_it.databinding.FragmentHomeBinding
import com.do_it.do_it.fragments.utils.ToAdapter
import com.do_it.do_it.fragments.utils.ToData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class HomeFragment : Fragment(), AddTodoPopupFragment.DialogNextButtonClickListeners,
    ToAdapter.ToAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popupFragment: AddTodoPopupFragment? = null
    private lateinit var adapter: ToAdapter
    private lateinit var mlist: MutableList<ToData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvent()
    }

    private fun registerEvent() {
        if(popupFragment != null){
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
        }
        binding.addBtnHome.setOnClickListener {
            popupFragment = AddTodoPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
                AddTodoPopupFragment.TAG
            )
        }
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks")
            .child(auth.currentUser?.uid.toString())
        binding.recylerView.setHasFixedSize(true)
        binding.recylerView.layoutManager = LinearLayoutManager(context)
        mlist = mutableListOf()
        adapter = ToAdapter(mlist)
        adapter.setlistener(this)
        binding.recylerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
                for (taskSnapshot in snapshot.children) {
                    val updatedTime = taskSnapshot.child("updatedTime").getValue(String::class.java)
                    val createdTime = taskSnapshot.child("createdTime").getValue(String::class.java)
                    val todoTask = taskSnapshot.key?.let {
                        ToData(it,
                            taskSnapshot.child("title").getValue(String::class.java).toString(),
                            taskSnapshot.child("task").getValue(String::class.java).toString(),
                            (updatedTime?:createdTime).toString()
                        )
                    }
                    if (todoTask != null) {
                        mlist.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun dateNow(): String {
        val currentCalendar = Calendar.getInstance()
        val year = currentCalendar.get(Calendar.YEAR)
        val month =
            currentCalendar.get(Calendar.MONTH) + 1  // Perhatikan bahwa bulan dimulai dari 0
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = currentCalendar.get(Calendar.MINUTE)
        val second = currentCalendar.get(Calendar.SECOND)
        return String.format(
            "%04d-%02d-%02d %02d:%02d:%02d",
            year,
            month,
            day,
            hour,
            minute,
            second
        )
    }
    override fun onSaveTask(todo: String, title:String, todoEt1: TextInputEditText, todoEt2: TextInputEditText) {

        val taskData = mapOf("title" to title, "task" to todo, "createdTime" to dateNow())
        databaseRef.push().setValue(taskData).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Firebase", "Task saved successfully")
                Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT).show()
                todoEt1.text = null
                todoEt2.text = null
            } else {
                Log.d("Firebase", "Failed to save Task")
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popupFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(toDoData: ToData) {
        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(toDoData: ToData) {
        if(popupFragment != null){
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
        }
        popupFragment = AddTodoPopupFragment.newInstance(toDoData.taskId, toDoData.title, toDoData.task, toDoData.date)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager, AddTodoPopupFragment.TAG)
    }

    override fun onUpdateTask(
        toData: ToData,
        todo: String,title:String,
        todoEt1: TextInputEditText,
        todoEt2: TextInputEditText
    ) {

        val taskData = mapOf("title" to title, "task" to todo, "updatedTime" to dateNow() )
        databaseRef.child(toData.taskId).updateChildren(taskData).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            todoEt1.text = null
            todoEt2.text = null
            popupFragment!!.dismiss()
        }
    }
}
