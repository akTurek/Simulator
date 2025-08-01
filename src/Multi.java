import java.security.PrivateKey;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Multi {
    public MatrikaCelic matrikaCelic;
    public int numberOfThreads;
    public AtomicBoolean isOver;


    public Multi(int row, int col, int numOfHeat) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.numberOfThreads = Runtime.getRuntime().availableProcessors();
        this.isOver = new AtomicBoolean(false);
    }


    public void calTemp() {

        long t0 = System.currentTimeMillis();
        Runnable reset = () -> this.isOver.set(true);
        CyclicBarrier cyclicBarrierStart = new CyclicBarrier(numberOfThreads,reset);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // Dodaj taske
        for (int i = 0; i < numberOfThreads; i++) {
            //Nad skupnim objektom brez konflikta, druge lokacijem, ali pa pregrada
            Runnable task = new Task(this, i, cyclicBarrier, cyclicBarrierStart);
            executorService.submit(task);
        }


        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while waiting for termination");
        }

        long t1 = System.currentTimeMillis();


        System.out.println("Trajanje programa v ms: " +(t1-t0));

        //matrikaCelic.matrikaJPG("multi");
    }
}





