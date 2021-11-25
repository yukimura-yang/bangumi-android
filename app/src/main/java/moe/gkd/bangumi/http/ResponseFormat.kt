package moe.gkd.bangumi.http

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ResponseFormat(val value: String = JSON) {
    companion object {
        const val JSON = "json"
        const val XML = "xml"
    }
}