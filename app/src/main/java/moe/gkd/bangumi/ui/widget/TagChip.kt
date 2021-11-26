package moe.gkd.bangumi.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.chip.Chip
import moe.gkd.bangumi.data.response.TorrentTag

class TagChip @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : Chip(context, attrs) {
    private lateinit var torrentTag: TorrentTag

    fun setTorrentTag(torrentTag: TorrentTag) {
        this.torrentTag = torrentTag
        text = torrentTag.getRecommendName()
    }

    fun getTorrentTag(): TorrentTag {
        return torrentTag
    }
}