package moe.gkd.bangumi.transmission.request.args

class AddTrackerReqArgs(
    val trackerAdd: List<String> = arrayListOf(
        //bangumi
        "https://tr.bangumi.moe:9696/announce",
        "http://tr.bangumi.moe:6969/announce",
        "udp://tr.bangumi.moe:6969/announce",
        //nyaa
        "udp://open.stealth.si:80/announce",
        "udp://tracker.opentrackr.org:1337/announce",
        "udp://tracker.coppersurfer.tk:6969/announce",
        "udp://exodus.desync.com:6969/announce",
        //acgrip
        "http://t.acg.rip:6699/announce",
        //acgtracker
        "http://open.acgtracker.com:1096/announce",
    ),
    val ids: List<Long>
)