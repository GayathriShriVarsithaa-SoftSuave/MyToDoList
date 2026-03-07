package com.example.mytodolist.detail

import android.content.res.ColorStateList
import kotlinx.coroutines.flow.first
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
import com.example.mytodolist.home.TaskAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {

    private val args by navArgs<DetailFragmentArgs>()
    private lateinit var viewModel: DetailViewModel
    private lateinit var adapter: TaskAdapter

    override fun setupViews() {

        viewModel = ViewModelProvider(requireActivity())[DetailViewModel::class.java]
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
        binding.detailDate.text=taskWithTags.task.date.toString()
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
        val prevdate=try {
            binding.detailDate.text.toString().toLong()
        }
        catch (e:Exception){
            System.currentTimeMillis()
        }
        popbinding.popUpCalendar.date=prevdate
        var date=prevdate
        popbinding.taskEdit.setText(binding.detailTitle.text)
        popbinding.descriptionEdit.setText(binding.detailDescriptionBox.text)
        popbinding.popUpCalendar.minDate=System.currentTimeMillis()
        popbinding.popUpCalendar.setOnDateChangeListener{_,year,month,dayOfMonth->
            val calendar=java.util.Calendar.getInstance()
            calendar.set(year, month,dayOfMonth, 0,0,0)
            date=calendar.timeInMillis
        }
        fun addUserTag(tagText: String) {
            if (tagText.isBlank()) return
            val chip = Chip(requireContext()).apply {
                text = tagText
                isCheckable = false
                isCloseIconVisible = true
                setChipBackgroundColorResource(R.color.primaryGreen)
                setTextColor(requireContext().getColor(R.color.black))
                setOnCloseIconClickListener { popbinding.popUpChip.removeView(this) }
            }
            popbinding.popUpChip.addView(chip)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val tagList = viewModel.alltags.first()
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

        dialog.show()
        popbinding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        popbinding.tagEditText.setOnEditorActionListener { _, _, _ ->
            addUserTag(popbinding.tagEditText.text.toString().trim())
            popbinding.tagEditText.text.clear()
            true
        }
        popbinding.addTagIcon.setOnClickListener{
            addUserTag(popbinding.tagEditText.text.toString())
            popbinding.tagEditText.text.clear()
        }
        popbinding.editBtn.setOnClickListener {
            val title=popbinding.taskEdit.text.toString()
            val description=popbinding.descriptionEdit.text.toString()
            val tags=mutableListOf<String>()
            for (i in 0 until popbinding.popUpChip.childCount) {
                val chip = popbinding.popUpChip.getChildAt(i) as Chip
                tags.add(chip.text.toString())
            }

            viewModel.updateTask(args.taskId,title,description,tags,date)

            dialog.dismiss()

        }
    }
}