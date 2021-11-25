package moe.gkd.bangumi.data.entity.bangumi

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
data class BangumiItem constructor(
    @field:Element(name = "title")
    @param:Element(name = "title")
    val title: String,

    @field:Element(name = "description")
    @param:Element(name = "description")
    val description: String,

    @field:Element(name = "link")
    @param:Element(name = "link")
    val link: String,

    @field:Element(name = "pubDate")
    @param:Element(name = "pubDate")
    val pubDate: String,

    @field:Element(name = "enclosure")
    @param:Element(name = "enclosure")
    val enclosure: BangumiEnclosure,
) {
    val url = enclosure.url
}