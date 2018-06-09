package xerus.util.ui;

import javafx.scene.control.Alert.AlertType;

public interface ShowError {
	
	default void showError(Throwable error, String... title) {
		showMessage(error.toString(), title.length > 0 ? title[0] : "Error", AlertType.ERROR);
		error.printStackTrace();
	}
	
	void showMessage(Object message, String title, AlertType type);
	
}
