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

    /* ZA racunanje ce bi prisla 1 vrstica na Racunalnik
    if (startIndexOfLastRow == 0) {
                        arrayPrevTemp[i] = (lowLimitNowTemp[i] + arrayNowTemp[i + 1] + arrayNowTemp[i - 1] + upLimitNowTemp[i]) / 4;
                }
     */

    public void calNowTempMiddle() {
        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
                if (i < cols - 1) {
                    arrayNowTemp[i] = (lowLimitPrevTemp[i] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i + cols]) / 4;
                } else  if (i >= startIndexOfLastRow) {
                    arrayNowTemp[i] = (upLimitPrevTemp[i % cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                } else {
                    arrayNowTemp[i] = (arrayPrevTemp[i + cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                }
            }
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

    public void calNowTempFirst() {
        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
                 if (i >= startIndexOfLastRow) {
                    arrayNowTemp[i] = (upLimitPrevTemp[i % cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                } else {
                    arrayNowTemp[i] = (arrayPrevTemp[i + cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                }
            }
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

    public void calNowTempLast() {
        for (int i = 0; i < arraySize; i++) {
            if (!arrayIsHeatSourc[i]) {
               if (i < cols - 1) {
                    arrayNowTemp[i] = (lowLimitPrevTemp[i] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i + cols]) / 4;
                }else {
                    arrayNowTemp[i] = (arrayPrevTemp[i + cols] + arrayPrevTemp[i + 1] + arrayPrevTemp[i - 1] + arrayPrevTemp[i - cols]) / 4;
                }
                //System.out.println("Now temp last " +arrayPrevTemp[i]);
            }
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


    public int isOver() {
        float maxTempChange = 0;
        float tempChange;
        for (int i = 0; i < arraySize; i++) {
            tempChange = Math.abs(arrayPrevTemp[i] - arrayNowTemp[i]);
            if (tempChange > maxTempChange) {
                maxTempChange = tempChange;
            }
        }
        System.out.println(maxTempChange);

        if (maxTempChange <= 0.25) {
            return 1;
        } else {
            return 0;
        }

    }

    public int enCikelSumulacijeMiddle() {
        calPrevTempMidlle();
        calNowTempMiddle();
        return isOver();
    }

    public int enCikelSumulacijeFirst() {
        calPrevTempFirst();
        calNowTempFirst();
        return isOver();
    }

    public int enCikelSumulacijeLast() {
        calPrevTempLast();
        calNowTempLast();
        return isOver();
    }


}


