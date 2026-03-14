package ca.ucalgary.seng300.demos;

import ca.ucalgary.seng300.platform.domain.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.swing.*;


public class ChatSystemDemoController {
    @FXML
    private TextArea chatWindow;

    @FXML
    private TextField messageBox;


    @FXML protected void messageSent(ActionEvent event) {
        Message m = new Message(messageBox.getText());
        chatWindow.setText(
                String.format("%s\n<%s> %s",
                        chatWindow.getText(), m.author, m.content)
        );
        messageBox.clear();
    }

}
