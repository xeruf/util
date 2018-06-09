package xerus.util;

import xerus.util.swing.SwingTools;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.text.JTextComponent;

public interface SwingSetting extends Setting {
	
	/**
	 * the JTextComponent is set to the current value of this Setting<br>
	 * subsequent changes to the field will change this setting too
	 */
	default JTextComponent addField(JTextComponent comp) {
		comp.setText(get());
		SwingTools.addChangeListener(comp, c -> put(comp.getText()));
		return comp;
	}
	
	/**
	 * the JComboBox is set to the current value of this setting<br>
	 * subsequent changes to the Combobox will change this setting too
	 */
	default <E> JComboBox<E> addField(JComboBox<E> comp) {
		comp.setSelectedItem(get());
		SwingTools.addChangeListener(comp, a -> put(comp.getSelectedItem()));
		return comp;
	}
	
	/**
	 * the JCheckBox is set to the current value of this setting<br>
	 * subsequent changes to the it will change this setting too
	 */
	default JCheckBox addField(JCheckBox comp) {
		comp.setSelected(getBool());
		SwingTools.addChangeListener(comp, a -> put(comp.isSelected()));
		return comp;
	}
	
	/** changes to the JSpinner will change this setting too */
	default JSpinner addField(JSpinner comp) {
		SwingTools.addChangeListener(comp, a -> put(comp.getValue()));
		return comp;
	}
	
	default JCheckBox addCheckbox(String title, String key) {
		JCheckBox box = new JCheckBox(title, getMulti(key));
		box.addActionListener(a -> putMulti(key, box.isSelected()));
		return box;
	}

}
