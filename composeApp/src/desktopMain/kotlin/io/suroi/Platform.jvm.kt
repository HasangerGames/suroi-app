package io.suroi

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override fun configureWebView(webView: Any) {
        TODO("Not yet implemented")
    }
}

actual fun getPlatform(): Platform = JVMPlatform()

