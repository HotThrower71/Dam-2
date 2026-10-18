public class App {
    public static void main(String[] args) {
        // Hilo 1: extends Thread
        new HiloThread().start();

        // Hilo 2: implements Runnable
        new Thread(new HiloRunnable()).start();
    }
}