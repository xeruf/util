package xerus.util.swing.bases;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * JPanel implementation for a simple, dynamic interface using GridBagLayout
 */
public abstract class BasePanel extends JPanel implements SwingShowError {
	
	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException|InstantiationException|IllegalAccessException|UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	public BasePanel() {
		super(new GridBagLayout());
		SwingUtilities.invokeLater(this::registerComponents);
	}
	
	protected JFrame createFrame(String name) {
		BasePanel panel = this;
		return new BaseFrame() {
			protected void initGUI() {
				setContentPane(panel);
				setTitle(name);
			}
		};
	}
	
	/**
	 * called when a registered button gets pressed<br>
	 * empty implementation given for the case no buttons are present
	 * @param buttonid the id of the pressed button
	 * @throws Exception dispose annoying exceptions
	 */
	protected void buttonCall(int buttonid) throws Exception {
	}
	;

// COMPONENT REGISTRATION
	
	/**
	 * register your GUI-Components here, preferably through the provided {@code reg}-Methods
	 */
	protected abstract void registerComponents();
	
	/**
	 * registers a JComponent with the given constraints
	 * @param comp the component to register
	 * @param constraints 1: Column; 2: Column-width
	 * @return {@code comp}
	 */
	public JComponent reg(JComponent comp, Object... constraints) {
		add(comp, getConstraints(constraints));
		return comp;
	}
	
	/**
	 * registers a JComponent with the given constraints and tooltip
	 * @param comp the JComponent to register
	 * @param constraints 1: Column; 2: Column-width
	 * @return {@code comp}
	 */
	protected JComponent reg(JComponent comp, String tooltip, Object... constraints) {
		add(comp, getConstraints(constraints));
		comp.setToolTipText(tooltip);
		return comp;
	}
	
	/**
	 * registers a label
	 * @param text the Text to display on the Label
	 * @param constraints 1: Column; 2: Column-width
	 * @return the registered label
	 */
	protected JLabel regLabel(String text, Object... constraints) {
		JLabel label = new JLabel(text);
		add(label, getConstraints(constraints).multiplyWeights(0.2));
		return label;
	}
	
	protected JTextField regTextField(Object... constraints) {
		JTextField textfield = new JTextField();
		add(textfield, getConstraints(constraints));
		return textfield;
	}
	
	/**
	 * registers a button
	 * @param buttonid the id which will be given in {@link #buttonCall(int)} when the button gets clicked
	 * @param text the text to display on the button
	 * @param constraints 1: Column; 2: Column-width
	 * @return {@code button}
	 */
	protected JButton regButton(String text, int buttonid, GridConstraints constraints) {
		JButton button = new JButton();
		addButtonListener(button, buttonid);
		button.setText(text);
		reg(button, constraints);
		nextid = buttonid + 1;
		return button;
	}
	
	private int nextid = 0;
	/**
	 * registers a button with the id of the last button incremented by 1
	 * @param text the text to display on the button
	 * @param constraints 1: Column; 2: Column-width
	 * @return {@code button}
	 */
	protected JButton regButton(String text, Object... constraints) {
		return regButton(text, nextid, getConstraints(constraints));
	}
	
	/**
	 * registers a button with the given ActionListener and constraints
	 */
	protected JButton regButton(String text, ActionListener l, Object... constraints) {
		JButton button = new JButton(text);
		button.addActionListener(l);
		reg(button, constraints);
		return button;
	}
	
	protected void addButtonListener(JButton button, int buttonid) {
		button.addActionListener(evt -> {
			try {
				buttonCall(buttonid);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public void removeAll(Component... components) {
		for (Component comp : components)
			remove(comp);
	}

// FILECHOOSER
	
	protected JTextField fileChooserField;
	
	/**
	 * adds a FileChooser with a TextField and a Button to the layout
	 * @param name name of the window & button
	 */
	protected JFileChooser regFileChooser(String name) {
		return regFileChooser(name, null);
	}
	
	/**
	 * adds a FileChooser with a TextField and a Button to the layout
	 * @param name name of the window & button
	 * @param startdir optional directory to start the FileChooser in
	 */
	protected JFileChooser regFileChooser(String name, String startdir, FileFilter... filter) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choose " + name);
		if (filter.length > 0)
			chooser.setFileFilter(filter[0]);
		
		fileChooserField = new JTextField();
		fileChooserField.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				fileChooserField.setScrollOffset(1000000);
			}
		});
		if (startdir != null)
			fileChooserField.setText(startdir);
		reg(fileChooserField, "Currently selected Path", 0);
		
		JButton select = new JButton();
		select.setText("Choose " + name);
		select.addActionListener(evt -> {
			File f = new File(getFileChooserInput());
			while (f != null && !f.exists())
				f = f.getParentFile();
			if (f == null)
				f = new File(".");
			chooser.setCurrentDirectory(f);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				fileChooserField.setText(chooser.getSelectedFile().getPath());
		});
		reg(select, 1);
		return chooser;
	}
	
