/******************************************************************************* Copyright (c) 2014 EM-SOFTWARE and
 * others. All rights reserved. This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Christoph Keimel <c.keimel></c.keimel>@emsw.de> - initial API and
 * implementation  */
package xerus.ktutil.javafx.ui

import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.CheckBoxTreeItem
import javafx.scene.control.TreeItem
import javafx.scene.control.cell.CheckBoxTreeCell
import xerus.ktutil.javafx.properties.bind
import xerus.ktutil.nullIfEmpty
import java.util.concurrent.Callable
import java.util.function.Predicate

/**
 * An extension of [TreeItem] with the possibility to filter its children. To enable filtering it is necessary to
 * set the [TreeItemPredicate]. If a predicate is set, then the tree item will also use this predicate to filter
 * its children (if they are of the type FilterableTreeItem).<br></br>
 *
 *
 * A tree item that has children will not be filtered. The predicate will only be evaluated if the TreeItem is a leaf.
 * Since the predicate is also set for the child tree items, the tree item in question can turn into a leaf if all of
 * its children are filtered out and [.autoLeaf] is set to true.
 *
 *
 * This class extends [CheckBoxTreeItem] so it can, but does not need to be, used in conjunction with
 * [CheckBoxTreeCell] cells.
 * @param <T> The type of the [value][.getValue] property within [TreeItem].
</T> */
class FilterableTreeItem<T>
/**
 * Creates a new [TreeItem] with filtered children.
 * @param value the value of the [TreeItem]
 */
(value: T) : CheckBoxTreeItem<T>(value) {
	
	/**
	 * Returns the list of children that is backing the filtered list.
	 * @return underlying list of children
	 */
	val internalChildren: ObservableList<TreeItem<T>> = FXCollections.observableArrayList<TreeItem<T>>()
	
	private val predicate = SimpleObjectProperty<TreeItemPredicate<T>?>()
	
	init {
		val filteredList = FilteredList<TreeItem<T>>(this.internalChildren)
		filteredList.predicateProperty().bind(Bindings.createObjectBinding<Predicate<TreeItem<T>>>(Callable {
			Predicate { child: TreeItem<T> ->
				val result = predicate.get()?.invoke(this, child.value) ?: true
				// Set the predicate of child items to force filtering
				val filterableChild = (child as? FilterableTreeItem<T>)?.also {
					it.setPredicate(if (keepSubitems && result) null else predicate.get())
				}
				// If there is no predicate, keep this tree item
				if (this.predicate.get() == null) {
					if (autoExpand)
						child.isExpanded = false
					return@Predicate true
				}
				// If there are children, keep this tree item
				if (child.children.size > 0) {
					if (autoExpand)
						child.isExpanded = true
					return@Predicate true
				}
				if (!autoLeaf && filterableChild != null && filterableChild.internalChildren.size > 0)
					return@Predicate false
				result
			}
		}, this.predicate))
		
		setHiddenFieldChildren(filteredList)
	}
	
	/**
	 * Set the hidden private field [TreeItem.children] through reflection and hook the hidden
	 * [ListChangeListener] in [TreeItem.childrenListener] to the list
	 * @param list the list to set
	 */
	private fun setHiddenFieldChildren(list: ObservableList<TreeItem<T>>) {
		try {
			val childrenField = TreeItem::class.java.getDeclaredField("children")
			childrenField.isAccessible = true
			childrenField.set(this, list)
			
			val declaredField = TreeItem::class.java.getDeclaredField("childrenListener")
			declaredField.isAccessible = true
			@Suppress("UNCHECKED_CAST")
			list.addListener(declaredField.get(this) as ListChangeListener<TreeItem<T>>)
		} catch (e: Exception) {
			e.printStackTrace()
		}
		
	}
	
	/** @return the predicate property */
	fun predicateProperty() = this.predicate
	
	/** @return the predicate */
	fun getPredicate() = this.predicate.get()
	
	/** Set the predicate */
	fun setPredicate(predicate: TreeItemPredicate<T>?) = this.predicate.set(predicate)
	
	/** Create and set the predicate */
	fun bindPredicate(filter: (T) -> Boolean, vararg dependencies: Observable) {
		predicate.bind({ { _, value -> filter.invoke(value) } }, *dependencies)
	}
	
	/** Establishes a Binding to that Property that defaults to filtering the value using the string, ignoring case
	 * if the current String of the Property is empty, the Predicate is automatically set to zero */
	fun bindPredicate(property: Property<String>, function: (T, String) -> Boolean = { value, text -> value.toString().contains(text, true) }) {
		predicate.bind({ property.value.nullIfEmpty()?.let { text -> { _, value -> function(value, text) } } }, property)
	}
	
	companion object {
		/** when true, items with children, but all filtered out, will turn into leafs  */
		var autoLeaf = true
		/** when true, subitems of matched items will automatically be kept  */
		var keepSubitems = true
		/** when true, items will collapse when the [.predicate] is null and expand when it is not  */
		var autoExpand = false
	}
	
}

typealias TreeItemPredicate<T> = (TreeItem<T>, T) -> Boolean
