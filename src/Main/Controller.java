package Main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Controller implements Observer  {

    private Socket socket;
    private DataOutputStream datosSalida = null;

    @FXML
    private Pane paneInicio;

    @FXML
    private TextField textUser;

    @FXML
    private Button btnIniciar;

    @FXML
    private TextField textName;

    @FXML
    private TextField textLastName;

    @FXML
    private TextField textCountry;

    @FXML
    private TextField textFruits;

    @FXML
    private TextField textAnimal;

    @FXML
    private TextField textThing;

    @FXML
    private Button btnTerminado;

    @FXML
    private ListView<String> listView;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnOut;

    @FXML
    private Label labelLetra;

    @FXML
    private Pane idPaneGanadores;

    @FXML
    private Label labelPerde;

    @FXML
    private Label labelGana;

    String nombreUsuario=" ";
    String letra=" ";
    int puntos=0;

    String [] letras = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    TextField [] arregloText = {textName,textLastName,textFruits,textThing,textAnimal,textCountry};
    String [] respuestaJugador2;
    Boolean aux = true;

    @FXML
    public void iniciarJuego(){

        if(!textUser.getText().isEmpty()){
            nombreUsuario = textUser.getText();
            paneInicio.setVisible(false);
            conectionServer();
        }
    }

    @FXML
    public void btnJugar(){
        Random aleatorio = new Random();
        letra = letras[((int) (aleatorio.nextDouble()*letras.length))];
        //letra = "A";
        labelLetra.setText(letra);
        btnStart.setDisable(true);
        btnTerminado.setDisable(false);
        textName.setEditable(true);
        textLastName.setEditable(true);
        textCountry.setEditable(true);
        textAnimal.setEditable(true);
        textFruits.setEditable(true);
        textThing.setEditable(true);
        nombreUsuario = nombreUsuario.substring(0,1);
        String mostrarLetra = "1"+"."+letra;
        enviarDatos(mostrarLetra);
    }

    @FXML
    public void datosInsertados(){
        aux = false;
        String respuesta = textName.getText() + "."+ textLastName.getText() +"."+ textCountry.getText() +"." +textAnimal.getText() +"."+ textFruits.getText() +"."+ textThing.getText();
        enviarDatos(respuesta);
         for(int i = 0; i < arregloText.length;i++){
             if(!arregloText[i].getText().isEmpty()){
                 if(letra.equals(arregloText[i].getText().substring(0,1)) || letra.toLowerCase().equals(arregloText[i].getText().substring(0,1))){
                     System.out.println(arregloText[i].getText()+" es igual");
                 }else{
                     System.out.println(arregloText[i].getText()+" no es igual");
                 }
             } else {
                 System.out.println("textFile esta vacio");
             }
         }
    }

    public void enviarDatos(String dato){
        try {
            datosSalida.writeUTF(dato);
            datosSalida.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  void compararDatos() {
        int jugador1=0;
        int jugador2=0;

        for (int i = 0; i < arregloText.length; i++) {
            if (!respuestaJugador2 [i].equals(" ")){
                System.out.println(respuestaJugador2 [i]+"Respuesta");
            }
            if (!arregloText[i].getText().isEmpty() && (!respuestaJugador2[i].isEmpty() && !respuestaJugador2 [i].equals(" "))){
                if (arregloText[i].getText().equals(respuestaJugador2[i])) {
                    jugador1=jugador1+50;
                    jugador2=jugador2+50;
                }else{
                    jugador1=jugador1+100;
                    jugador2=jugador2+100;
                }

            }else if (arregloText[i].getText().isEmpty() && !respuestaJugador2[i].isEmpty() || !respuestaJugador2[i].equals(" ")){
                jugador2 = jugador2 + 100;
            } else if(!arregloText[i].getText().isEmpty() && respuestaJugador2[i].isEmpty() || !arregloText[i].getText().equals(" ")) {
                jugador1 = jugador1 + 100;
            }
        }
        System.out.println("Puntos jugador1: "+jugador1);
        System.out.println("Puntos jugador2: "+jugador2);
        if (jugador2 > jugador1){
            labelGana.setText("El Servidor gano");
            labelPerde.setText("El Cliente perdio");
        }else if (jugador2 < jugador1){
            labelGana.setText("El Cliente gano");
            labelPerde.setText("El Servidor perdio");
        }else{
            labelGana.setText("Ha sido un empate");
        }
        idPaneGanadores.setVisible(true);
        listaMostrada();
        aux = true;
    }

    public void listaMostrada(){
        String idk = "RESPUESTAS DEL JUGADOR NUMERO 2: \n";
        String listBasta = "Nombre: " + respuestaJugador2[0] + "\nApellido: " + respuestaJugador2[1] + "\nCiudad o Pais: " + respuestaJugador2[2] + "\nAnimal: " + respuestaJugador2[3] + "\nFlor o Fruto: " + respuestaJugador2[4] + "\nCosa: " + respuestaJugador2[5];
        listView.getItems().add(idk);
        listView.getItems().add(listBasta);
    }

    @FXML
    public void btnSalir(){
        System.exit(0);
    }

    public void conectionServer(){
        try {
            socket = new Socket("localhost", 3001);
            datosSalida = new DataOutputStream(socket.getOutputStream());
            datosSalida.flush();
            crearHilo();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize(){
        arregloText[0] = textName;
        arregloText[1] = textLastName;
        arregloText[2] = textCountry;
        arregloText[3] = textAnimal;
        arregloText[4] = textFruits;
        arregloText[5] = textThing;
    }

    public void crearHilo(){
        try {
            HiloCliente hiloCliente = new HiloCliente(socket);
            hiloCliente.addObserver(this);
            new Thread(hiloCliente).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        String [] datosServidor = (String[]) arg;
        respuestaJugador2 = datosServidor;
        if (aux) {
            String respuesta = textName.getText() +" "+ "."+ textLastName.getText() +" "+"."+ textCountry.getText() +" "+"." +textAnimal.getText()+" "+"."+ textFruits.getText()+" "+"."+ textThing.getText()+" ";
            //System.out.println("Hola");
            enviarDatos(respuesta);
            //System.out.println("Adios");
        }else{
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    compararDatos();
                }
            });
        }
    }
}
