package genie.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** A reusable chat bubble for a user or Genie message. */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        setMaxWidth(Double.MAX_VALUE);
    }

    /** Creates a right-aligned user message with the user's profile image. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.setAlignment(Pos.TOP_RIGHT);
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /** Creates a left-aligned Genie message with the bot's profile image. */
    public static DialogBox getGenieDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        ObservableList<Node> children = FXCollections.observableArrayList(box.getChildren());
        Collections.reverse(children);
        box.getChildren().setAll(children);
        box.setAlignment(Pos.TOP_LEFT);
        box.dialog.getStyleClass().add("reply-label");
        box.getStyleClass().add("genie-dialog");
        return box;
    }
}
