import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import controle.ControllerTela;

public class Principal extends Application{
  
  public static void main(String[] args){
    launch();
  }

public void start(Stage stage) {
    try {
        // Carrega a interface
        Parent root = FXMLLoader.load(getClass().getResource("visao/telaPrincipal.fxml"));
        Scene scene = new Scene(root, 878, 488); 
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        
    } catch (Exception e) {
        System.err.println("Falha crítica:");
        e.printStackTrace();
    }
}
  
}