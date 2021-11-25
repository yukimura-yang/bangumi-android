package moe.gkd.bangumi.transmission.request

import moe.gkd.bangumi.transmission.enum.EnumTransmissionMethod

class GetSessionBody : BaseRequestBody<Void>(null, EnumTransmissionMethod.SESSION_GET)