@file:Suppress("UNCHECKED_CAST")

package xerus.ktutil.javafx.ui

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import xerus.ktutil.javafx.*
import xerus.ktutil.javafx.properties.*
import xerus.ktutil.pair
import java.util.function.Predicate

class SearchView<T : Any>(val options: ObservableList<AnySearchable<T>>) : GridPane() {

    constructor(vararg searchables: AnySearchable<T>) : this(FXCollections.observableArrayList(*searchables))

    val conjunctions = arrayOf<Conjunction<T>>(
            Conjunction("and", { p1, p2 -> p1.and(p2) }),
            Conjunction("or", { p1, p2 -> p1.or(p2) }))

    val rows = FXCollections.observableArrayList<Row>().apply {
        addListener(ListChangeListener {
            children.clear()
            forEachIndexed { index, row -> addRow(index, *row.components) }
        })
    }
    val predicate = MyBinding({
		rows.map { it.predicate }.reduceIndexed { ind, r1, r2 -> rows[ind].conjunction.value.converter(r1, r2) }
	})

    val shortMenu = ContextMenu(MenuItem("Add row", { addRow() }))
    val contextMenu = ContextMenu(MenuItem("Add row", { addRow() }),
            MenuItem("Remove row", { rows.removeAt(GridPane.getRowIndex(scene.focusOwner)) }))

    init {
        if (options.size > 0)
            addRow(selected = 0)
        else
            options.addOneTimeListener { addRow(selected = 0) }
    }

    fun addRow(selected: Int? = null) {
        val row = Row(selected?.let { options[it] } ?: run {
            val map = rows.map { it.get() }
            options.find { !map.contains(it) }
        } ?: options[0])
        rows.add(row)
        predicate.addDependencies(row.conjunction, row.predicateProperty)
    }

    inner class Row(searchable: AnySearchable<T>) : SearchRow<T>() {
        private val myContextMenu = if (rows.size == 0) shortMenu else contextMenu

        val conjunction: ObservableValue<Conjunction<T>>
        val components: Array<Control>

        init {
            val (connector, con) =
                    if (rows.size == 0)
                        Label().apply { prefWidth = 0.0 }.pair { ConstantObservable(conjunctions[0]) }
                    else
                        ComboBox<Conjunction<T>>(UnmodifiableObservableList(*conjunctions)).apply { selectionModel.select(0) }.pair { valueProperty() }
            conjunction = con

            val option = ComboBox(options).select(searchable)
            components = arrayOf(connector, option, conditionBox, Label())
            components.forEach {
                it.contextMenu = myContextMenu
            }

            searchFieldProperty.addListener { _, _, field ->
                children.remove(components[3])
                add(field, 3, GridPane.getRowIndex(components[3]) ?: rows.size)
                components[3] = field
                field.contextMenu = myContextMenu
            }

            bind(option.valueProperty())
        }

        /*private fun addField() {
            predicate.unbind()
            val value = selected.get() ?: return
            val (field, filter) = value.type.representation()
            children.remove(components[3])
            field.maxWidth = Double.MAX_VALUE
            add(field, 3, GridPane.getRowIndex(components[3]) ?: rows.size)
            GridPane.setHgrow(field, Priority.ALWAYS)
            components[3] = field
            conditionBox.items = (value.type as Type<Any>).conditions
            conditionBox.value = conditionBox.items[0]

            predicate.bind({
                Predicate {
                    if (filter.value == null)
                        true
                    else {
                        val computed = value(it) ?: return@Predicate false
                        val test = { input: Any -> (conditionBox.value as Condition<Any>).predicate(input, filter.value!!) }
                        if (computed is Iterable<*>)
                            computed.any { if (it != null) test(it) else false }
                        else
                            test(computed)
                    }
                }
            }, conditionBox.valueProperty(), filter)
            components[3].contextMenu = myContextMenu
        }*/
    }

}

val alwaysTruePredicate = Predicate<Any> { true }

open class SearchRow<T : Any>(searchable: AnySearchable<T>? = null) : SimpleObjectProperty<AnySearchable<T>>(searchable) {
    val conditionBox = ComboBox<Condition<Any>>()

    val searchFieldProperty = SimpleObjectProperty<Control>()
    val searchField: Control
        get() = searchFieldProperty.get()

    val predicateProperty = SimpleObjectProperty<Predicate<T>>()
    val predicate: Predicate<T>
        get() = predicateProperty.get()

    init {
        this.addListener { _ -> addField() }
        conditionBox.maxWidth = Double.MAX_VALUE
        if (searchable != null)
            addField()
    }

    protected fun addField() {
        predicateProperty.unbind()
        val searchable = value ?: return

        val (field, filter) = searchable.type.representation()
        searchFieldProperty.value = field
        field.maxWidth = Double.MAX_VALUE
        HBox.setHgrow(field, Priority.SOMETIMES)
        GridPane.setHgrow(field, Priority.SOMETIMES)

        conditionBox.items = (searchable.type as Type<Any>).conditions
        conditionBox.value = conditionBox.items[0]

        predicateProperty.bind({
            filter.value?.let {
                Predicate { value: T ->
                    val computed = searchable(value) ?: return@Predicate false
                    val test = { input: Any -> conditionBox.value.predicate(input, filter.value!!) }
                    if (computed is Iterable<*>)
                        computed.any { if (it != null) test(it) else false }
                    else
                        test(computed)
                }
            } ?: alwaysTruePredicate as Predicate<T>
        }, conditionBox.valueProperty(), filter)
    }
}

