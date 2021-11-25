package moe.gkd.bangumi.data.entity.bangumi

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class BangumiFeed(
    @field:Element(name = "channel")
    @param:Element(name = "channel")
    val channel: BangumiChannel
)