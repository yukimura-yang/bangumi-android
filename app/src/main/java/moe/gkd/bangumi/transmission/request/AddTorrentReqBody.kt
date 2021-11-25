package moe.gkd.bangumi.transmission.request

import moe.gkd.bangumi.transmission.enum.EnumTransmissionMethod
import moe.gkd.bangumi.transmission.request.args.AddTorrentReqArgs

class AddTorrentReqBody(
    magnet: String? = null,
    torrent: String? = null,
    dir: String
) : BaseRequestBody<AddTorrentReqArgs>(
    method = EnumTransmissionMethod.TORRENT_ADD,
    arguments = AddTorrentReqArgs(
        magnet,
        torrent,
        dir
    )
)