@file:Suppress("UNCHECKED_CAST")

package xerus.ktutil.javafx.ui.controls

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
import xerus.ktutil.javafx.properties.ImmutableObservable
import xerus.ktutil.javafx.properties.ImmutableObservableList
import xerus.ktutil.javafx.properties.MyBinding
import xerus.ktutil.javafx.properties.addOneTimeListener
import xerus.ktutil.javafx.properties.bind
import xerus.ktutil.pair
import java.util.function.Predicate

class SearchView<T: Any>(val options: ObservableList<AnySearchable<T>>): GridPane() {
	
	constructor(vararg searchables: AnySearchable<T>): this(FXCollections.observableArrayList(*searchables))
	
	val conjunctions = arrayOf<Conjunction<T>>(
		Conjunction("and") { p1, p2 -> p1.and(p2) },
		Conjunction("or") { p1, p2 -> p1.or(p2) })
	
	val rows = FXCollections.observableArrayList<Row>().apply {
		addListener(ListChangeListener {
			children.clear()
			forEachIndexed { index, row -> addRow(index, *row.components) }
		})
	}
	val predicate = MyBinding({
		rows.map { it.predicate }.reduceIndexed { ind, r1, r2 -> rows[ind].conjunction.value.converter(r1, r2) }
	})
	
	val shortMenu = ContextMenu(MenuItem("Add row") { addRow() })
	val contextMenu = ContextMenu(MenuItem("Add row") { addRow() },
		MenuItem("Remove row") { rows.removeAt(GridPane.getRowIndex(scene.focusOwner)) })
	
	init {
		if(options.size > 0)
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
	
	inner class Row(searchable: AnySearchable<T>): SearchRow<T>() {
		private val myContextMenu = if(rows.size == 0) shortMenu else contextMenu
		
		val conjunction: ObservableValue<Conjunction<T>>
		val components: Array<Control>
		
		init {
			val (connector, con) =
				if(rows.size == 0)
					Label().apply { prefWidth = 0.0 }.pair { ImmutableObservable(conjunctions[0]) }
				else
					ComboBox<Conjunction<T>>(ImmutableObservableList(*conjunctions)).apply { selectionModel.select(0) }.pair { valueProperty() }
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
	}
	
}

val alwaysTruePredicate = Predicate<Any> { true }

open class SearchRow<T: Any>(searchable: AnySearchable<T>? = null): SimpleObjectProperty<AnySearchable<T>>(searchable) {
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
		if(searchable != null)
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
					if(computed is Iterable<*>)
						computed.any { if(it != null) test(it) else false }
					else
						test(computed)
				}
			} ?: alwaysTruePredicate as Predicate<T>
		}, conditionBox.valueProperty(), filter)
	}
}

class Condition<in T>(val name: String, val predicate: (T, T) -> Boolean): (T, T) -> Boolean by predicate {
	
	override fun toString() = name
	
	companion object {
		val contains = Condition<Any>("contains") { content, filter -> content.toString().contains(if(filter is Number && filter.toDouble().rem(1.0) == 0.0) filter.toLong().toString() else filter.toString(), true) }
		fun <T> equals(name: String = "is", predicate: (T, T) -> Boolean = { s1, s2 -> s1 == s2 }) = Condition<T>(name, predicate)
		fun <T> contains(toString: (T) -> String = { it.toString() }) = Condition<T>("contains") { content, filter -> toString(content).contains(toString(filter), true) }
		
		/** Creates a Condition that compares the two objects via [Comparable.compareTo] and yields true if the first one is bigger. */
		fun <T: Comparable<T>> larger(name: String) = Condition<T>(name) { s1, s2 -> s1 > s2 }
		
		/** Creates a Condition that compares the two objects via [Comparable.compareTo] and yields true if the first one is smaller. */
		fun <T: Comparable<T>> smaller(name: String) = Condition<T>(name) { s1, s2 -> s1 < s2 }
	}
}

class Conjunction<T: Any>(val name: String, val converter: (Predicate<T>, Predicate<T>) -> Predicate<T>): (Predicate<T>, Predicate<T>) -> Predicate<T> by converter {
	override fun toString() = name
}

// region Types

/** A type of a Searchable value.
 * This defines the way it is displayed, as well as the [Condition]s that can be applied to it and how they are labeled. */
abstract class Type<T> {
	abstract val representation: () -> Pair<Control, ObservableValue<out T?>>
	abstract val conditions: ObservableList<Condition<T>>
	