	public static final Path getPath(JTextComponent input) {
		return Paths.get(input.getText());
	}
	
	/**
	 * reads the current String in the FileChooser
	 * @return value of the FileChooser-TextField
	 */
	public String getFileChooserInput() {
		return fileChooserField.getText();
	}
	
	/**
	 * reads all lines of the currently selected File in the FileChooser
	 * @return List of all the lines of the file
	 */
	public List<String> getFileLines() throws IOException {
		return Files.readAllLines(getPath(fileChooserField));
	}

// CONSTRAINTS
	
	protected GridConstraints getConstraints(Object... constraints) {
		if (constraints.length == 0)
			return constraints();
		if (constraints[0] instanceof GridConstraints)
			return (GridConstraints) constraints[0];
		int[] newconstraints = new int[constraints.length];
		for (int i = 0; i < constraints.length; i++)
			newconstraints[i] = (int) constraints[i];
		return constraints(newconstraints);
	}
	
	public GridConstraints constraints(int... columns) {
		return new GridConstraints(columns);
	}
	
	/** Builder pattern class for creating {@link GridBagConstraints} */
	public static class GridConstraints extends GridBagConstraints implements Cloneable {
		
		/** @param columns 1: column 2: component width in columns */
		public GridConstraints(int... columns) {
			super();
			weightx = 1;
			weighty = 0.2;
			setPadding(2);
			setInsets(1);
			fill = BOTH;
			gridx = REMAINDER;
			gridwidth = REMAINDER;
			setColumns(columns);
		}
		
		/**
		 * convenient case-insensitive method to set the {@link GridBagConstraints#fill} value
		 * @param s "H" for {@code HORIZONTAL}, "V" for {@code VERTICAL}, combine for {@code BOTH}, null or empty String for {@code NONE}
		 */
		public GridConstraints setFill(String s) {
			if (s == null || s == "") {
				fill = NONE;
				return this;
			}
			switch (s.toUpperCase()) {
				case "H":
					fill = HORIZONTAL;
					break;
				case "V":
					fill = VERTICAL;
					break;
				case "HV":
				case "VH":
					fill = BOTH;
					break;
				default:
					throw new IllegalArgumentException(s + " is not a valid fill value!");
			}
			return this;
		}
		
		/**
		 * @param columns 1: column 2: component width in columns
		 */
		public GridConstraints setColumns(int... columns) {
			if (columns.length > 0) {
				gridx = columns[0];
				if (columns.length > 1)
					gridwidth = columns[1];
				else
					gridwidth = 1;
			}
			return this;
		}
		
		public GridConstraints setPadding(int pad) {
			ipadx = pad;
			ipady = pad;
			return this;
		}
		
		public GridConstraints multiplyWeights(double multiplier) {
			weightx *= multiplier;
			weighty *= multiplier;
			return this;
		}
		
		/**
		 * default weightx is 1, default weighty is 0.2
		 */
		public GridConstraints setWeight(double weight) {
			weightx = weight;
			weighty = weight;
			return this;
		}
		
		/**
		 * default weightx is 1, default weighty is 0.2<br>
		 * any parameter that is null will be left at the original value
		 */
		public GridConstraints setWeights(Double... weight) {
			if (weight != null && weight.length > 0) {
				if (weight[0] != null)
					weightx = weight[0];
				if (weight.length > 1)
					weighty = weight[1];
			}
			return this;
		}
		
		public GridConstraints setGridY(int y) {
			gridy = y;
			return this;
		}
		
		public GridConstraints setGridHeight(int height) {
			gridheight = height;
			return this;
		}
		
		public GridConstraints setInsets(int... margins) {
			switch (margins.length) {
				case 1:
					insets = new Insets(margins[0], margins[0], margins[0], margins[0]);
					break;
				case 2:
					insets = new Insets(margins[0], margins[1], margins[0], margins[1]);
					break;
				case 3:
					insets = new Insets(margins[0], margins[1], margins[0], margins[2]);
					break;
				case 4:
					insets = new Insets(margins[0], margins[1], margins[2], margins[3]);
					break;
			}
			return this;
		}
		
		public GridConstraints setAnchor(int anchor) {
			this.anchor = anchor;
			return this;
		}
		public GridConstraints setAnchorTop() {
			anchor = FIRST_LINE_START;
			return this;
		}
		
		public GridConstraints clone() {
			return (GridConstraints) super.clone();
		}
		
	}
	
}
