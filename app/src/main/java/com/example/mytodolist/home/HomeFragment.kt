package com.example.mytodolist.home

import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodolist.R
import com.example.mytodolist.base.BaseFragment
import com.example.mytodolist.data.*
import com.example.mytodolist.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun setupViews() {
        binding.fabAddTask.setOnClickListener {
            onClick(it.id)
        }
        binding.searchIcon.setOnClickListener {
            onClick(it.id)
        }

        adapter = TaskAdapter(
            emptyList(),
            onCheckedChange = { taskId, isChecked -> viewModel.updateCompletion(taskId, isChecked) },
            onItemClick = { taskId ->
                val action = HomeFragmentDirections.homeToDetail(taskId)
                findNavController().navigate(action)
            }
        )

        binding.homeRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecycler.adapter = adapter

    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allTasks.collect { taskList ->
                adapter.submitList(taskList)
            }
        }
    }

    override fun onClick(viewId: Int) {
        when (viewId) {
            R.id.searchIcon -> {
                findNavController().navigate(R.id.searchFragment)
            }
            R.id.fabAddTask -> {
                showAddTaskPopup()
            }
        }
    }

    private fun showAddTaskPopup() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.add_task_popup, null)
        dialog.setContentView(view)

        val titleBox = view.findViewById<EditText>(R.id.popUpTitleBox)
        val descriptionBox = view.findViewById<EditText>(R.id.popUpDescriptionBox)
        val chipGroup = view.findViewById<ChipGroup>(R.id.popUpChip)
        val tagEditText = view.findViewById<EditText>(R.id.tagEditText)
        val calendarView = view.findViewById<CalendarView>(R.id.popUpCalendar)
        val cancelButton = view.findViewById<MaterialButton>(R.id.cancelBtn)
        val addButton = view.findViewById<MaterialButton>(R.id.addBtn)

        tagEditText.setOnEditorActionListener { _, _, _ ->
            val tagText = tagEditText.text.toString().trim()
            if (tagText.isNotEmpty()) {
                val chip = Chip(requireContext()).apply {
                    text = tagText
                    isCloseIconVisible = true
                    setChipBackgroundColorResource(R.color.mediumGreen)
                    setTextColor(requireContext().getColor(R.color.textWhite))
                    setCloseIconTintResource(R.color.textWhite)
                    setChipStrokeColorResource(R.color.primaryGreen)
                    setChipStrokeWidthResource(R.dimen.chipWidth)
                    setOnCloseIconClickListener { chipGroup.removeView(this) }
                }
                chipGroup.addView(chip)
                tagEditText.text.clear()
            }
            true
        }

        cancelButton.setOnClickListener { dialog.dismiss() }

        addButton.setOnClickListener {
            val title = titleBox.text.toString().trim()
            val description = descriptionBox.text.toString().trim()
            val deadline = calendarView.date

            val tags = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                tags.add(chip.text.toString())
            }

            viewModel.addTask(title, description, tags, deadline)

            dialog.dismiss()
        }

        dialog.show()
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            bottomSheet?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(it)

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isFitToContents = true
                behavior.skipCollapsed = true
                behavior.isDraggable = true
            }
        }
    }
}