package moe.gkd.bangumi.ui.addsubscription

import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
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
            changeRecommendTags(it)
        }
    }

    /**
     * @param isAdd 是否是在推荐列表里添加
     */
    fun changeRecommendTags(selected: LinkedHashSet<TorrentTag>) {
        val childViews = arrayListOf<TagChip>()
        for (i in 0 until binding.recommendGroup.childCount) {
            childViews.add(binding.recommendGroup[i] as TagChip)
        }
        for (childView in childViews) {
            val tag = childView.getTorrentTag()
            if (selected.contains(tag)) {
                childView.visibility = View.GONE
            } else {
                childView.visibility = View.VISIBLE
            }
        }
    }

    fun createRecommendTagChip(tag: TorrentTag): TagChip {
        val chip = TagChip(requireContext())
        chip.setTorrentTag(tag)
        chip.setOnClickListener(this::onRecommendChipClick)
        return chip
    }

    fun setRecommendTags(list: List<TorrentTag>) {
        binding.recommendGroup.also { chipGroup ->
            chipGroup.removeAllViews()
            for (tag in list) {
                chipGroup.addView(createRecommendTagChip(tag))
            }
        }
    }

    fun createSelectedTagChip(tag: TorrentTag): TagChip {
        val chip = TagChip(requireContext())
        chip.setTorrentTag(tag)
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener(this::onCloseTagClick)
        return chip
    }


    /**
     * 搜索tag
     */
    fun setSearchTags(list: List<TorrentTag>) {
        binding.searchGroup.also { chipGroup ->
            chipGroup.removeAllViews()
            for (tag in list) {
                chipGroup.addView(createRecommendTagChip(tag))
            }
        }
    }

    /**
     * 选择tag
     */
    private fun setSelectTag(set: LinkedHashSet<TorrentTag>) {
        binding.selectedGroup.also { chipGroup ->
            val oldChildViews = arrayListOf<TagChip>()
            for (i in 0 until chipGroup.childCount) {
                oldChildViews.add(chipGroup[i] as TagChip)
            }
            for (childView in oldChildViews) {
                val tag = childView.getTorrentTag()
                if (!set.contains(tag)) {
                    //不存在选中的集合里，删除
                    chipGroup.removeView(childView)
                }
            }
            for (tag in set) {
                val view = oldChildViews.find { it.getTorrentTag().id == tag.id }
                if (view == null) {
                    //不存在原先的集合里,添加
                    chipGroup.addView(createSelectedTagChip(tag))
                }
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