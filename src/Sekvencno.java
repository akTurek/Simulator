public class Sekvencno {
    public MatrikaCelic matrikaCelic;


    public Sekvencno(int row, int col, int numOfHeat){
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
    }

    public MatrikaCelic getMatrikaCelic() {
        return matrikaCelic;
    }

    public void calTemp() {
        long t0 = System.currentTimeMillis();
        int rows = matrikaCelic.getRow();
        int cols = matrikaCelic.getCol();
        do{
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                   matrikaCelic.calPrevTemp(i,j);
                }
            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrikaCelic.calNowTemp(i,j);
                }
            }

        }while (!isOver(rows, cols));
        long t1 = System.currentTimeMillis();
        System.out.println("Trajanje programa v ms: " +(t1-t0)+ " max temp change "+maxTempChange(rows, cols));
        matrikaCelic.printMatriko();
    }
    public boolean isOver(int rows, int cols){
        float maxTempChange = 0;
        float change;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                change = matrikaCelic.tempChange(i,j);
                if (change>maxTempChange){
                    maxTempChange = change;
                }
            }
        }
       // System.out.println(maxTempChange);
        if (maxTempChange >= 0.25) {
            return false;
        }else {
            return true;
        }
    }

    public float maxTempChange(int rows, int cols){
        float maxTempChange = 0;
        float change = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                change = matrikaCelic.tempChange(i, j);
                if (change >= maxTempChange) {
                    maxTempChange = change;

                }
            }
        }

        return maxTempChange;
    }


}
