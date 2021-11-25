package moe.gkd.bangumi.transmission.request

import moe.gkd.bangumi.transmission.enum.EnumTransmissionMethod
import moe.gkd.bangumi.transmission.request.args.GetTorrentsReqArgs

class GetTorrentsReqBody :
    BaseRequestBody<GetTorrentsReqArgs>(GetTorrentsReqArgs(), EnumTransmissionMethod.TORRENT_GET)