package com.example.financialmanager.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.financialmanager.R
import com.example.financialmanager.Setting
import com.example.financialmanager.Spending
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import java.lang.NumberFormatException
import java.sql.Timestamp
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [InputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputFragment : Fragment() {
    private lateinit var  database : DatabaseReference
    private var setting = Setting()
    //android 10 disallows hardware identifier

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sendButton = getView()?.findViewById<Button>(R.id.saveButton)

        //fetch settings
        //raise error
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
                val fid = task.result


                sendButton?.setOnClickListener {
                    var product =
                        getView()?.findViewById<EditText>(R.id.editTextProduct)?.text.toString()
                    var category =
                        getView()?.findViewById<EditText>(R.id.editTextCategory)?.text.toString()
                    val timestamp = Timestamp(Calendar.getInstance().timeInMillis)
                    val time = timestamp.toString().replace("-",":").substring(0,19) //format: 2022:02:22 15:51:31 (does not accept : in firebase)
                    var price =
                        getView()?.findViewById<EditText>(R.id.editTextPrice)?.text.toString()

                    if(product.length > 20){
                        Toast.makeText(activity, "Product should not exceed 20 characters", Toast.LENGTH_SHORT).show()
                    }
                    else if(category.length > 20){
                        Toast.makeText(activity, "Category should not exceed 20 characters", Toast.LENGTH_SHORT).show()
                    }
                    else if(price.length > 20){
                        Toast.makeText(activity, "Category should not exceed 20 characters", Toast.LENGTH_SHORT).show()
                    }
                    else if((product=="")||(category=="")||(price=="")){
                        Toast.makeText(activity, "Input shouldn't be empty", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        database = FirebaseDatabase.getInstance(
                            getString(R.string.firebaseURL)
                        )
                            .reference

                        database.child("Settings/$fid").get().addOnSuccessListener{
                            setting.hideProduct = it.child("hideProduct").value as Boolean?
                            setting.hideCategory = it.child("hideCategory").value as Boolean?
                            setting.hidePrice = it.child("hidePrice").value as Boolean?

                            if(setting.hideProduct == true){
                                product = "hidden"
                                Log.e("productHidden", product.toString())
                            }
                            if(setting.hideCategory == true){
                                category = "hidden"
                            }
                        try{
                            val spending = Spending(product, category, price.toInt(), time)
                            database.child("Spending/$fid").child(time).setValue(spending).addOnSuccessListener {
                                getView()?.findViewById<EditText>(R.id.editTextProduct)?.text?.clear()
                                getView()?.findViewById<EditText>(R.id.editTextCategory)?.text?.clear()
                                getView()?.findViewById<EditText>(R.id.editTextPrice)?.text?.clear()
                            }

                            Toast.makeText(activity, "Data Saved", Toast.LENGTH_SHORT).show()
                        }catch(nfe: NumberFormatException){
                            Toast.makeText(activity, "Price should only contain numbers", Toast.LENGTH_SHORT).show()
                        }
                            }
                    }
                }

            } else {
                Log.e("Installations", "Unable to get Installation ID")
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input, container, false)
    }


}