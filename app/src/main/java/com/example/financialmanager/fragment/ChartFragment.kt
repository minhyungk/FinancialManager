package com.example.financialmanager.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.financialmanager.R
import com.example.financialmanager.Spending
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [ChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChartFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private val spendingMap : MutableMap<String?, Int?> = mutableMapOf()
    //val because only the content changes

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var pieChart = getView()?.findViewById<PieChart>(R.id.pieChart)

        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Installations", "Installation ID: " + task.result)
                val fid = task.result
                database =
                    FirebaseDatabase.getInstance(
                        getString(R.string.firebaseURL))
                        .getReference("Spending/" + fid)

                database.get().addOnSuccessListener {
                    Log.i("hello", "hello")

                    for (spending in it.children) {
                        val category = spending.child("category")
                        val price = spending.child("price")

                        if (spendingMap.containsKey(category.value)) {
                            spendingMap[category.value.toString()] = spendingMap[category.value]?.plus(
                                price.value.toString().toInt()
                            ) //cannot call Any

                        } else {
                            spendingMap.put(category.value.toString(), price.value.toString().toInt())
                            //cannot call Any
                            }

                        }

                    val spendingPie = mutableListOf<PieEntry>()

                    for((c, p) in spendingMap) {
                        if (p != null) {
                            spendingPie.add(PieEntry(p.toFloat(), c))
                        }
                    }

                    var pieDataSet = PieDataSet(spendingPie, "")
                    pieDataSet.setColors(Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.BLACK)


                    var pieData = PieData(pieDataSet)
                    pieData.setValueTextSize(12f)
                    pieData.setValueTextColor(Color.BLACK)

                    var des = pieChart?.description
                    des?.isEnabled = false //remove description
                    pieChart?.setData(PieData(pieDataSet))
                    pieChart?.invalidate()
                    pieChart?.setCenterText("Categories")
                    pieChart?.animate()

                    }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }


            }
        }



    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.financialmanager.R.layout.fragment_chart, container, false)
    }


}