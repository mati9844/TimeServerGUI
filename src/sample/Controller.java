package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.LinkedList;

public class Controller {

    public static Controller CONTROLLER;

    public Controller(){
        CONTROLLER = this;
    }

    @FXML
    public TextArea consoleTextArea;

    @FXML
    private Button ButtonRefresh, connectButton;

    @FXML
    public ChoiceBox<String> ChoiceBox;

    @FXML
    private TextField FrequencyField;

    @FXML
    private Label connectionLabel;

    @FXML
    private Label frequencyLabel;

    @FXML
    public void initialize() {
        ChoiceBox.getItems().addAll(Main.client.offers);

    }

    public void setTextArea(String s)
    {
        consoleTextArea.appendText(s);
    }

    public void clearChoiceBox(){
        Platform.runLater(() ->ChoiceBox.getItems().removeAll());
    }

    public void addItemsChoiceBox(String x){
        Platform.runLater(() ->ChoiceBox.getItems().add(x));
    }
    @FXML
    private void handleButtonRefreshOnAction(ActionEvent event) {
        Main.client.offers = new LinkedList<>();
        ChoiceBox.getItems().removeAll();
        Main.client.updateOffers();
        //Platform.runLater(()->ChoiceBox.getItems().addAll(Main.client.offers));

    }

    public void appendText(String str) {
        Platform.runLater(() -> consoleTextArea.appendText(str+'\n'));
    }

    @FXML
    private void handleStopButtonOnAction(ActionEvent event) {
        if(Main.tcp != null) {
            Main.tcp.stopClient();
            connectionLabel.setText("NoConnection");
        }
    }

    @FXML
    private void handleConnectButtonOnAction(ActionEvent event) {
        try{
            int frequency = Integer.parseInt(FrequencyField.getText());
            if(frequency<= 1  || frequency >= 1000){
                frequencyLabel.setText("This value must be between 1 and 1000");
            }else{
                frequencyLabel.setText("");

            }

        if(frequency>1 && frequency<1000) {
            String address = ChoiceBox.getSelectionModel().getSelectedItem();

            if (Main.tcp != null)
                Main.tcp.stopClient();

            consoleTextArea.appendText("ATTEMPTING TO CONNECT TO THE SERVER" + '\n');
            Main.tcp = new TCP(address, frequency);
            Main.tcp.start();
            connectionLabel.setText(Main.tcp.getAddress());
        }
        }catch(Exception e){
            frequencyLabel.setText("This field must be an integer");

        }
    }
    @FXML
    private void handleChoiceBoxOnDragDetected(ActionEvent event){

    }



}
