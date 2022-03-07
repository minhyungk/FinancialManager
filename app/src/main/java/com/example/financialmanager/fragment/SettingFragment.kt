package com.example.financialmanager.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.financialmanager.R
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import com.example.financialmanager.Setting
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations


class SettingFragment : Fragment() {
    private lateinit var  database : DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingButton = getView()?.findViewById<Button>(R.id.settingButton)


        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
                val fid = task.result

                settingButton?.setOnClickListener {
                    Log.e("button", "button clicked")

                    val hideProduct = getView()?.findViewById<Switch>(R.id.hideProduct)?.isChecked
                    val hideCategory = getView()?.findViewById<Switch>(R.id.hideCategory)?.isChecked
                    val hidePrice = getView()?.findViewById<Switch>(R.id.hidePrice)?.isChecked

                    database = FirebaseDatabase.getInstance(
                        getString(R.string.firebaseURL)
                    ).getReference("Settings")

                    var setting: Setting = Setting(hideProduct, hideCategory, hidePrice)

                    database.child(fid).setValue(setting) //save settings
                    Toast.makeText(activity, "Setting Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }


}