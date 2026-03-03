package com.example.mytodolist.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.R
import com.example.mytodolist.base.BaseFragment
import com.example.mytodolist.databinding.FragmentHomeBinding

import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun setupViews() {
        binding.fabAddTask.setOnClickListener {
            onClick(it.id)
        }
        binding.searchIcon.setOnClickListener {
            onClick(it.id)

        }
    }
    private fun setupRecyclerView(){

    }
    override fun observeViewModel() {

    }

    override fun onClick(viewId: Int) {
        when (viewId) {
            R.id.searchIcon -> {
                findNavController().navigate(R.id.searchFragment)
            }
            R.id.fabAddTask->{
                showAddTaskpopup()
            }
        }
    }
    private fun showAddTaskpopup() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.add_task_popup,null)
        dialog.setContentView(view)

    }


}