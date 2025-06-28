import mpi.*;

public class Distributivno {


    public static void main(String[] args) {

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        
        int rows = 100; // Number of rows
        int cols = 100; // Number of columns
        int heatSources = 100; // Number of columns

        long t0 = System.currentTimeMillis();
        int c = 0;

        int remainderRows = rows % size;  // ostanek vrstic za enakomirno porazdelitev 
        int rowsPerProcess = (rows - remainderRows) / size;  //vrstice na proces
        
        int[] recvcounts = new int[size];  //stevilo elementov
        int[] displs = new int[size];      //zamik 

        // Calculate the size of data to be received by each process
        int totalRows = 0;
        for (int i = 0; i < size - 1; i++) {
            recvcounts[i] = rowsPerProcess * cols;
            displs[i] = totalRows * cols;
            totalRows += rowsPerProcess;
        }
        recvcounts[size - 1] = (rowsPerProcess + remainderRows) * cols;   // zadnji proces dobi se ostanek
        displs[size - 1] = totalRows * cols;
        
        MatrikaCelicDistributivno arrDis = new MatrikaCelicDistributivno(recvcounts[rank], cols);  // naredi praznodistributivno "matriko za kasnjese racunanje"
        
        //matrixs for reciving data  data among processes
        float[] arrayNowTemp = new float[recvcounts[rank]]; // Receive buffer for arrayNowTemp
        float[] arrayPrevTemp = new float[recvcounts[rank]]; // Receive buffer for arrayPrevTemp
        boolean[] arrayIsHeatSource = new boolean[recvcounts[rank]]; // Receive buffer for arrayIsHeatSource
        float[] lowLimitNowTemp = new float[cols];  //  MEJNE VREDNOSTI KI JIH RABI ZA RACUNATI A JIH IMA SOSENJA MATRIKA
        float[] upLimitNowTemp = new float[cols];   //  MEJNE VREDNOSTI KI JIH RABI ZA RACUNATI A JIH IMA SOSENJA MATRIKA
        float[] lowLimitPrevTemp = new float[cols]; //  MEJNE VREDNOSTI KI JIH RABI ZA RACUNATI A JIH IMA SOSENJA MATRIKA
        float[] upLimitPrevTemp = new float[cols]; //  MEJNE VREDNOSTI KI JIH RABI ZA RACUNATI A JIH IMA SOSENJA MATRIKA
        float maxChamge;
        float globalMax;

        MatrikaCelic matrikaCelic = null;
        // rank0 ustvari matriko in jo razdeli na 1D arraye 
        if (rank == 0) {
            matrikaCelic = new MatrikaCelic(rows, cols, heatSources);
            float[] sendArrayNowTemp = matrikaCelic.matrikaToArrayNowTemp();
            float[] sendArrayPrevTemp = matrikaCelic.matrikaToArrayPrevTemp();
            boolean[] sendArrayIsHeatSource = matrikaCelic.matrikaToArrayIsHeatSource();

            //shrani si svoj del matrike
            arrayNowTemp = arrDis.getMyPartF(sendArrayNowTemp,recvcounts[rank]);
            arrayPrevTemp = arrDis.getMyPartF(sendArrayPrevTemp,recvcounts[rank]);
            arrayIsHeatSource = arrDis.getMyPartB(sendArrayIsHeatSource,recvcounts[rank]);


            //poslji matrkike podatke
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Send(sendArrayNowTemp, displs[i], recvcounts[i], MPI.FLOAT, i , 0);
                MPI.COMM_WORLD.Send(sendArrayPrevTemp, displs[i], recvcounts[i], MPI.FLOAT, i, 1);
                MPI.COMM_WORLD.Send(sendArrayIsHeatSource, displs[i], recvcounts[i], MPI.BOOLEAN, i, 2);
            }
        }

