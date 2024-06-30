public class MatrikaCelicDistributivno {

    float[] arrayPrevTemp;
    float[] arrayNowTemp;
    boolean[] arrayIsHeatSourc;

    float[] lowLimitPrevTemp;
    float[] upLimitPrevTemp;
    float[] lowLimitNowTemp;
    float[] upLimitNowTemp;

    int arraySize;
    int cols;

    int startIndexOfLastRow;
    int endIndexOfLastRow;


    public MatrikaCelicDistributivno(int arraySize, int cols) {
        this.arrayNowTemp = new float[arraySize];
        this.arrayPrevTemp = new float[arraySize];
        this.arrayIsHeatSourc = new boolean[arraySize];

        this.cols = cols;
        this.startIndexOfLastRow = arraySize - cols;
        this.endIndexOfLastRow = arraySize - 1;
        this.arraySize = arraySize;
        this.lowLimitPrevTemp = new float[cols];
        this.upLimitPrevTemp = new float[cols];
        this.lowLimitNowTemp = new float[cols];
        this.upLimitNowTemp = new float[cols];

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

    public void setLimits(float[] lowLimitPrevTemp, float[] upLimitPrevTemp, float[] lowLimitNowTemp, float[] upLimitNowTemp) {
        this.lowLimitPrevTemp = lowLimitPrevTemp;
        this.upLimitPrevTemp = upLimitPrevTemp;
        this.lowLimitNowTemp = lowLimitNowTemp;
        this.upLimitNowTemp = upLimitNowTemp;
    }

    public float[] getArrayPrevTemp() {
        return arrayPrevTemp;
    }

    public float[] getArrayNowTemp() {
        return arrayNowTemp;
    }

    public int calNowTempMiddle() {
        float maxChange = 0;
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

        if (maxChange <= 0.25) {
            return 1;
        } else {
            return 0;
        }
    }

    public void calPrevTempMidlle() {
        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
                if (i < cols - 1) {
                    arrayPrevTemp[i] = (lowLimitNowTemp[i] + arrayNowTemp[i + 1] + arrayNowTemp[i - 1] + arrayNowTemp[i + cols]) / 4;
                } else if (i >= startIndexOfLastRow) {
                    arrayPrevTemp[i] = (upLimitNowTemp[i % cols] + arrayNowTemp[i + 1] + arrayNowTemp[i - 1] + arrayNowTemp[i - cols]) / 4;
                } else {
                    arrayPrevTemp[i] = (arrayNowTemp[i + cols] + arrayNowTemp[i + 1] + arrayNowTemp[i - 1] + arrayNowTemp[i - cols]) / 4;
                }
            }
        }
    }

    public int calNowTempFirst() {
        float maxChange = 0;
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
        if (maxChange <= 0.25) {
            return 1;
        } else {
            return 0;
        }
    }

    public void calPrevTempFirst() {
        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
                if (i >= startIndexOfLastRow) {
                    arrayPrevTemp[i] = (upLimitNowTemp[i % cols] + arrayNowTemp[i + 1] + arrayNowTemp[i - 1] + arrayNowTemp[i - cols]) / 4;
                } else {
                    arrayPrevTemp[i] = (arrayNowTemp[i + cols] + arrayNowTemp[i + 1] + arrayNowTemp[i - 1] + arrayNowTemp[i - cols]) / 4;
                }
            }
        }
    }

    public int calNowTempLast() {
        float maxChange = 0;
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
        if (maxChange <= 0.25) {
            return 1;
        } else {
            return 0;
        }
    }

    public void calPrevTempLast() {
        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
                 if (i < cols - 1) {
                    arrayPrevTemp[i] = (lowLimitNowTemp[i] + arrayNowTemp[i + 1] + arrayNowTemp[i - 1] + arrayNowTemp[i + cols]) / 4;
                } else {
                    arrayPrevTemp[i] = (arrayNowTemp[i + cols] + arrayNowTemp[i + 1] + arrayNowTemp[i - 1] + arrayNowTemp[i - cols]) / 4;
                }
            }

        }
    }



    public int enCikelSumulacijeMiddle() {
        calPrevTempMidlle();
        int isOver = calNowTempMiddle();
        return isOver;
    }

    public int enCikelSumulacijeFirst() {
        calPrevTempFirst();
        int isOver = calNowTempFirst();
        return isOver;
    }

    public int enCikelSumulacijeLast() {
        calPrevTempLast();
        int isOver = calNowTempLast();
        return isOver;
    }


}


