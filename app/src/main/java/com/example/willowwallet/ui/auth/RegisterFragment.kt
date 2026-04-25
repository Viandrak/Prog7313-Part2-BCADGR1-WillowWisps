package com.example.willowwallet.ui.auth

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.willowwallet.databinding.FragmentRegisterBinding
import com.example.willowwallet.viewmodel.AuthViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val authActivity = requireActivity() as AuthActivity
        viewModel = ViewModelProvider(this, ViewModelFactory(authActivity.repository))[AuthViewModel::class.java]

        viewModel.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthViewModel.AuthResult.Loading -> setLoading(true)
                is AuthViewModel.AuthResult.Success -> {
                    setLoading(false)
                    authActivity.onAuthSuccess(result.user.id, result.user.username, result.user.displayName)
                }
                is AuthViewModel.AuthResult.Error -> { setLoading(false); showError(result.message) }
                null -> setLoading(false)
            }
        }

        binding.btnRegister.setOnClickListener {
            viewModel.register(
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString(),
                binding.etConfirmPassword.text.toString(),
                binding.etDisplayName.text.toString()
            )
        }

        binding.tvLogin.setOnClickListener { parentFragmentManager.popBackStack() }

        // Password toggle
        var passwordVisible = false
        binding.btnTogglePassword.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnTogglePassword.setImageResource(com.example.willowwallet.R.drawable.ic_eye_closed)
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnTogglePassword.setImageResource(com.example.willowwallet.R.drawable.ic_eye_open)
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        // Confirm password toggle
        var confirmVisible = false
        binding.btnToggleConfirmPassword.setOnClickListener {
            confirmVisible = !confirmVisible
            if (confirmVisible) {
                binding.etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnToggleConfirmPassword.setImageResource(com.example.willowwallet.R.drawable.ic_eye_closed)
            } else {
                binding.etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnToggleConfirmPassword.setImageResource(com.example.willowwallet.R.drawable.ic_eye_open)
            }
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text.length)
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRegister.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}