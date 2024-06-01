package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivitySupplierProductAddBinding


class SupplierProductAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener() {
            startActivity(Intent(this, SupplierHomeActivity::class.java))
        }
//        val spinner = binding.spinner
//        val adapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.options_array,
//            R.layout.simple_spinner_item
//        )
//        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter

//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                val selectedOption = parent.getItemAtPosition(position).toString()
//                // Lakukan sesuatu dengan selectedOption
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // Tindakan ketika tidak ada yang dipilih
//            }
//        }
    }
}