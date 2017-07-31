package Cipheroni;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;

import java.util.HashMap;
import java.util.Map;

public class Controller {

    // FXML elements
    @FXML
    TextArea txaMessage;
    @FXML
    TextArea txaKey;
    @FXML
    TextArea txaResult;
    @FXML
    Button btnHelp;
    @FXML
    RadioButton chkBea;
    @FXML
    RadioButton chkVig;
    @FXML
    Button btnEnode;
    @FXML
    Button btnDecode;
    @FXML
    CheckBox chkPunctuation;

    // our alphabet
    private HashMap<Character,Integer> alphabet = new HashMap<>();

    // super method that gets called when the fxml file is loaded
    public void initialize() {
        fillMapCaps(alphabet);
    }

    // self explanatory
    @FXML
    void btnEncode_OnClick(ActionEvent actionEvent) {

        // is the message longer than the key?
        if ( txaMessage.getText().length() > txaKey.getText().length() ) {

            // convert the message into a char-array for processing
            char[] msgChar = txaMessage.getText().toUpperCase().toCharArray();

            // get the key twice, once for processing, once for adding to itself
            // (otherwise it would double every time we add to it)
            String keyString = txaKey.getText().toUpperCase();
            String addString = keyString;

            // if the key is smaller than the message, pad the key with itself
            if ( keyString.length() < msgChar.length ) {
                // how often the entire key can be added to itself for padding
                int times = (int) Math.floor( msgChar.length / keyString.length() );
                for (; times > 1; times--) {
                    // add the key to itself
                    keyString += addString;
                }
                // is it enough padding yet?
                if ( keyString.length() < msgChar.length ) {
                    // get how many chars need to be added until the key is full
                    times = msgChar.length - keyString.length();
                    for (int c = 0; c < times; c++ ) {
                        // add them in correct order
                        keyString += keyString.charAt(c);
                    }
                }
            }
            // convert the key to a char-array for processing
            char[] keyChar = keyString.toCharArray();

            // converting the message and key to int
            int[] msgInt = new int[msgChar.length];
            int[] keyInt = new int[keyChar.length];

            // the converted message is stored in these
            int[] resultInt = new int[keyChar.length];
            char[] resultChar = new char[keyChar.length];

            // convert the message to its arithmetic counterpart
            for ( int c = 0; c < msgChar.length; c++ ) {
                msgInt[c] = alphabet.get(msgChar[c]);
                keyInt[c] = alphabet.get(keyChar[c]);

                if (msgChar[c] == ' ') {

                }

                // the Beaufort algorithm
                if ( chkBea.isSelected() ) {
                    resultInt[c] = keyInt[c] - msgInt[c];
                    if (keyInt[c] - msgInt[c] < 0) {
                        resultInt[c] += 26;
                    }
                // the Vigenère algorithm
                } else if ( chkVig.isSelected() ) {
                    resultInt[c] = (keyInt[c] + msgInt[c]) % 26;
                } else {
                    // no algorithm is selected, the sun is about to implode
                    error(2);
                }
                // reverse (mis-)use the HashMap to get the char paired with the values
                // to get the ciphered message as a String
                resultChar[c] = getKeyByValue(alphabet, resultInt[c]);
            }
            // display the ciphered message
            txaResult.setText(String.valueOf(resultChar));

        } else {
            // the key is longer than the message
            error(1);
        }
    }

    // self explanatory
    @FXML
    void btnDecode_OnClick(ActionEvent actionEvent) {
        if ( txaMessage.getText().length() > txaKey.getText().length() ) {
            char[] msgChar = txaMessage.getText().toUpperCase().toCharArray();
            String keyString = txaKey.getText().toUpperCase();
            String addString = keyString;

            // if the key is smaller than the message, pad the key with itself
            if ( keyString.length() < msgChar.length ) {
                int times = (int) Math.floor( msgChar.length / keyString.length() );
                for (; times > 1; times--) {
                    keyString += addString;
                }
                if ( keyString.length() < msgChar.length ) {
                    times = msgChar.length - keyString.length();
                    for (int c = 0; c < times; c++ ) {
                        keyString += keyString.charAt(c);
                    }
                }
            }
            char[] keyChar = keyString.toCharArray();

            // converting the message and key to int
            int[] msgInt = new int[msgChar.length];
            int[] keyInt = new int[keyChar.length];

            int[] resultInt = new int[keyChar.length];
            char[] resultChar = new char[keyChar.length];

            for ( int c = 0; c < msgChar.length; c++ ) {
                msgInt[c] = alphabet.get(msgChar[c]);
                keyInt[c] = alphabet.get(keyChar[c]);
                if ( chkBea.isSelected() ) {
                    resultInt[c] = keyInt[c] - msgInt[c];
                    if (keyInt[c] - msgInt[c] < 0) {
                        resultInt[c] += 26;
                    }
                } else if ( chkVig.isSelected() ) {
                    resultInt[c] = msgInt[c] - keyInt[c];
                    if (resultInt[c] < 0) {
                        resultInt[c] += 26;
                    }
                } else {
                    error(2);
                }
                resultChar[c] = getKeyByValue(alphabet, resultInt[c]);
            }
            // display the cipher
            txaResult.setText(String.valueOf(resultChar));

        } else {
            error(1);
        }
    }

    // (mis-)using the HashMap to get the Keys to Values (not the other way around)
    private Character getKeyByValue(Map<Character, Integer> map, Integer value) {
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        // if no key is found, return '@', this is normally never seen by a user
        return '@';
    }

    // display different error codes
    private void error(int errorcode) {
        txaResult.setText("There has been an error!\nErrorcode "+errorcode+"\nRefer to the help view.");
        // list of errors:
        // 1 = KEY is longer than MESSAGE
        // 2 = no algorithm was selected, this will most likely never happen
    }

    // the "manual" or help screen
    @FXML
    void btnHelp_OnClick(ActionEvent actionEvent) {
        txaResult.setText("1.a enter a MESSAGE without spaces or punctuation\n" +
                                  "1.b enter a KEY without spaces or punctuation\n" +
                                  "2. hit the encode button\n" +
                                  "your message is now encoded, but case always upper case only\n" +
                                  "paste the encoded MESSAGE into the MESSAGE box, enter the correct KEY and hit decode\n" +
                                  "in order to decode your message");
    }

    // uncheck the Beaufort checkbox when the Vigenère checkbox was checked
    @FXML
    void chkCheckBea(ActionEvent actionEvent) {
        chkBea.setSelected(true);
        chkVig.setSelected(false);
    }

    // uncheck the Vigenère checkbox when the Beaufort checkbox was checked
    @FXML
    void chkCheckVig(ActionEvent actionEvent) {
        chkVig.setSelected(true);
        chkBea.setSelected(false);
    }

    // fill the HashMap with the alphabet in Caps
    private void fillMapCaps(HashMap<Character,Integer> map) {
        map.put('A', 0);
        map.put('B', 1);
        map.put('C', 2);
        map.put('D', 3);
        map.put('E', 4);
        map.put('F', 5);
        map.put('G', 6);
        map.put('H', 7);
        map.put('I', 8);
        map.put('J', 9);
        map.put('K', 10);
        map.put('L', 11);
        map.put('M', 12);
        map.put('N', 13);
        map.put('O', 14);
        map.put('P', 15);
        map.put('Q', 16);
        map.put('R', 17);
        map.put('S', 18);
        map.put('T', 19);
        map.put('U', 20);
        map.put('V', 21);
        map.put('W', 22);
        map.put('X', 23);
        map.put('Y', 24);
        map.put('Z', 25);
        map.put(' ', 100);
    }
}