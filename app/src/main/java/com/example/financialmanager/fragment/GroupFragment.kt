package com.example.financialmanager.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financialmanager.MyAdapter
import com.example.financialmanager.R
import com.example.financialmanager.Spending
import com.google.firebase.database.*
import com.google.firebase.installations.FirebaseInstallations
import java.sql.Timestamp
import java.util.*


class GroupFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var database: DatabaseReference
    private var userFIDList = mutableListOf<String>("cdROWNLRQiKJ99oTZ2SGKG") //이런 시발 이따 고쳐 왜 미리 add가 안되는건지 몰루
    private var userFIDnumber : Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshFragment(activity)

        var recyclerView = getView()?.findViewById<RecyclerView>(R.id.groupSpendingList)
        val groupButton = getView()?.findViewById<Button>(R.id.groupButton)
        var userNameTextView = getView()?.findViewById<TextView>(R.id.UserName)

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
                val fid = task.result

                database =
                    FirebaseDatabase.getInstance(
                        getString(R.string.firebaseURL))
                        .getReference("Spending")

                database.get().addOnSuccessListener {
                    for (userID in it.children) {
                        if (userID.key != fid) {
                            userID.key?.let { it1 -> userFIDList.add(it1) } //nullable
                        }
                    }
                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }

                //recyclerView
                recyclerView?.setHasFixedSize(true)
                recyclerView?.setLayoutManager(LinearLayoutManager(activity)) //set recyclerView

                if(fid == userFIDList[userFIDnumber]) {
                    userNameTextView?.text = "My Spending"
                }else{
                    userNameTextView?.text = userFIDList[userFIDnumber]
                }

                var list = ArrayList<Spending>()
                var myAdapter =
                    activity?.let { MyAdapter(it, list) } //bind spendingList to adapter

                recyclerView?.setAdapter(myAdapter) //bind adapter to recyclerView

                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot in snapshot.child(userFIDList[userFIDnumber]).children) {
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


                groupButton?.setOnClickListener {
                    if (userFIDList.isNotEmpty()) {
                        if (userFIDnumber < userFIDList.size - 1) {
                            userFIDnumber += 1  // go to next user
                        } else {
                            userFIDnumber = 0  //reset to first user
                        }

                        if(fid == userFIDList[userFIDnumber]) {
                            userNameTextView?.text = "My Spending"
                        }else{
                            userNameTextView?.text = userFIDList[userFIDnumber]
                        }

                        recyclerView?.setHasFixedSize(true)
                        recyclerView?.setLayoutManager(LinearLayoutManager(activity)) //set recyclerView

                        var list = ArrayList<Spending>()
                        var myAdapter =
                            activity?.let { MyAdapter(it, list) } //bind spendingList to adapter
                        recyclerView?.setAdapter(myAdapter) //bind adapter to recyclerView

                        database.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (dataSnapshot in snapshot.child(userFIDList[userFIDnumber]).children) {
                                    val spending = dataSnapshot.getValue(Spending::class.java)
                                    if (spending != null) { //needs null check in Kotlin
                                        list.add(spending) //adds spending data from db to list
                                    }
                                }
                                myAdapter?.notifyDataSetChanged() //implements updated list to recyclerview
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Database error", "database error")
                            }
                        })
                    }
                }
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
        return inflater.inflate(R.layout.fragment_group, container, false)
    }


}