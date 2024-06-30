
import java.util.Random;


public class MatrikaCelic {
    final int row;
    final int col;
    final int numOfHeat;

    private float [][] prevTemp;
    private float [][] nowTemp;
    private boolean [][] isHeatSource;

    public MatrikaCelic(int row, int col, int numOfHeat) {
        this.row = row;
        this.col = col;
        this.numOfHeat = numOfHeat;

        this.prevTemp = new float[row][col];
        this.nowTemp = new float[row][col];
        this.isHeatSource = new boolean[row][col];

        narediMatriko();

    }

    public MatrikaCelic(int row, int col) {
        this.row = row;
        this.col = col;

        this.prevTemp = new float[row][col];
        this.nowTemp = new float[row][col];
        this.isHeatSource = new boolean[row][col];
        this.numOfHeat = 1;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setNowToPrev(){
        this.prevTemp = this.nowTemp;
    }


    private void narediMatriko() {

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i == 0 || i == row - 1 || j == 0 || j == col - 1){
                    prevTemp[i][j] = 0.F;
                    nowTemp[i][j] = 0.F;
                    isHeatSource[i][j] = true; //robi so 0C ampak  heat sourci, da se jih ne racuna
                }else {
                    prevTemp[i][j] = 0.F;
                    nowTemp[i][j] = 0.F;
                    isHeatSource[i][j] = false;
                }
            }
        }

        Random rand = new Random();
        int count = 0;
        while (count < numOfHeat) {
            int randomRow = rand.nextInt(row);
            int randomCol = rand.nextInt(col);
            if (!isHeatSource[randomRow][randomCol]) {
                prevTemp[randomRow][randomCol] = 100.F;
                nowTemp[randomRow][randomCol] = 100.F;
                isHeatSource[randomRow][randomCol] = true;
                count++;
            }
        }
    }


    public void printMatriko() {
        // Print column headers
        System.out.print("    ");
        for (int i = 0; i < col; i++) {
            System.out.printf("%4d", i);
        }
        System.out.println();

        // Print rows
        for (int h = 0; h < row; h++) {
            // Print row header
            System.out.printf("%-4d", h);

            // Print row content
            for (int k = 0; k < col; k++) {
                int temp = (int) this.nowTemp[h][k];
                System.out.printf("%4d", temp);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printMatrikoGraficno() {
        // Print column headers
        System.out.print("    ");
        for (int i = 0; i < col; i++) {
            System.out.printf("%3d", i);
        }
        System.out.println();

        // Define symbols for different temperature ranges
        String[] symbols = {"   ", "...", ";;;", "ooo", "&&&", "OOO", "XXX", "##", "$", "@@@"};

        // Print rows
        for (int h = 0; h < row; h++) {
            // Print row header
            System.out.printf("%-3d", h);

            // Print row content
            for (int k = 0; k < col; k++) {
                int temp = (int) (this.nowTemp[h][k]/10);
                String symbol;
                if (temp >= 0 && temp < symbols.length) {
                    symbol = symbols[temp];
                } else {
                    symbol = symbols[symbols.length - 1]; // Use the highest symbol for out-of-range temperatures
                }
                System.out.print(symbol); // Changed printf to print
            }
            System.out.println();
        }
        System.out.println();
    }


    public void calNowTemp(int i, int j) {
            if (!isHeatSource[i][j]) {
                nowTemp[i][j] = (prevTemp[i - 1][j]+ prevTemp[i + 1][j] + prevTemp[i][j + 1] + prevTemp[i][j - 1])/4;
            }
    }

    public void calPrevTemp(int i, int j) {
        if (!isHeatSource[i][j]) {
            prevTemp[i][j] = (nowTemp[i - 1][j]+ nowTemp[i + 1][j] + nowTemp[i][j + 1] + nowTemp[i][j - 1])/4;
        }
    }

    public float getTempChange(int i, int j){
        return Math.abs(nowTemp[i][j] - prevTemp[i][j]);
    }


    public float[] matrikaToArrayPrevTemp() {
        float[] arrayPrevTemp = new float[row * col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                arrayPrevTemp[i * col + j] = prevTemp[i][j];
            }
        }

        return arrayPrevTemp;
    }

    public float[] matrikaToArrayNowTemp() {
        float[] arrayNowTemp = new float[row * col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                arrayNowTemp[i * col + j] = nowTemp[i][j];
            }
        }

        return arrayNowTemp;
    }

    public boolean[] matrikaToArrayIsHeatSource() {
        boolean[] arrayIsHeatSourc = new boolean[row * col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                arrayIsHeatSourc[i * col + j] = isHeatSource[i][j];
            }
        }

        return arrayIsHeatSourc;
    }

    public void arraysNTToMatrika(float[] arrayNowTemp) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                nowTemp[i][j] = (arrayNowTemp[i * col + j]);
            }
        }
    }

    public void arraysPTToMatrika(float[] arrayPrevTemp) {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                prevTemp[i][j] = (arrayPrevTemp[i * col + j]);
            }
        }

    }

}