        //prejmi matrike za rang > 0
        if (rank > 0 ) {
            MPI.COMM_WORLD.Recv(arrayNowTemp, 0, recvcounts[rank], MPI.FLOAT, 0, 0);
            MPI.COMM_WORLD.Recv(arrayPrevTemp, 0, recvcounts[rank], MPI.FLOAT, 0, 1);
            MPI.COMM_WORLD.Recv(arrayIsHeatSource, 0, recvcounts[rank], MPI.BOOLEAN, 0, 2);
            arrDis.setArrays(arrayPrevTemp,arrayNowTemp,arrayIsHeatSource);
        }

        System.out.println("Prejel arraje  rank" + rank + " velikosti prev temp " +arrayPrevTemp.length+ " velikosti now temp " +arrayNowTemp.length+ " velikosti hs " +arrayIsHeatSource.length );
        MPI.COMM_WORLD.Barrier();
        arrDis.setArrays(arrayPrevTemp,arrayNowTemp,arrayIsHeatSource);

        do {
            c++;
            //poslji in prejme mejne vrednosti sosednjim matrikam
            if (rank != size - 1) {
                MPI.COMM_WORLD.Send(arrayPrevTemp, recvcounts[rank] - cols, cols, MPI.FLOAT, rank + 1, 0);
            }
            if (rank != 0) {

                MPI.COMM_WORLD.Recv(lowLimitPrevTemp, 0, cols, MPI.FLOAT, rank - 1, 0);
            }
            MPI.COMM_WORLD.Barrier();

            if (rank != 0) {

                MPI.COMM_WORLD.Send(arrayPrevTemp, 0, cols, MPI.FLOAT, rank - 1, 0);
            }
            if (rank != size - 1) {

                MPI.COMM_WORLD.Recv(upLimitPrevTemp, 0, cols, MPI.FLOAT, rank + 1, 0);
            }
            MPI.COMM_WORLD.Barrier();

            arrDis.setLimits(lowLimitPrevTemp, upLimitPrevTemp); // nastavi mejne vrednosti v distributivni matriki

            // racunanje en cikel
            if (rank == 0) {
                maxChamge = arrDis.enCikelSumulacijeFirst();


            } else if (rank == size - 1) {
                maxChamge = arrDis.enCikelSumulacijeLast();

            } else {
                maxChamge = arrDis.enCikelSumulacijeMiddle();

            }

            System.out.println("Rank "+rank+" maxChange "+maxChamge);

            float[] sendBuf = new float[] {maxChamge};
            float[] recvBuf = new float[1];
            MPI.COMM_WORLD.Allreduce(
                    sendBuf, 0,
                    recvBuf, 0,
                    1, MPI.FLOAT, MPI.MAX
            );
            globalMax = recvBuf[0];

            MPI.COMM_WORLD.Barrier();

            // kazi na nove izracunane vrdnosti da v naslednji zanki posiljajo nove vredne mejnosti
            arrayNowTemp = arrDis.getArrayNowTemp();
            arrayPrevTemp = arrDis.getArrayPrevTemp();
            System.out.println("/////");


        }while (globalMax > 0.25);

        //sestavi 2dmatriko
        if (rank > 0){
            MPI.COMM_WORLD.Send(arrayNowTemp, 0, arrayNowTemp.length, MPI.FLOAT, 0 , 0);
        }

        if (rank == 0){
            float [] temp = new float[rows*cols];
            for (int i = 0; i < arrayNowTemp.length; i++) {
                temp[i] = arrayNowTemp[i];
            }

            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Recv(temp, displs[i], recvcounts[i], MPI.FLOAT, i, 0);
            }
            matrikaCelic.arraysNTToMatrika(temp);
            matrikaCelic.matrikaJPG("dist");


        }

        
        MPI.COMM_WORLD.Barrier();
        MPI.Finalize();


        long t1 = System.currentTimeMillis();
        System.out.println("trajanje programa "+ (t1-t0)+ " stevilo cikljev "+c);



    }
}