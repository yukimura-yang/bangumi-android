package moe.gkd.bangumi.ui.addsubscription

import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import moe.gkd.bangumi.data.response.TorrentTag
import moe.gkd.bangumi.databinding.FragmentTagSearchBinding
import moe.gkd.bangumi.ui.BaseFragment
import moe.gkd.bangumi.ui.widget.TagChip

class TagSearchFragment : BaseFragment<FragmentTagSearchBinding>() {
    private val TAG = TagSearchFragment::class.simpleName
    private val viewModel: AddSubscriptionViewModel by activityViewModels()

    override fun initViews() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                viewModel.search(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    override fun initViewModel() {
        viewModel.recommendTags.observe(this) {
            setRecommendTags(it)
        }
        viewModel.searchTags.observe(this) {
            setSearchTags(it)
        }
        viewModel.selectedTags.observe(this) {
            setSelectTag(it)
        }
    }

    fun setRecommendTags(list: List<TorrentTag>) {
        binding.recommendGroup.also { chipGroup ->
            chipGroup.removeAllViews()
            for (tag in list) {
                val chip = TagChip(requireContext())
                chip.setTorrentTag(tag)
                chip.setOnClickListener(this::onRecommendChipClick)
                chipGroup.addView(chip)
            }
        }
    }

    fun setSearchTags(list: List<TorrentTag>) {
        binding.searchGroup.also { chipGroup ->
            chipGroup.removeAllViews()
            for (tag in list) {
                val chip = TagChip(requireContext())
                chip.setTorrentTag(tag)
                chip.setOnClickListener(this::onRecommendChipClick)
                chipGroup.addView(chip)
            }
        }
    }

    private fun setSelectTag(set: LinkedHashSet<TorrentTag>) {
        binding.selectedGroup.also { chipGroup ->
            chipGroup.removeAllViews()
            for (tag in set) {
                val chip = TagChip(requireContext())
                chip.setTorrentTag(tag)
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener(this::onCloseTagClick)
                chipGroup.addView(chip)
            }
        }
    }


    fun onCloseTagClick(view: View) {
        val chip = view as TagChip
        val set = viewModel.selectedTags.value!!
        set.remove(chip.getTorrentTag())
        viewModel.selectedTags.postValue(set)
    }

    fun onRecommendChipClick(view: View) {
        val chip = view as TagChip
        val set = viewModel.selectedTags.value!!
        set.add(chip.getTorrentTag())
        viewModel.selectedTags.postValue(set)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTagSearchBinding {
        return FragmentTagSearchBinding.inflate(inflater, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }
}