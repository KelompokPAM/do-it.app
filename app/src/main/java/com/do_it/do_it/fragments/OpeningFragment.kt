package com.do_it.do_it.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.do_it.do_it.R
import com.google.firebase.auth.FirebaseAuth

class OpeningFragment : Fragment() {
    private  lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opening, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        Handler(Looper.myLooper()!!).postDelayed({
            if(auth.currentUser != null){
                navController.navigate(R.id.action_openingFragment_to_homeFragment)
            }else{
                navController.navigate(R.id.action_openingFragment_to_signInFragment)
            }
        },3000)
    }
}