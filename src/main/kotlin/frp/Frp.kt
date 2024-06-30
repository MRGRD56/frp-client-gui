package frp

class Frp {
    companion object {
        val availableProtocols: List<String> = listOf(
            "HTTP",
            "HTTPS",
            "TCP",
            "UDP",
            "STCP",
            "SUDP"
        )
    }
}