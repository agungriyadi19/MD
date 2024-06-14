package com.example.smetracecare.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.FragmentProfileHomeBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.SmeProfileViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class ProfileHomeFragment : Fragment() {

    private var _binding: FragmentProfileHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferences: SharedPreferences
    private val smeProfileViewModel: SmeProfileViewModel by lazy {
        ViewModelProvider(this)[SmeProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences instance
        preferences = SharedPreferences.getInstance(requireContext().dataStore)

        // Setup ViewModel and observe data changes
        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        observeUserProfile(dataStoreViewModel)

        // Handle logout button click
        binding.buttonLogout.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Handle edit profile button click
        binding.editProfileButton.setOnClickListener {
            val intent = Intent(context, SmeProfileEditActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeUserProfile(dataStoreViewModel: DataStoreViewModel) {
        // Observe token and user ID changes to fetch profile data
        dataStoreViewModel.getToken().observe(viewLifecycleOwner) { token ->
            dataStoreViewModel.getUserID().observe(viewLifecycleOwner) { userId ->
                smeProfileViewModel.getSmeProfileResponse(token, userId)
            }
        }

        // Observe profile data changes
        smeProfileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            Log.d("dataUser", user.toString())
            binding.apply {
                if (user != null) {
                    tvDataName.text = user.loginResult!!.name ?: ""
                    tvDataEmail.text = user.loginResult!!.email ?: ""
                    tvDataDescription.text = user.loginResult!!.description ?: ""
                    tvDataPhone.text = user.loginResult!!.phoneNumber ?: ""
                    tvDataAddress.text = user.loginResult!!.address ?: ""
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi keluar")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, which ->
                // Perform logout action
                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
