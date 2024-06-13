package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smetracecare.R
import com.example.smetracecare.adapter.ListMaterialAdapter
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.databinding.FragmentSupplierHomeBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.MaterialViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class SupplierHomeFragment : Fragment() {

    private var _binding: FragmentSupplierHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences: SharedPreferences
    private val materialViewModel: MaterialViewModel by lazy {
        ViewModelProvider(this)[MaterialViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(requireContext()).apply {
                    setMessage("Are you sure want to exit?")
                    setPositiveButton("Yes") { _, _ ->
                        activity?.moveTaskToBack(true)
                        activity?.finish()
                    }
                    setNegativeButton("No", null)
                }.show()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplierHomeBinding.inflate(inflater, container, false)

        super.onCreate(savedInstanceState)
        onClicked()

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getName().observe(viewLifecycleOwner) { data ->
            binding.apply {
                tvSupplierName.text = data
            }
        }
        dataStoreViewModel.getToken().observe(viewLifecycleOwner) { token ->
            dataStoreViewModel.getUserID().observe(viewLifecycleOwner) { userId ->
                materialViewModel.GetMaterial(token, userId)
            }
        }
        materialViewModel.loading.observe(viewLifecycleOwner) {
            onLoading(it)
        }
        Log.d("ini material", materialViewModel.material.toString())

        materialViewModel.message.observe(viewLifecycleOwner) {
//            setDataMaterial(materialViewModel.material)
            showToast(it)
        }
        materialViewModel.material.observe(viewLifecycleOwner) { materialList ->
            Log.d("SupplierHomeActivity", "Data updated: $materialList")
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
                val intent = Intent(context, SupplierProductDetailActivity::class.java)
                intent.putExtra(SupplierProductDetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }
    private fun showToast(msg: String) {
        if (materialViewModel.isError) {
            Toast.makeText(context, "${getString(R.string.error_load)} $msg", Toast.LENGTH_LONG).show()
        }
    }

    private fun onClicked() {
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(context, SupplierProductAddActivity::class.java))
        }

        binding.btnProfile.setOnClickListener() {
            startActivity(Intent(context, SupplierProfileActivity::class.java))
        }
    }
}