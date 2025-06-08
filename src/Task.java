import java.util.concurrent.*;

class Task implements Runnable {
    private Multi multi;
    private int taskId;
    private int startRow;
    private int endRow;
    private int rows, cols;
    CyclicBarrier cyclicBarrier;
    CyclicBarrier cyclicBarrierStart;

    public Task(Multi multi, int taskId, CyclicBarrier cyclicBarrier, CyclicBarrier cyclicBarrierStart) {
        this.multi = multi;
        this.taskId = taskId;
        this.startRow = taskId * (multi.matrikaCelic.row) / (multi.numberOfThreads);
        this.endRow = Math.min((taskId + 1) * (multi.matrikaCelic.row) / (multi.numberOfThreads), multi.matrikaCelic.row);
        this.rows = multi.matrikaCelic.row;
        this.cols = multi.matrikaCelic.col;
        this.cyclicBarrier = cyclicBarrier;
        this.cyclicBarrierStart = cyclicBarrierStart;
    }


    @Override
    public void run() {
        float change;
        float maxChange;
        int c = 0;

        do {

            //System.out.println("racunam "+taskId);
            //calPrevTemp

            for (int k = startRow; k < endRow; k++) {
                for (int j = 0; j < multi.matrikaCelic.col; j++) {
                    multi.matrikaCelic.calPrevTemp(k, j);
                    //System.out.println("racunam "+taskId);
                }
            }

            //Barrier///////////////////////////////////////////////////////////////////////////////
            try {
                cyclicBarrierStart.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }


            maxChange = 0.F;


            //calNowTemp
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < cols; j++) {
                    multi.matrikaCelic.calNowTemp(i, j);
                    change = multi.matrikaCelic.getTempChange(i,j);

                    if (change > maxChange) {
                        maxChange = change;
                    }
                }
            }
            c++;
            if (maxChange > 0.25F){
                multi.isOver.set(false);
                System.out.println(maxChange + " set false "+taskId);
            }

            //Barrier//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted or barrier broken");
            }


        } while (!multi.isOver.get());
        System.out.println("max temp change: " + maxChange+ " is over "+ multi.isOver.get()+" thread "+taskId+"stevilo cikljev "+c);
    }

}
