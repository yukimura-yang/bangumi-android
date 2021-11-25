package moe.gkd.bangumi.transmission.entity

class Peer(
    val address: String,
    val clientIsChoked: Boolean,
    val clientIsInterested: Boolean,
    val clientName: String,
    val flagStr: String,
    val isDownloadingFrom: Boolean,
    val isEncrypted: Boolean,
    val isIncoming: Boolean,
    val isUTP: Boolean,
    val isUploadingTo: Boolean,
    val peerIsChoked: Boolean,
    val peerIsInterested: Boolean,
    val port: Int,
    val progress: Long,
    val rateToClient: Long,
    val rateToPeer: Long,
)