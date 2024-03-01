public class MatrikaCelicDistributivno {

    float [] arrayPrevTemp;
    float [] arrayNowTemp;
    boolean [] arrayIsHeatSourc;
    int cols;

    public MatrikaCelicDistributivno(int arraySize, int cols) {
        this.arrayNowTemp = new float[arraySize];
        this.arrayPrevTemp = new float[arraySize];
        this.arrayIsHeatSourc = new boolean[arraySize];
        this.cols = cols;
    }

    public void setArrays(float[] arrayPrevTemp, float[] arrayNowTemp, boolean[] arrayIsHeatSourc) {
        this.arrayPrevTemp = arrayPrevTemp;
        this.arrayIsHeatSourc = arrayIsHeatSourc;
        this.arrayNowTemp = arrayNowTemp;
    }



    public void calNowTemp(int cols, float arraz) {


    }

    public void calPrevTemp(int i, int j, int rows, int cols) {

    }



}


