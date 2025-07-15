public class MatrikaCelicDistributivno {

    float[] arrayPrevTemp;
    float[] arrayNowTemp;
    boolean[] arrayIsHeatSourc;

    float[] lowLimitPrevTemp;
    float[] upLimitPrevTemp;

    int arraySize;
    int cols;
    int startIndexOfLastRow;



    public MatrikaCelicDistributivno(int arraySize, int cols) {
        this.arrayNowTemp = new float[arraySize];
        this.arrayPrevTemp = new float[arraySize];
        this.arrayIsHeatSourc = new boolean[arraySize];

        this.cols = cols;
        this.startIndexOfLastRow = arraySize - cols;

        this.arraySize = arraySize;
        this.lowLimitPrevTemp = new float[cols];
        this.upLimitPrevTemp = new float[cols];

    }

    public float[] getMyPartF(float[] celArray, int size) {
        float [] myPart = new float[size];
        for (int i = 0; i < size; i++) {
            myPart[i] = celArray[i];
        }
        return myPart;
    }

    public boolean[] getMyPartB(boolean[] celArray, int size) {
        boolean [] myPart = new boolean[size];
        for (int i = 0; i < size; i++) {
            myPart[i] = celArray[i];
        }
        return myPart;
    }

    public void setArrays(float[] arrayPrevTemp, float[] arrayNowTemp, boolean[] arrayIsHeatSourc) {
        this.arrayPrevTemp = arrayPrevTemp;
        this.arrayIsHeatSourc = arrayIsHeatSourc;
        this.arrayNowTemp = arrayNowTemp;
    }

    public void setLimits(float[] lowLimitPrevTemp, float[] upLimitPrevTemp) {
        this.lowLimitPrevTemp = lowLimitPrevTemp;
        this.upLimitPrevTemp = upLimitPrevTemp;
    }

    public float[] getArrayPrevTemp() {
        return arrayPrevTemp;
    }

    public float[] getArrayNowTemp() {
        return arrayNowTemp;
    }

    public float calNowTempMiddle() {
        float maxChange = 0.F;
        float change;


        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
                if (i < cols - 1) {
                    arrayNowTemp[i] = (lowLimitPrevTemp[i] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i + cols]) / 4;
                } else  if (i >= startIndexOfLastRow) {
                    arrayNowTemp[i] = (upLimitPrevTemp[i % cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                } else {
                    arrayNowTemp[i] = (arrayPrevTemp[i + cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                }
                change = Math.abs(arrayNowTemp[i] - arrayPrevTemp[i]);
                if (change > maxChange) {
                    maxChange = change;
                }
            }
        }

        return maxChange;
    }



    public float calNowTempFirst() {
        float maxChange = 0.F;
        float change;
        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
                 if (i >= startIndexOfLastRow) {
                    arrayNowTemp[i] = (upLimitPrevTemp[i % cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                } else {
                    arrayNowTemp[i] = (arrayPrevTemp[i + cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                }

                 change = Math.abs(arrayNowTemp[i] - arrayPrevTemp[i]);
                if (change > maxChange) {
                    maxChange = change;
                }
            }
        }
        return maxChange;
    }


    public float calNowTempLast() {
        float maxChange = 0.F;
        float change;
        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
               if (i < cols - 1) {
                    arrayNowTemp[i] = (lowLimitPrevTemp[i] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i + cols]) / 4;
                }else {
                    arrayNowTemp[i] = (arrayPrevTemp[i + cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                }

                change = Math.abs(arrayNowTemp[i] - arrayPrevTemp[i]);
                if (change > maxChange) {
                    maxChange = change;
                }
            }
        }
        return maxChange;
    }




    public float enCikelSumulacijeMiddle() {

        float maxChamge = calNowTempMiddle();
        arrayPrevTemp = new float[arrayNowTemp.length];
        System.arraycopy(arrayNowTemp, 0, arrayPrevTemp, 0, arrayNowTemp.length);
        return maxChamge;
    }

    public float enCikelSumulacijeFirst() {

        float maxChamge = calNowTempFirst();
        arrayPrevTemp = new float[arrayNowTemp.length];
        System.arraycopy(arrayNowTemp, 0, arrayPrevTemp, 0, arrayNowTemp.length);
        return maxChamge;
    }

    public float enCikelSumulacijeLast() {

        float maxChamge = calNowTempLast();
        arrayPrevTemp = new float[arrayNowTemp.length];
        System.arraycopy(arrayNowTemp, 0, arrayPrevTemp, 0, arrayNowTemp.length);
        return maxChamge;
    }


}


