package detector

import java.time.LocalDateTime

data class Transaction(
    val cardId: String,
    val dataTime: LocalDateTime,
    val amount: Double
)
