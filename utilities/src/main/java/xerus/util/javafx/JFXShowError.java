package xerus.util.javafx;

import xerus.util.ui.ShowError;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public interface JFXShowError extends ShowError {
	
	@Override
	default void showMessage(Object message, String title, Alert.AlertType type) {
		Alert alert = new Alert(type);
		if (this instanceof Window)
			alert.initOwner((Window) this);
		alert.setTitle(title);
		alert.setContentText(message.toString());
		alert.show();
	}
	
}
