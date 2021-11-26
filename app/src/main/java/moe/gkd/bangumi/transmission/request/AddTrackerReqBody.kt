package moe.gkd.bangumi.transmission.request

import moe.gkd.bangumi.transmission.enum.EnumTransmissionMethod
import moe.gkd.bangumi.transmission.request.args.AddTrackerReqArgs

class AddTrackerReqBody(args: AddTrackerReqArgs) :
    BaseRequestBody<AddTrackerReqArgs>(args, EnumTransmissionMethod.TORRENT_SET) {
}