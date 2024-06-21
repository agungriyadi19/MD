package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import com.example.smetracecare.adapter.ListProductAdapter
import com.example.smetracecare.data.ProductDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.FragmentProductHomeBinding
import com.example.smetracecare.databinding.FragmentSupplierHomeBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.ProductViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class ProductHomeFragment : Fragment() {

    private var _binding: FragmentProductHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferences: SharedPreferences
    private val productViewModel: ProductViewModel by lazy {
        ViewModelProvider(this)[ProductViewModel::class.java]
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
        _binding = FragmentProductHomeBinding.inflate(inflater, container, false)
        preferences = SharedPreferences.getInstance(requireContext().dataStore)

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
                productViewModel.GetProduct(token, userId)
            }
        }
        productViewModel.loading.observe(viewLifecycleOwner) {
            onLoading(it)
        }
        Log.d("ini product", productViewModel.product.toString())

        productViewModel.message.observe(viewLifecycleOwner) {
//            setDataProduct(productViewModel.product)
            showToast(it)
        }
        productViewModel.product.observe(viewLifecycleOwner) { productList ->
            Log.d("SupplierHomeActivity", "Data updated: $productList")
            setDataProduct(productList)
        }
        return binding.root
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun setDataProduct(product: List<ProductDetail>) {
        val adapter = ListProductAdapter(product)
        binding.rvStories.layoutManager = LinearLayoutManager(context)
        binding.rvStories.adapter = adapter
        adapter.setOnItemClickCallback(object : ListProductAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ProductDetail) {
                val intent = Intent(context, SupplierProductDetailActivity::class.java)
                intent.putExtra(SupplierProductDetailActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }
    private fun showToast(msg: String) {
        if (productViewModel.isError) {
            Toast.makeText(context, "${getString(R.string.error_load)} $msg", Toast.LENGTH_LONG).show()
        }
    }

    private fun onClicked() {
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(context, UploadProductUmkmActivity::class.java))
        }
    }
}