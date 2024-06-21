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
import com.example.smetracecare.adapter.ListPembatikAdapter
import com.example.smetracecare.data.PembatikDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.FragmentPembatikHomeBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.PembatikViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class PembatikHomeFragment : Fragment() {

    private var _binding: FragmentPembatikHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences: SharedPreferences
    private val smePembatikViewModel: PembatikViewModel by lazy {
        ViewModelProvider(this)[PembatikViewModel::class.java]
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
        dataStoreViewModel.getUserID().observe(viewLifecycleOwner) { userId ->
            dataStoreViewModel.getToken().observe(viewLifecycleOwner) { token ->
                if (token != null) {
                    smePembatikViewModel.GetPembatik(token, userId)
                } else {
                    Log.e("PembatikHomeFragment", "Token is null")
                }
            }
        }
        smePembatikViewModel.loading.observe(viewLifecycleOwner) {
            onLoading(it)
        }

        smePembatikViewModel.message.observe(viewLifecycleOwner) {
            showToast(it)
        }
        smePembatikViewModel.pembatik.observe(viewLifecycleOwner) { pembatikList ->
            Log.d("PembatikHomeFragment", "Data updated: $pembatikList")
            setDataPembatik(pembatikList)
        }
        return binding.root
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun setDataPembatik(pembatik: List<PembatikDetail>) {
        val adapter = ListPembatikAdapter(pembatik)
        binding.rvStories.layoutManager = LinearLayoutManager(context)
        binding.rvStories.adapter = adapter
        adapter.setOnItemClickCallback(object : ListPembatikAdapter.OnItemClickCallback {
            override fun onItemClicked(data: PembatikDetail) {
                val intent = Intent(context, SmePembatikDetailActivity::class.java)
                intent.putExtra(SmePembatikDetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }

    private fun showToast(msg: String) {
        if (smePembatikViewModel.isError) {
            Toast.makeText(context, "${getString(R.string.error_load)} $msg", Toast.LENGTH_LONG).show()
        }
    }

    private fun onClicked() {
        binding.btnAddPembatik.setOnClickListener {
            val intent = Intent(requireContext(), SmePembatikAddActivity::class.java)
            startActivity(intent)
        }
    }
}
