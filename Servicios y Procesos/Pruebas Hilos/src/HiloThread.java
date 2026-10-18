public class HiloThread extends Thread {
    @Override
    public void run() {
        System.out.println("Cálculo Thread (Suma): 5 + 5 = " + (5 + 5));
    }
}