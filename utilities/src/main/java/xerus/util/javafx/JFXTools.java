package xerus.util.javafx;

import xerus.util.tools.Tools;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class JFXTools {
	
	/** returns a recursively composed Collection with Nodes that match the given css StyleClass */
	public static Collection<Styleable> findByStyleClass(Styleable root, String className) {
		return find(root, node -> node.getStyleClass().contains(className));
	}
	
	/** returns a recursively composed Collection with Nodes that are instances of the given Class */
	public static <T> Collection<T> findByClass(Styleable root, Class<T> c) {
		return (Collection<T>) find(root, c::isInstance);
	}
	
	/** returns a recursively composed Collection with Nodes that match the predicate */
	public static Collection<Styleable> find(Styleable root, Predicate<Styleable> p) {
		Collection<Styleable> found = new HashSet<>();
		findRec(root, p, found);
		return found;
	}
	
	public static void dump(Styleable root, Function<Styleable,String> f) {
		findRec(root, node -> {
			String s = f.apply(node);
			if (s != null)
				System.out.println(s);
			return false;
		}, null);
	}
	
	/** recursively fills the given Collection with Nodes that match the predicate */
	public static void findRec(Styleable node, Predicate<Styleable> p, Collection<Styleable> col) {
		if (node == null)
			return;
		if (p.test(node))
			col.add(node);
		if (node instanceof TabPane)
			for (Tab tab : ((TabPane) node).getTabs())
				findRec(tab.getContent(), p, col);
		else if (node instanceof MenuBar)
			for (Styleable child : ((MenuBar) node).getMenus())
				findRec(child, p, col);
		else if (node instanceof Menu)
			for (Styleable child : ((Menu) node).getItems())
				findRec(child, p, col);
		else if (node instanceof Parent)
			for (Node child : ((Parent) node).getChildrenUnmodifiable())
				findRec(child, p, col);
	}
	
	// css Metadata
	
	public static void printCSS(Node n) {
		printCSS(n, "");
	}
	
	public static void printCSS(Node n, String filter) {
		for (CssMetaData style : n.getCssMetaData())
			if (style.getProperty().contains(filter))
				System.out.println(toString(style, n));
	}
	
	public static void printCSS(Collection<CssMetaData<? extends Styleable,?>> styles, String filter) {
		for (CssMetaData style : styles)
			if (style.getProperty().contains(filter))
				System.out.println(style);
	}
	
	public static String toString(CssMetaData style, Node n) {
		StringBuilder res = new StringBuilder(String.format("%20s: %s", style.getProperty(), Tools.toString(style.getInitialValue(n))));
		List<CssMetaData> properties = style.getSubProperties();
		if (properties != null && properties.size() > 0) {
			res.append(" - sub: [");
			for (CssMetaData prop : properties) {
				res.append(toString(prop, n));
				res.append(", ");
			}
			res.setLength(res.length() - 2);
			res.append(']');
		}
		return res.toString();
	}
	
}
