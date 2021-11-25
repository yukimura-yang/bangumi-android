package moe.gkd.bangumi.transmission.entity

class FileStats(
    val bytesCompleted: Long,
    val priority: Int,
    val wanted: Boolean,
)