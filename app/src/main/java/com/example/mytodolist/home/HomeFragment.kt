package com.example.mytodolist.home

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.base.BaseFragment
import com.example.mytodolist.data.*
import com.example.mytodolist.databinding.AddTaskPopupBinding
import com.example.mytodolist.databinding.DeleteconformationBinding
import com.example.mytodolist.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun setupViews() {
        binding.fabAddTask.setOnClickListener { onClick(it.id) }
        binding.searchIcon.setOnClickListener { onClick(it.id) }

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


        setupSwipeToDelete()
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
            R.id.searchIcon -> findNavController().navigate(R.id.searchFragment)
            R.id.fabAddTask -> showAddTaskPopup()
        }
    }

    private fun showAddTaskPopup() {
        val popbinding = AddTaskPopupBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(popbinding.root)

        popbinding.popUpCalendar.minDate = System.currentTimeMillis()

        fun addUserTag(tagText: String) {
            if (tagText.isBlank()) return
            val chip = Chip(requireContext()).apply {
                text = tagText
                isCheckable = true
                isChecked=true
                isCloseIconVisible = true
                setChipBackgroundColorResource(R.color.primaryGreen)
                setTextColor(requireContext().getColor(R.color.black))
                setOnCloseIconClickListener { popbinding.popUpChip.removeView(this) }
                setOnCheckedChangeListener{_,isChecked->
                    if(isChecked){
                        setChipBackgroundColorResource(R.color.primaryGreen)
                        setTextColor(requireContext().getColor(R.color.black))
                    }
                    else{
                        setChipBackgroundColorResource(R.color.midgreen)
                        setTextColor(requireContext().getColor(R.color.white))
                    }
                }
            }
            popbinding.popUpChip.addView(chip)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.alltags.collect { tagList ->
                tagList.forEach { tag ->
                    val chip = Chip(requireContext()).apply {
                        text = tag
                        isCheckable = true
                        setChipBackgroundColorResource(R.color.midgreen)
                        setTextColor(requireContext().getColor(R.color.white))
                    }
                    chip.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            chip.setChipBackgroundColorResource(R.color.primaryGreen)
                            chip.setTextColor(requireContext().getColor(R.color.black))
                        } else {
                            chip.setChipBackgroundColorResource(R.color.midgreen)
                            chip.setTextColor(requireContext().getColor(R.color.white))
                        }
                    }
                    popbinding.popUpChip.addView(chip)
                }
            }
        }

        popbinding.tagEditText.setOnEditorActionListener { _, _, _ ->
            addUserTag(popbinding.tagEditText.text.toString().trim())
            popbinding.tagEditText.text.clear()
            true
        }
        popbinding.addTagIcon.setOnClickListener {
            addUserTag(popbinding.tagEditText.text.toString().trim())
            popbinding.tagEditText.text.clear()
        }

        popbinding.cancelBtn.setOnClickListener { dialog.dismiss() }
        var selected_date=System.currentTimeMillis()
        popbinding.popUpCalendar.setOnDateChangeListener{_,year,month,dayOfMonth->
            val calendar=java.util.Calendar.getInstance()
            calendar.set(year, month,dayOfMonth, 0,0,0)
            selected_date=calendar.timeInMillis
        }
        popbinding.addBtn.setOnClickListener {
            val title = popbinding.popUpTitleBox.text.toString().trim()
            val description = popbinding.popUpDescriptionBox.text.toString().trim()
            val deadline=selected_date
            if (title.isBlank()) {
                Toast.makeText(requireContext(), "Title should not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tags = mutableListOf<String>()
            for (i in 0 until popbinding.popUpChip.childCount) {
                val chip = popbinding.popUpChip.getChildAt(i) as Chip
                if(chip.isChecked) {
                    tags.add(chip.text.toString())
                }
            }

            viewModel.addTask(title, description, tags, deadline)
            dialog.dismiss()
        }

        dialog.show()
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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


    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                target: androidx.recyclerview.widget.RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = adapter.taskList[position]
                val dialogbinding = DeleteconformationBinding.inflate(layoutInflater)
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogbinding.root)
                    .setCancelable(false)
                    .create()


                dialog.show()
                dialogbinding.cancelBtn.setOnClickListener {
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                dialogbinding.deleteBtn.setOnClickListener {
                    viewModel.deleteTask(task.task)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(),"Deleted!!",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

            }
            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val background = android.graphics.drawable.ColorDrawable()
                    background.color = android.graphics.Color.RED
                    //right swipe
                    if (dX > 0) {
                        background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    }
                    background.draw(c)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.homeRecycler)
    }
}