import com.oolio.pos.data.db.entities.PrintStatus
import com.oolio.pos.data.print.PrinterService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class MockPrinterServiceImpl @Inject constructor() : PrinterService {

    private val availabilityMap = mutableMapOf<String, Boolean>()
    private val callbacksMap = mutableMapOf<String, MutableList<(Boolean) -> Unit>>()

    override fun isAvailable(printerId: String) = availabilityMap[printerId] ?: false

    override fun onPrinterAvailable(printerId: String, callback: (Boolean) -> Unit) {
        val list = callbacksMap.getOrPut(printerId) { mutableListOf() }
        list.add(callback)
    }

    override fun print(printerId: String, data: String): PrintStatus {
        return if (isAvailable(printerId)) {
            println("Printing on $printerId: $data")
            PrintStatus.COMPLETED
        } else {
            PrintStatus.FAILED
        }
    }
}
