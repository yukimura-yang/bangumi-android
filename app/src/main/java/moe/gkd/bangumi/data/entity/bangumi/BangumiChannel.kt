package moe.gkd.bangumi.data.entity.bangumi

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "channel", strict = false)
data class BangumiChannel constructor(
    @field:ElementList(entry = "item", inline = true, required = false)
    @param:ElementList(entry = "item", inline = true, required = false)
    val items: List<BangumiItem> = arrayListOf()
)