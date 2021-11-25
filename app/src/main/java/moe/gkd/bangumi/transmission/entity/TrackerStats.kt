package moe.gkd.bangumi.transmission.entity

class TrackerStats(
    val announce: String,
    val announceState: Int,
    val downloadCount: Int,
    val hasAnnounced: Boolean,
    val hasScraped: Boolean,
    val host: String,
    val id: Int,
    val isBackup: Boolean,
    val lastAnnouncePeerCount: Int,
    val lastAnnounceResult: String,
    val lastAnnounceStartTime: Long,
    val lastAnnounceSucceeded: Boolean,
    val lastAnnounceTime: Long,
    val lastAnnounceTimedOut: Boolean,
    val lastScrapeResult: String,
    val lastScrapeStartTime: Long,
    val lastScrapeSucceeded: Boolean,
    val lastScrapeTime: Long,
    val lastScrapeTimedOut: Boolean,
    val leecherCount: Long,
    val nextAnnounceTime: Long,
    val nextScrapeTime: Long,
    val scrape: String,
    val scrapeState: Int,
    val seederCount: Long,
    val tier: Int,
)