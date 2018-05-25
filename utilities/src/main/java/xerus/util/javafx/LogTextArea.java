package xerus.util.javafx;

import xerus.util.ui.LogArea;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogTextArea extends VBox implements LogArea {
	
	public TextArea textArea;
	
	public LogTextArea() {
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setWrapText(true);
		
		getChildren().add(textArea);
		VBox.setVgrow(textArea, Priority.ALWAYS);
	}
	
	@Override
	public void appendText(String s) {
		Platform.runLater(() -> textArea.appendText(s));
	}
	
}
