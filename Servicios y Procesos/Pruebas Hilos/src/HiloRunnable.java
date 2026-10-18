public class HiloRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Cálculo Runnable (Multiplicación): 10 * 10 = " + (10 * 10));
    }
}