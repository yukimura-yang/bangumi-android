package moe.gkd.bangumi.transmission.request

import moe.gkd.bangumi.transmission.enum.EnumTransmissionMethod

open class BaseRequestBody<T>(
    val arguments: T?,
    val method: EnumTransmissionMethod
)