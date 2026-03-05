package com.example.mytodolist.detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mytodolist.R
import com.example.mytodolist.base.BaseFragment
import com.example.mytodolist.home.HomeViewModel
import com.example.mytodolist.data.TaskWithTags
import com.example.mytodolist.databinding.AddTaskPopupBinding
import com.example.mytodolist.databinding.EditPageBinding
import com.example.mytodolist.databinding.FragmentDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {

    private val args by navArgs<DetailFragmentArgs>()
    private lateinit var viewModel: HomeViewModel

    override fun setupViews() {

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allTasks.collect { taskList ->
                val task = taskList.find { it.task.taskId == args.taskId }
                task?.let { bindTaskDetails(it) }
            }
        }
        binding.toolBar.setNavigationOnClickListener {
            onClick(R.id.toolBar)
        }
        binding.searchIcon.setOnClickListener{
            onClick(it.id)
        }
    }
    private fun bindTaskDetails(taskWithTags: TaskWithTags) {

        binding.detailTitle.text = taskWithTags.task.title
        binding.detailDescriptionBox.text = taskWithTags.task.description
        binding.detailChip.removeAllViews()

        taskWithTags.tags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag.tag
                isClickable = false
                isCheckable = false
                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darkBlue
                    )
                )
                setTextColor(Color.WHITE)
            }
            binding.detailChip.addView(chip)
        }
    }

    override fun observeViewModel() {

    }

    override fun onClick(viewId: Int) {
        when (viewId) {
            R.id.toolBar -> {
                findNavController().navigate(R.id.detailToHome)
            }
            R.id.searchIcon->{
                showEdit()
            }
        }
    }
    fun showEdit(){
        val popbinding= EditPageBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(popbinding.root)

        popbinding.titleEdit.setText(binding.detailTitle.text)
        popbinding.descriptionEdit.setText(binding.detailDescriptionBox.text)
        dialog.show()
    }
}