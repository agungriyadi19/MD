package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smetracecare.R
import com.example.smetracecare.adapter.ListMaterialAdapter
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivitySupplierHomeBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.MaterialViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class SupplierHomeActivity : AppCompatActivity() {
    private val preferences = SharedPreferences.getInstance(dataStore)

    private lateinit var binding: ActivitySupplierHomeBinding
    private val materialViewModel: MaterialViewModel by lazy {
        ViewModelProvider(this)[MaterialViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClicked()

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getName().observe(this) { data ->
            binding.apply {
                tvSupplierName.text = data
            }
        }
        dataStoreViewModel.getToken().observe(this) { token ->
            dataStoreViewModel.getUserID().observe(this) { userId ->
                materialViewModel.GetMaterial(token, userId)
            }
        }
        materialViewModel.loading.observe(this) {
            onLoading(it)
        }
        Log.d("ini material", materialViewModel.material.toString())

        materialViewModel.message.observe(this) {
//            setDataMaterial(materialViewModel.material)
            showToast(it)
        }
        materialViewModel.material.observe(this) { materialList ->
            Log.d("SupplierHomeActivity", "Data updated: $materialList")
            setDataMaterial(materialList)
        }
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun setDataMaterial(material: List<MaterialDetail>) {
        Log.d("material", material.toString())

        val adapter = ListMaterialAdapter(material)
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter
        adapter.setOnItemClickCallback(object : ListMaterialAdapter.OnItemClickCallback {
            override fun onItemClicked(data: MaterialDetail) {
                Log.d("detail material", data.toString())
                val intent = Intent(this@SupplierHomeActivity, SupplierProductDetailActivity::class.java)
                intent.putExtra(SupplierProductDetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }
    private fun showToast(msg: String) {
        if (materialViewModel.isError) {
            Toast.makeText(this, "${getString(R.string.error_load)} $msg", Toast.LENGTH_LONG).show()
        }
    }

    private fun onClicked() {
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, SupplierProductAddActivity::class.java))
        }

        binding.btnProfile.setOnClickListener() {
            startActivity(Intent(this, SupplierProfileActivity::class.java))
        }
    }

}