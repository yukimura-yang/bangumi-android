package moe.gkd.bangumi.transmission

import moe.gkd.bangumi.MainApplication
import moe.gkd.bangumi.TRANSMISSION_AUTHORIZATION
import moe.gkd.bangumi.TRANSMISSION_PASSWORD
import moe.gkd.bangumi.TRANSMISSION_USERNAME
import okhttp3.*

class TransmissionAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") != null) {
            return null //Give up, we've already attempted to authenticate.
        }
        val userName = MainApplication.INSTANCE.hashMap[TRANSMISSION_USERNAME].toString()
        val password = MainApplication.INSTANCE.hashMap[TRANSMISSION_PASSWORD].toString()
        val credential = Credentials.basic(userName, password)
        MainApplication.INSTANCE.hashMap[TRANSMISSION_AUTHORIZATION] = credential
        return response.request.newBuilder()
            .header("Authorization", credential)
            .build()
    }
}