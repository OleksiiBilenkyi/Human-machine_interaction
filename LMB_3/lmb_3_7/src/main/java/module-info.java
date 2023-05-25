module com.example.lmb_4_7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;


    opens com.example.lmb_3_7 to javafx.fxml;
    exports com.example.lmb_3_7;
}