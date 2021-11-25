package moe.gkd.bangumi.data.entity

data class LoadStateEntity(
    val loading: Boolean,
    val success: Boolean,
    val error: String = ""
)