package com.example.mytodolist.search

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodolist.R
import com.example.mytodolist.base.BaseFragment
import com.example.mytodolist.data.Tag
import com.example.mytodolist.databinding.FragmentSearchBinding
import com.example.mytodolist.home.TaskAdapter
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class SearchFragment :
    BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: TaskAdapter
    override fun setupViews() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.searchToHome)
        }
        setupRecyclerView()
        setupSearchListener()
        loadAvailableTags()
    }

    override fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.results.collect { tasks ->

                adapter.submitList(tasks)

                if (tasks.isEmpty()) {
                    binding.tvNoResults.visibility = View.VISIBLE
                } else {
                    binding.tvNoResults.visibility = View.GONE
                }

                binding.filteredTitle.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(viewId: Int) {}

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            emptyList(),
            onCheckedChange = { taskId, isChecked ->
                viewModel.updateCompletion(taskId, isChecked)
                              },
            onItemClick = { taskId ->
                val action =
                    SearchFragmentDirections.searchToDetail(taskId)
                findNavController().navigate(action)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchListener() {
        binding.searchBox.addTextChangedListener {
            viewModel.updateQuery(binding.searchBox.text.toString())
        }
    }

    private fun loadAvailableTags() {
        lifecycleScope.launch {
            val tags = viewModel.getAllTags()
            addChips(tags)
        }
    }

    private fun addChips(tags: List<Tag>) {
        binding.availableChip.removeAllViews()
        tags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag.tag
                isCheckable = true
                isChecked = false
                setChipBackgroundColorResource(R.color.midgreen)
                setTextColor(requireContext().getColor(R.color.white))
                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        setChipBackgroundColorResource(R.color.primaryGreen)
                        setTextColor(requireContext().getColor(R.color.black))
                    } else {
                        setChipBackgroundColorResource(R.color.midgreen)
                        setTextColor(requireContext().getColor(R.color.white))
                    }
                    updateSelectedTags()
                }
            }
            binding.availableChip.addView(chip)
        }
    }

    private fun updateSelectedTags() {
        val selectedTags = mutableListOf<String>()
        binding.availableChip.children.forEach { view ->
            val chip = view as Chip
            if (chip.isChecked) {
                selectedTags.add(chip.text.toString())
            }
        }
        viewModel.updateTags(selectedTags)
    }
}