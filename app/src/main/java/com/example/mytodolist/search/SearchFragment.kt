package com.example.mytodolist.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.R
import com.example.mytodolist.base.BaseFragment
import com.example.mytodolist.databinding.FragmentSearchBinding


class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    override fun setupViews() {
        binding.topAppBar.setNavigationOnClickListener{
            findNavController().navigate(R.id.searchToHome)
        }
    }

    override fun observeViewModel() {

    }

    override fun onClick(viewId: Int) {

    }



}