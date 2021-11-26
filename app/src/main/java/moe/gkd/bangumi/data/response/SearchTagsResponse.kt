package moe.gkd.bangumi.data.response

data class SearchTagsResponse(
    val found: Boolean,
    val success: Boolean,
    val tag: List<TorrentTag>
)