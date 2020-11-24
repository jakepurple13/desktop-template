interface GenericInfo {
    fun getItems(page: Int): List<GenericData>
}

abstract class GenericData(
    open val title: String,
    open val url: String,
) {
    abstract val listOfData: GenericInformation?
}

abstract class GenericInformation(
    open val title: String,
    open val url: String,
    open val imageUrl: String?,
    open  val description: String?,
    open val genres: List<String>
) {
    abstract fun rowData(): List<RowData>
}

class RowData(val name: String, val uploaded: String, val getItem: () -> List<String>)
