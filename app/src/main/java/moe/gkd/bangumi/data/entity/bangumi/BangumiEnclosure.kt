package moe.gkd.bangumi.data.entity.bangumi

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root

@Root(name = "enclosure", strict = false)
data class BangumiEnclosure constructor(
    @field:Attribute(name = "url")
    @param:Attribute(name = "url")
    val url: String
)