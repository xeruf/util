package xerus.util.swing.bases;

import xerus.util.ui.ShowError;

import javafx.scene.control.Alert.AlertType;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.Component;

public interface SwingShowError extends ShowError {
	
	default void showMessage(Object message, String title, String type) {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(dialog, message, title, MessageType.valueOf(type.toUpperCase()).id);
	}
	
	default void showMessage(Object message, String title, AlertType type) {
		try {
			JOptionPane.showMessageDialog((Component) this, message, title, (int) JOptionPane.class.getField(type.name() + "_MESSAGE").get(null));
		} catch(IllegalAccessException|NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	enum MessageType {
		ERROR(AlertType.ERROR, JOptionPane.ERROR_MESSAGE), WARN(AlertType.WARNING, JOptionPane.WARNING_MESSAGE), INFO(AlertType.INFORMATION, JOptionPane.INFORMATION_MESSAGE);
		final AlertType type;
		final int id;
		MessageType(AlertType type, int id) {
			this.type = type;
			this.id = id;
		}
	}
	
}
