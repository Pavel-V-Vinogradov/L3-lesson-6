module ru.gb.gbchat2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;

    exports ru.gb.gbchat2.client;
    opens ru.gb.gbchat2.client to javafx.fxml;
}