package xerus.util.ui;

import xerus.util.javafx.LogTextArea;
import xerus.util.swing.JLog;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Changelog {
	
	private String[] notes;
	private Map<String,String[]> changelog;
	
	public Changelog(String... notes) {
		changelog = new LinkedHashMap<>();
		this.notes = notes;
	}
	
	public void addVersion(String version, String... changes) {
		this.changelog.put(version, changes);
	}
	
	public void show(JFrame parent) {
		JLog text = new JLog();
		appendLog(text);
		JScrollPane scrollPane = text.get();
		SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
		
		JDialog dialog = new JDialog(parent);
		dialog.getContentPane().add(scrollPane);
		dialog.setTitle("Changelog");
		dialog.setSize(600, 400);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}
	
	public void show(Window parent) {
		LogTextArea text = new LogTextArea();
		appendLog(text);
		Platform.runLater(() -> text.textArea.setScrollTop(0));
		
		Stage stage = new Stage();
		Scene scene = new Scene(text);
		stage.initOwner(parent);
		stage.setScene(scene);
		stage.setTitle("Changelog");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/paper.png")));
		stage.setMinHeight(300);
		if (parent instanceof Stage) {
			Stage s = ((Stage) parent);
			if (s.getScene() != null)
				Bindings.bindContent(scene.getStylesheets(), s.getScene().getStylesheets());
		}
		stage.show();
	}
	
	private void appendLog(LogArea text) {
		if (notes.length > 0) {
			text.appendAll("", notes);
			text.appendln();
		}
		for (Entry<String,String[]> e : changelog.entrySet()) {
			text.appendln(e.getKey());
			text.appendAll(" - ", e.getValue());
			text.appendln();
		}
	}
	
}
