import mpi.*;

public class Distributivno {


    public static void main(String[] args) {

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();


        int rows = 40; // Number of rows
        int cols = 40; // Number of columns
        int heatSources = 10; // Number of columns

        long t0 = System.currentTimeMillis();


        // Calculate the number of elm per process
        int remainderRows = rows % size;
        //System.out.println("Ostanek "+remainderRows);
        int rowsPerProcess = (rows - remainderRows) / size;
        //System.out.println("vrstic na elm "+rowsPerProcess);
        // Calculate the size of data to be received by each process
        int[] recvcounts = new int[size];
        int[] displs = new int[size];

        int totalRows = 0;
        for (int i = 0; i < size - 1; i++) {
            recvcounts[i] = rowsPerProcess * cols;
            displs[i] = totalRows * cols;
            totalRows += rowsPerProcess;
        }
        recvcounts[size - 1] = (rowsPerProcess + remainderRows) * cols;
        displs[size - 1] = totalRows * cols;

        System.out.println("Rank: " + rank + " zacetni elm: " + displs[rank]);


        MatrikaCelicDistributivno arrDis = new MatrikaCelicDistributivno(recvcounts[rank], cols);
        System.out.println("Dolzina arraya v disarry " + arrDis.getArrayNowTemp().length + " Rank " + rank);
        // System.out.println(" st cols "+cols);


        // Scatter matrix data among processes
        float[] arrayNowTemp = new float[recvcounts[rank]]; // Receive buffer for arrayNowTemp
        float[] arrayPrevTemp = new float[recvcounts[rank]]; // Receive buffer for arrayPrevTemp
        boolean[] arrayIsHeatSource = new boolean[recvcounts[rank]]; // Receive buffer for arrayIsHeatSource

        float[] lowLimitNowTemp = new float[cols];
        float[] upLimitNowTemp = new float[cols];

        float[] lowLimitPrevTemp = new float[cols];
        float[] upLimitPrevTemp = new float[cols];


        int[] recvIsOverInt = new int[1];
        int[] sendIsOverInt = new int[1];

        boolean isOver = false;

        // System.out.println(size);
        // System.out.println("Rank " + rank + " recvcounts vrednost " + recvcounts[rank]);


        MatrikaCelic matrikaCelic = new MatrikaCelic(rows, cols, heatSources);
        float[] sendArrayNowTemp = matrikaCelic.matrikaToArrayNowTemp();
        float[] sendArrayPrevTemp = matrikaCelic.matrikaToArrayPrevTemp();
        boolean[] sendArrayIsHeatSource = matrikaCelic.matrikaToArrayIsHeatSource();

        MPI.COMM_WORLD.Barrier();

        // Scatter ArrayNowTemp
        MPI.COMM_WORLD.Scatterv(sendArrayNowTemp, 0, recvcounts, displs, MPI.FLOAT, arrayNowTemp, 0, recvcounts[rank], MPI.FLOAT, 0);
        // Scatter arrayPrevTemp
        MPI.COMM_WORLD.Scatterv(sendArrayPrevTemp, 0, recvcounts, displs, MPI.FLOAT, arrayPrevTemp, 0, recvcounts[rank], MPI.FLOAT, 0);
        // Scatter arrayIsHeatSource
        MPI.COMM_WORLD.Scatterv(sendArrayIsHeatSource, 0, recvcounts, displs, MPI.BOOLEAN, arrayIsHeatSource, 0, recvcounts[rank], MPI.BOOLEAN, 0);
        MPI.COMM_WORLD.Barrier();


        arrDis.setArrays(arrayPrevTemp, arrayNowTemp, arrayIsHeatSource);

        //System.out.println("opravil scater");

        while (!isOver) {

            isOver = true;

            //poslji in prejme mejne vrednosti
            if (rank != size - 1) {
                MPI.COMM_WORLD.Send(arrayNowTemp, recvcounts[rank] - cols, cols, MPI.FLOAT, rank + 1, 1);
                MPI.COMM_WORLD.Send(arrayPrevTemp, recvcounts[rank] - cols, cols, MPI.FLOAT, rank + 1, 0);
            }
            if (rank != 0) {
                MPI.COMM_WORLD.Recv(lowLimitNowTemp, 0, cols, MPI.FLOAT, rank - 1, 1);
                MPI.COMM_WORLD.Recv(lowLimitPrevTemp, 0, cols, MPI.FLOAT, rank - 1, 0);
            }
            MPI.COMM_WORLD.Barrier();

            if (rank != 0) {
                MPI.COMM_WORLD.Send(arrayNowTemp, 0, cols, MPI.FLOAT, rank - 1, 1);
                MPI.COMM_WORLD.Send(arrayPrevTemp, 0, cols, MPI.FLOAT, rank - 1, 0);
            }
            if (rank != size - 1) {
                MPI.COMM_WORLD.Recv(upLimitNowTemp, 0, cols, MPI.FLOAT, rank + 1, 1);
                MPI.COMM_WORLD.Recv(upLimitPrevTemp, 0, cols, MPI.FLOAT, rank + 1, 0);
            }
            MPI.COMM_WORLD.Barrier();
             System.out.println("Dolzina mejnih vrednosti " + lowLimitNowTemp.length);

            MPI.COMM_WORLD.Barrier();
            if (rank == 1){
                for (int i = 0; i < lowLimitNowTemp.length; i++) {
                    System.out.print(lowLimitNowTemp[i]+" ");

                }
                System.out.print("spodnja meja prejeta ");
                System.out.println();
            }

            MPI.COMM_WORLD.Barrier();

            if (rank == 0){
                for (int i = arrayNowTemp.length-cols; i < arrayNowTemp.length; i++) {
                    System.out.print(arrayNowTemp[i]+" ");

                }
                System.out.print("spodnja meja         ");

                System.out.println();
                System.out.println();
            }

            if (rank == 1){
                for (int i = 0; i < upLimitNowTemp.length; i++) {
                    System.out.print(upLimitNowTemp[i]+" ");

                }
                System.out.print("zgornja meja prejeta ");
                System.out.println();
            }

            MPI.COMM_WORLD.Barrier();

            if (rank == 2){
                for (int i = 0; i < cols; i++) {
                    System.out.print(arrayNowTemp[i]+" ");

                }
                System.out.print("zgornja meja         ");

                System.out.println();
                System.out.println();
            }


            arrDis.setLimits(lowLimitPrevTemp, upLimitPrevTemp, lowLimitNowTemp, upLimitNowTemp);
            if (rank == 0) {
                sendIsOverInt[0] = arrDis.enCikelSumulacijeFirst();
                if (sendIsOverInt[0] == 0) {
                    isOver = false;
                }
            } else if (rank == size - 1) {
                //System.out.println(rank+" Racunam");
                sendIsOverInt[0] = arrDis.enCikelSumulacijeLast();


            } else {
                sendIsOverInt[0] = arrDis.enCikelSumulacijeMiddle();
            }

            System.out.println("Izracunal en cikel");


            MPI.COMM_WORLD.Barrier();

            if (rank > 0) {
                 System.out.println("poslal vrednost is over " + sendIsOverInt[0] + " rank " + rank);
                MPI.COMM_WORLD.Send(sendIsOverInt, 0, 1, MPI.INT, 0, 2); // Send integer
            }


            if (rank == 0) {

                for (int i = 1; i < size; i++) {
                    MPI.COMM_WORLD.Recv(recvIsOverInt, 0, 1, MPI.INT, i, 2); // Receive integer
                    if (recvIsOverInt[0] == 0) {
                        isOver = false;
                    }
                }
                 System.out.println("preracunal is over "+isOver);
            }
            MPI.COMM_WORLD.Barrier();
            if (rank == 0) {
                if (!isOver) {
                    sendIsOverInt[0] = 0;
                }
                //System.out.println("poslal preracunano vrednost is over " + sendIsOverInt[0] + " rank " + rank);
                for (int i = 1; i < size; i++) {
                    MPI.COMM_WORLD.Send(sendIsOverInt, 0, 1, MPI.INT, i, 2); // Send integer
                }
            }


            if (rank > 0) {
                MPI.COMM_WORLD.Recv(recvIsOverInt, 0, 1, MPI.INT, 0, 2); // Receive integer
                if (recvIsOverInt[0] == 0) {
                    isOver = false;
                }
                //System.out.println("prejel preracunal is over " + isOver + " " + rank);
            }

            //System.out.println("skoraj zakljucil z racunanjem "+rank+" rezultat is over " +isOver);
            MPI.COMM_WORLD.Barrier();
            arrayNowTemp = arrDis.getArrayNowTemp();
            arrayPrevTemp = arrDis.getArrayPrevTemp();

        }

        /*for (int i = 0; i < arrayNowTemp.length; i++) {
            arrayNowTemp[i] = rank;
        }*/


        MPI.COMM_WORLD.Barrier();

        //System.out.println("zakljucil z racunanjem "+rank+" rezultat is over " +isOver);
        MPI.COMM_WORLD.Barrier();
        MPI.COMM_WORLD.Gatherv(arrayNowTemp, 0, recvcounts[rank], MPI.FLOAT, sendArrayNowTemp, 0, recvcounts, displs, MPI.FLOAT, 0);
        MPI.COMM_WORLD.Gatherv(arrayPrevTemp, 0, recvcounts[rank], MPI.FLOAT, sendArrayPrevTemp, 0, recvcounts, displs, MPI.FLOAT, 0);
        //MPI.COMM_WORLD.Gatherv(arrayIsHeatSource, 0, recvcounts[rank], MPI.BOOLEAN, sendArrayIsHeatSource, 0, recvcounts, displs, MPI.BOOLEAN, 0);


        if (rank == 0) {
            for (int i = 0; i < sendArrayNowTemp.length; i++) {

                //System.out.print(sendArrayNowTemp[i]+" ");
            }

            matrikaCelic.arraysToMatrika(sendArrayNowTemp, sendArrayPrevTemp);
            matrikaCelic.printMatriko();
            matrikaCelic.printMatrikoGraficno();
        }


        MPI.Finalize();


        long t1 = System.currentTimeMillis();
        System.out.println("Trajanje programa v ms: " + (t1 - t0));


    }
}