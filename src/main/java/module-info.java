module org.p1_funcionario {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.p1_funcionario to javafx.fxml;
    exports org.p1_funcionario;
    exports org.p1_funcionario.controller;
    opens org.p1_funcionario.controller to javafx.fxml;
}