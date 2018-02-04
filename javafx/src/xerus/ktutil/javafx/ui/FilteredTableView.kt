package xerus.ktutil.javafx.ui

import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.scene.control.TableView

open class FilteredTableView<T>(val data: ObservableList<T>, columnsConfigurable: Boolean = false) : TableView<T>() {

    val filteredData: FilteredList<T> = FilteredList(data)
    val predicate = filteredData.predicateProperty()!!

    init {
        items = SortedList<T>(filteredData).also { it.comparatorProperty().bind(comparatorProperty()) }

        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        isTableMenuButtonVisible = columnsConfigurable
    }

}