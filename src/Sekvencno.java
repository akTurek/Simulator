public class Sekvencno {
    public MatrikaCelic matrikaCelic;


    public Sekvencno(int row, int col, int numOfHeat){
        this.matrikaCelic = new MatrikaCelic(row, col, numOfHeat);
    }



    public void calTemp() {
        float maxChange;
        float change;
        int c = 0;
        long t0 = System.currentTimeMillis();
        int rows = matrikaCelic.getRow();
        int cols = matrikaCelic.getCol();
        do{

            maxChange = 0.F;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                   matrikaCelic.calPrevTemp(i,j);
                }
            }

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    matrikaCelic.calNowTemp(i,j);
                    change = matrikaCelic.getTempChange(i,j);
                    if (change > maxChange) {
                        maxChange = change;
                    }
                }
            }
            c++;
        }while (maxChange > 0.25F);
        long t1 = System.currentTimeMillis();
        System.out.println("Trajanje programa v ms: " +(t1-t0)+ " max temp change "+maxChange+" cikljev " + c);
        matrikaCelic.matrikaJPG("sek");
    }



}
