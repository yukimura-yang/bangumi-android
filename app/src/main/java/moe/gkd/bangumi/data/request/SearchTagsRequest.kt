package moe.gkd.bangumi.data.request

data class SearchTagsRequest(
    val keywords: Boolean = true,
    val multi: Boolean = true,
    val name: String
)