class Condition<in T>(val name: String, val predicate: (T, T) -> Boolean) : (T, T) -> Boolean by predicate {

    override fun toString() = name

    companion object {
        val contains = Condition<Any>("contains", { content, filter -> content.toString().contains(if (filter is Number && filter.toDouble().rem(1.0) == 0.0) filter.toLong().toString() else filter.toString(), true) })
        fun <T> equals(name: String = "is", predicate: (T, T) -> Boolean = { s1, s2 -> s1 == s2 }) = Condition<T>(name, predicate)
        fun <T> contains(toString: (T) -> String = { it.toString() }) = Condition<T>("contains", { content, filter -> toString(content).contains(toString(filter), true) })

        /** creates a Condition that compares the two objects via [Comparable.compareTo] and yields true if the first one is bigger */
        fun <T : Comparable<T>> larger(name: String) = Condition<T>(name, { s1, s2 -> s1 > s2 })

        /** creates a Condition that compares the two objects via [Comparable.compareTo] and yields true if the first one is smaller */
        fun <T : Comparable<T>> smaller(name: String) = Condition<T>(name, { s1, s2 -> s1 < s2 })
    }
}

class Conjunction<T : Any>(val name: String, val converter: (Predicate<T>, Predicate<T>) -> Predicate<T>) : (Predicate<T>, Predicate<T>) -> Predicate<T> by converter {
    override fun toString() = name
}

// region Types

abstract class Type<T> {
    abstract val representation: () -> Pair<Control, ObservableValue<out T?>>
    abstract val conditions: ObservableList<Condition<T>>

    companion object {
        fun <T> create(representation: () -> Pair<Control, ObservableValue<T?>>, vararg conditions: Condition<T>): Type<T> =
                SimpleType(representation, *conditions)

        val TEXT = create({ TextField().pair { textProperty() } }
                , Condition.contains()
                , Condition("contains not", { content, filter -> !content.contains(filter, true) })
                , Condition.equals { content, filter -> content.equals(filter, true) }
        )

        val INT = NumberType { intSpinner().apply { editor.text = "" }.pair { optionalProperty() } }
        val DOUBLE = NumberType { doubleSpinner().apply { editor.text = "" }.pair { optionalProperty() } }

        val DATE = TimeType { DatePicker().pair { valueProperty() } }
        val TIME = TimeType { TimeSpinner().pair { optionalProperty() } }

        val LENGTH = create({ TimeSpinner().pair { optionalProperty() } }
                , Condition.equals()
                , Condition.larger("longer than")
                , Condition.smaller("shorter than"))
    }

    private class SimpleType<T>(override val representation: () -> Pair<Control, ObservableValue<T?>>, vararg conditions: Condition<T>) : DefaultType<T>(*conditions)
}

abstract class DefaultType<T>(vararg conditions: Condition<T>) : Type<T>() {
    override val conditions = UnmodifiableObservableList(*conditions)
}

class NumberType<T>(override val representation: () -> Pair<Control, ObservableValue<T?>>) : Type<T>() where T : Comparable<T>, T : Number {
    override val conditions = UnmodifiableObservableList<Condition<T>>(
            Condition.contains { if (it.toDouble().rem(1.0) == 0.0) it.toLong().toString() else it.toString() }
            , Condition.equals()
            , Condition.larger("more than")
            , Condition.smaller("less than")
    )
}

class TimeType<T : Comparable<T>>(override val representation: () -> Pair<Control, ObservableValue<T?>>) : Type<T>() {
    override val conditions = UnmodifiableObservableList<Condition<T>>(
            Condition.equals()
            , Condition.larger("after")
            , Condition.smaller("before")
    )
}

//endregion

// region Searchable

/** DO NOT USE DIRECTLY - this interface is for abstraction usage only */
interface AnySearchable<in T> {
    val type: Type<*>

    /** this MUST return either a value compatible with the Type or an Iterable of such, of which then any has to match for the Searchable to match */
    val converter: (T) -> Any?

    operator fun invoke(arg: T): Any? = converter(arg)

    /** how this Searchable will be named to the User */
    override fun toString(): String
}

/** a Searchable representing a Single Value */
interface ISearchable<in T, U> : AnySearchable<T> {
    override val type: Type<U>
    override val converter: (T) -> U?
}

/** a Searchable representing an Iterable */
interface IMultiSearchable<in T, U> : AnySearchable<T> {
    override val type: Type<U>
    override val converter: (T) -> Iterable<U?>?
}

class Searchable<in T, U>(val name: String, override val type: Type<U>, override val converter: (T) -> U?) : ISearchable<T, U> {
    override fun toString() = name
}

class MultiSearchable<in T, U>(val name: String, override val type: Type<U>, override val converter: (T) -> Iterable<U?>?) : IMultiSearchable<T, U> {
    override fun toString() = name
}

class SearchableColumn<T, U : Any>(val name: String, override val type: Type<U>, override val converter: (T) -> U?, private val display: (T) -> String? = { converter(it)?.toString() }) : TableColumn<T, String>(name), AnySearchable<T> {
    init {
        setCellValueFactory { ConstantObservable(display(it.value)) }
    }

    override fun toString() = name
}

//endregion
