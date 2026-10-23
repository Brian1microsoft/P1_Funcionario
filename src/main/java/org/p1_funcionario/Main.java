
package org.p1_funcionario;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        File arquivoFXML = new File(
                "src/main/java/org/p1_funcionario/view/main-view.fxml"
        );

        FXMLLoader loader = new FXMLLoader(arquivoFXML.toURI().toURL());
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Sistema de Gestão de Funcionários");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
