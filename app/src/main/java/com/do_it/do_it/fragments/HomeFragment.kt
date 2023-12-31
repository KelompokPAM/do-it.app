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

class HomeFragment : Fragment(), AddTodoPopupFragment.DialogNextButtonClickListeners,
    ToAdapter.ToAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popupFragment: AddTodoPopupFragment
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
        binding.addBtnHome.setOnClickListener {
            popupFragment = AddTodoPopupFragment()
            popupFragment.setListener(this)
            popupFragment.show(
                childFragmentManager,
                "AddTodoPopupFragment"
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
                    val todoTask = taskSnapshot.key?.let {
                        ToData(it, taskSnapshot.child("title").getValue(String::class.java).toString())
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

    override fun onSaveTask(todo: String, title:String, todoEt1: TextInputEditText, todoEt2: TextInputEditText) {
        val taskData = mapOf("title" to title, "task" to todo)
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
            popupFragment.dismiss()
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
        // Implement edit functionality here if needed
    }
}
