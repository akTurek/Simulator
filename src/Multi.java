import java.util.concurrent.*;

public class Multi {
    public MatrikaCelic matrikaCelic;
    public int numberOfThreads;
    public int[] wait;
    public int[] areAllTasksOverArr;


    public Multi(int row, int col, int numOfHeat, int numberOfThreads) {
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
        this.numberOfThreads = numberOfThreads;
        this.wait = new int[numberOfThreads];
        this.areAllTasksOverArr = new int[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            wait[i] = 0;
            areAllTasksOverArr[i] = 0;
        }
    }


    public long calTemp() {

        long t0 = System.currentTimeMillis();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(numberOfThreads);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // Dodaj taske
        for (int i = 0; i < numberOfThreads; i++) {
            //Nad skupnim objektom brez konflikta, druge lokacijem, ali pa pregrada
            Runnable task = new Task(this, i, cyclicBarrier);
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
        //matrikaCelic.printMatriko();
        return  (t1 - t0);
    }
}





