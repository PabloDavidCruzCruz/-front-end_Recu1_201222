package Main;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

public class HiloCliente extends Observable implements Runnable {

    private Socket socket;
    private DataInputStream dataInputStream;

    public HiloCliente(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            String [] datosRecibidos;
            do {
                Thread.sleep(ThreadLocalRandom.current().nextLong(1000L)+100);
                datosRecibidos = dataInputStream.readUTF().split("\\.");
                this.setChanged();
                this.notifyObservers(datosRecibidos);
            }while (!socket.isClosed());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
