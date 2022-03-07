package com.example.financialmanager.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financialmanager.MyAdapter
import com.example.financialmanager.R
import com.example.financialmanager.Spending
import com.google.firebase.database.*
import com.google.firebase.installations.FirebaseInstallations
import java.util.*


class ListFragment : Fragment() {

    private lateinit var database: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshFragment(activity)

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
                val fid = task.result

                var recyclerView = getView()?.findViewById<RecyclerView>(R.id.spendingList)
                database = FirebaseDatabase.getInstance(getString(R.string.firebaseURL))
                    .getReference("Spending/"+fid)
                recyclerView?.setHasFixedSize(true)
                recyclerView?.setLayoutManager(LinearLayoutManager(activity)) //set recyclerView

                var list = ArrayList<Spending>()
                var myAdapter = activity?.let { MyAdapter(it, list) } //bind spendingList to adapter
                recyclerView?.setAdapter(myAdapter) //bind adapter to recyclerView

                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot in snapshot.children) {
                            val spending = dataSnapshot.getValue(Spending::class.java)
                            if (spending != null) { //needs null check in Kotlin
                                list.add(spending) //adds spending data from db to list
                            }
                        }
                        myAdapter?.notifyDataSetChanged() //implements updated list to recyclerview
                    }

                override fun onCancelled(error: DatabaseError) {
                    }
                })

            }
        }



    }

    private fun refreshFragment(context: Context?){
        context?.let {
            val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
            fragmentManager?.let {
                val currentFragment = fragmentManager.findFragmentById(R.id.container)
                currentFragment?.let {
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.detach(it)
                    fragmentTransaction.attach(it)
                    fragmentTransaction.commit()
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }


}