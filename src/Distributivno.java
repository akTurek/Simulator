import mpi.*;

public class Distributivno {


    public static void main(String[] args) {

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        
        int rows = 200; // Number of rows
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
        int[] recvIsOverInt = new int[1];  // za preverejanje rank0 ali so vsi procesi konec ali je konec
        int[] sendIsOverInt = new int[1];  // za posiljanje ali so konec
        boolean isOver = false;  // za while loop ali so konec 

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

        //System.out.println("Prejel arraje  rank" + rank + " velikosti prev temp " +arrayPrevTemp.length+ " velikosti now temp " +arrayNowTemp.length+ " velikosti hs " +arrayIsHeatSource.length );
        MPI.COMM_WORLD.Barrier();
        arrDis.setArrays(arrayPrevTemp,arrayNowTemp,arrayIsHeatSource);


        while (!isOver) {

            isOver = true;
            //poslji in prejme mejne vrednosti sosednjim matrikam
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

            arrDis.setLimits(lowLimitPrevTemp, upLimitPrevTemp, lowLimitNowTemp, upLimitNowTemp); // nastavi mejne vrednosti v distributivni matriki

            // racunanje en cikel
            if (rank == 0) {
                sendIsOverInt[0] = arrDis.enCikelSumulacijeFirst();
                c++;
                if (sendIsOverInt[0] == 0) {
                    isOver = false;
                }
            } else if (rank == size - 1) {
                sendIsOverInt[0] = arrDis.enCikelSumulacijeLast();
                c++;
            } else {
                sendIsOverInt[0] = arrDis.enCikelSumulacijeMiddle();
                c++;
            }
            MPI.COMM_WORLD.Barrier();

            // posiljanje rangu0 ali so konec
            if (rank > 0) {
                MPI.COMM_WORLD.Send(sendIsOverInt, 0, 1, MPI.INT, 0, 2);
            }

            // prejemanje ali so vsi procesi konec
            if (rank == 0) {
                for (int i = 1; i < size; i++) {
                    MPI.COMM_WORLD.Recv(recvIsOverInt, 0, 1, MPI.INT, i, 2); // Receive integer
                    if (recvIsOverInt[0] == 0) {
                        isOver = false;
                    }
                }
            }
            MPI.COMM_WORLD.Barrier();

            // sporocanje vstalim ali je konec simulacije
            if (rank == 0) {
                if (!isOver) {
                    sendIsOverInt[0] = 0;
                }
                for (int i = 1; i < size; i++) {
                    MPI.COMM_WORLD.Send(sendIsOverInt, 0, 1, MPI.INT, i, 2); // Send integer
                }
            }

            // prejemanje ali je konec simulacije
            if (rank > 0) {
                MPI.COMM_WORLD.Recv(recvIsOverInt, 0, 1, MPI.INT, 0, 2); // Receive integer
                if (recvIsOverInt[0] == 0) {
                    isOver = false;
                }
            }
            MPI.COMM_WORLD.Barrier();

            // kazi na nove izracunane vrdnosti da v naslednji zanki posiljajo nove vredne mejnosti
            arrayNowTemp = arrDis.getArrayNowTemp();
            arrayPrevTemp = arrDis.getArrayPrevTemp();


        }


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