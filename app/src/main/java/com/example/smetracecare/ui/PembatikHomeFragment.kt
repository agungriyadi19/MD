package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smetracecare.R
import com.example.smetracecare.adapter.ListMaterialAdapter
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.FragmentPembatikHomeBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.SmeMaterialViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class PembatikHomeFragment : Fragment() {

    private var _binding: FragmentPembatikHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences: SharedPreferences
    private val smeMaterialViewModel: SmeMaterialViewModel by lazy {
        ViewModelProvider(this)[SmeMaterialViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPembatikHomeBinding.inflate(inflater, container, false)
        super.onCreate(savedInstanceState)

        // Inisialisasi preferences menggunakan DataStore
        preferences = SharedPreferences.getInstance(requireContext().dataStore)

        onClicked()

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]

        dataStoreViewModel.getToken().observe(viewLifecycleOwner) { token ->
            if (token != null) {
                smeMaterialViewModel.GetMaterialSme(token)
            } else {
                Log.e("PembatikHomeFragment", "Token is null")
            }
        }
        smeMaterialViewModel.loading.observe(viewLifecycleOwner) {
            onLoading(it)
        }

        smeMaterialViewModel.message.observe(viewLifecycleOwner) {
            showToast(it)
        }
        smeMaterialViewModel.material.observe(viewLifecycleOwner) { materialList ->
            Log.d("PembatikHomeFragment", "Data updated: $materialList")
            setDataMaterial(materialList)
        }
        return binding.root
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun setDataMaterial(material: List<MaterialDetail>) {
        val adapter = ListMaterialAdapter(material)
        binding.rvStories.layoutManager = LinearLayoutManager(context)
        binding.rvStories.adapter = adapter
        adapter.setOnItemClickCallback(object : ListMaterialAdapter.OnItemClickCallback {
            override fun onItemClicked(data: MaterialDetail) {
                val intent = Intent(context, PembatikDetailActivity::class.java)
                intent.putExtra(PembatikDetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }

    private fun showToast(msg: String) {
        if (smeMaterialViewModel.isError) {
            Toast.makeText(context, "${getString(R.string.error_load)} $msg", Toast.LENGTH_LONG).show()
        }
    }

    private fun onClicked() {
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(context, PembatikAddActivity::class.java))
        }
    }
}
