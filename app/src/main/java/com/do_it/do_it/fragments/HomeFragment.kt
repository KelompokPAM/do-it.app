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
import com.do_it.do_it.R
import com.do_it.do_it.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
class HomeFragment : Fragment(), AddTodoPopupFragment.DialogNextButtonClickListeners {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popupFragment: AddTodoPopupFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvent()
    }

    private fun registerEvent(){
        binding.addBtnHome.setOnClickListener{
            popupFragment = AddTodoPopupFragment()
            popupFragment.setListener(this)
            popupFragment.show(
                childFragmentManager,
                "AddTodoPopupFragment"
            )
        }
    }

    private fun init(view: View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks")
            .child(auth.currentUser?.uid.toString())
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
//        if (auth.currentUser==null){
//            Log.d("MSG", "No user")
//        }else {
            databaseRef.push().setValue(todo).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Firebase", "Task saved successfully")
                    Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT).show()
                    todoEt.text = null
                } else {
                    Log.d("Firebase", "Failed to save Task")
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
                popupFragment.dismiss()
            }
//        }
    }
}