	companion object {
		fun <T> create(representation: () -> Pair<Control, ObservableValue<T?>>, vararg conditions: Condition<T>): Type<T> =
			SimpleType(representation, *conditions)
		
		val TEXT = create({ TextField().pair { textProperty() } },
			Condition.contains(),
			Condition("contains not") { content, filter -> !content.contains(filter, true) },
			Condition.equals { content, filter -> content.equals(filter, true) }
		)
		
		val INT = NumberType { intSpinner().apply { editor.text = "" }.pair { optionalValue() } }
		val DOUBLE = NumberType { doubleSpinner().apply { editor.text = "" }.pair { optionalValue() } }
		
		val DATE = TimeType { DatePicker().pair { valueProperty() } }
		val TIME = TimeType { TimeSpinner().pair { optionalValue() } }
		
		val LENGTH = create({ TimeSpinner().pair { optionalValue() } },
			Condition.equals(),
			Condition.larger("longer than"),
			Condition.smaller("shorter than")
		)
	}
	
	private class SimpleType<T>(override val representation: () -> Pair<Control, ObservableValue<T?>>, vararg conditions: Condition<T>): DefaultType<T>(*conditions)
}

abstract class DefaultType<T>(vararg conditions: Condition<T>): Type<T>() {
	override val conditions = ImmutableObservableList(*conditions)
}

class NumberType<T>(override val representation: () -> Pair<Control, ObservableValue<T?>>): Type<T>() where T: Comparable<T>, T: Number {
	override val conditions = ImmutableObservableList<Condition<T>>(
		Condition.contains { if(it.toDouble().rem(1.0) == 0.0) it.toLong().toString() else it.toString() },
		Condition.equals(),
		Condition.larger("more than"),
		Condition.smaller("less than")
	)
}

class TimeType<T: Comparable<T>>(override val representation: () -> Pair<Control, ObservableValue<T?>>): Type<T>() {
	override val conditions = ImmutableObservableList<Condition<T>>(
		Condition.equals(),
		Condition.larger("after"),
		Condition.smaller("before")
	)
}

//endregion

// region Searchable

/** DO NOT USE DIRECTLY - this interface is for abstraction only. */
interface AnySearchable<in T> {
	val type: Type<*>
	
	/** Returns the value for the Searchable to match.
	 * This has to be either a value compatible with the Type or an Iterable of such, in case of the latter only one element has to match to match this Searchable. */
	val converter: (T) -> Any?
	
	operator fun invoke(arg: T): Any? = converter(arg)
	
	/** The name of this Searchable, as is it displayed to the User. */
	override fun toString(): String
}

/** A Searchable representing a single value. */
interface ISearchable<in T, U>: AnySearchable<T> {
	override val type: Type<U>
	override val converter: (T) -> U?
}

/** A Searchable representing an Iterable. */
interface IMultiSearchable<in T, U>: AnySearchable<T> {
	override val type: Type<U>
	override val converter: (T) -> Iterable<U?>?
}

class Searchable<in T, U>(private val name: String, override val type: Type<U>, override val converter: (T) -> U?): ISearchable<T, U> {
	override fun toString() = name
}

class MultiSearchable<in T, U>(private val name: String, override val type: Type<U>, override val converter: (T) -> Iterable<U?>?): IMultiSearchable<T, U> {
	override fun toString() = name
}


/** A [TableColumn] that can be searched through via a [SearchView].
 * @param S The type of the TableView generic type.
 * @param T The type of the content in all cells in this TableColumn.
 * @param U The type that is used for searching. */
open class SearchableColumn<S, T, U: Any>(private val name: String, override val type: Type<U>, override val converter: (S) -> U?, private val display: (S) -> T?): TableColumn<S, T>(name), ISearchable<S, U> {
	
	init {
		setCellValueFactory { ImmutableObservable(display(it.value)) }
	}
	
	override fun toString() = name
	
	companion object {
		/** A [SearchableColumn] that uses the same value for displaying and sorting. */
		fun <S, T: Any> simple(name: String, type: Type<T>, converter: (S) -> T?) =
			SearchableColumn<S, T, T>(name, type, converter, converter)
		
		/** A [SearchableColumn] that displays its content via a String, by default from the [toString] method of the search value. */
		fun <S, U: Any> text(name: String, type: Type<U>, converter: (S) -> U?, display: (S) -> String? = { converter(it)?.toString() }) =
			SearchableColumn<S, String, U>(name, type, converter, display)
	}
}

//endregion
