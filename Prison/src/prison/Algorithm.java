package prison;

//hlko
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
public class Algorithm {

    ArrayList<Offspring> generation = new ArrayList<>();
    Offspring theFittest = new Offspring();
    double theFittestScore;
    double score;
    GeneticFunctions genetics;
    Random r;
    double mutationRate; //podaje user??
    double crossingRate; //podaje user??
    double defaultCrossingRate;
    double defaultMutationRate;
    double acceptedQuality; //podaje user, default=100%, wtedy koniec przy osiągnięciu popMax lub przycisk "STOP"    
    final int keySize = 70;
    int popSize; // liczba potomków w generacji
    final int evacuationRouteWidth = 20;
    final int bedWidth = 10;
    final int bedHeight = 20;
    final int wcWidth = 10;
    final int wcHeight = 10;
    final int cameraRange = 50;
    int popNumber; // indeks porządkowy przy wyświetlaniu kolejnych generacji
    int popMaxNumber; // liczba populacji, które użytkownik zdecydował się uruchomić??? - może będzie do usunięcia i po prostu leci aż do końca    
    int budget; //podaje user
    //Klucz:
    int X1, H1; // 9 bit zakres 0-511
    int X2, H2; // 9 bit zakres 0-511
    int cellWidth; // 8 bit? zakres 0-63
    int cellHeight; // 8 bit? zakres 0-63
    int cellQuantity; // 6 bit? zakres 0-63
    int bedQuantity; // 7 bit? zakres 0-127
    int cameraQuantity; // 7 bit? zakres 0-63
    //
    // Wygląd klucza (indeksy) : 
    // keySize = 72
    // 0-8 | 9-17 | 18-26 | 27-35 | 36-42 | 43-49 | 50-55 | 56-62 | 63-69 |
    //  X1 |  H1  |   X2  |   H2  | cellW | cellH | cellQ |  bedQ |  camQ |
    int maxCellQuantity;
    int maxBedQuantity;
    int optimalCameraQuantity;
    int minCellWidth;
    int minCellHeight;
    int camerasPerCell;
    int moneySpent;

    public Algorithm(int popMax, int budget, int popSize, double crossingRate, double mutationRate,
            double acceptedQuality) {

        r = new Random();

        this.popMaxNumber = popMax;
        this.popSize = popSize;
        this.budget = budget;
        this.crossingRate = crossingRate;
        this.mutationRate = mutationRate;
        this.defaultMutationRate = mutationRate;
        this.defaultCrossingRate = crossingRate;
        this.acceptedQuality = acceptedQuality;
        this.genetics = new GeneticFunctions(keySize, popSize, crossingRate, mutationRate);

    }

    public class Offspring implements Comparable<Offspring> {

        public String key;
        @Override

        public int compareTo(Offspring p) {
            if (getFitness(this.key) > getFitness(p.key)) {
                return 1;
            } else if (getFitness(this.key) < getFitness(p.key)) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    private String normalizeNumber(String key) {

        String buffer = "";
        int flaga = 0;
        for (int i = 0; i < key.length(); i++) {
            if (key.charAt(i) == '1' && flaga == 0) {
                flaga = 1;
            }
            if (flaga == 1) {
                buffer += key.charAt(i);
            }
        }
        if (buffer.equals("")) {
            buffer += '0';
        }
        return buffer;
    }

    private void interpreteKey(String key) {

        String buffer = "";
        for (int i = 0; i < keySize; i++) {
            buffer += key.charAt(i);
            
            switch (i) {

                case 8:
                    X1 = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;
                case 17:
                    H1 = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;
                case 26:
                    X2 = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;
                case 35:
                    H2 = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;
                case 42:
                    cellWidth = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;
                case 49:
                    cellHeight = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;
                case 55:
                    cellQuantity = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;
                case 62:
                    bedQuantity = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;
                case 69:
                    cameraQuantity = Integer.parseInt(normalizeNumber(buffer), 2);
                    buffer = "";
                    break;

            }

            minCellWidth = 2 * bedWidth + evacuationRouteWidth;
            minCellHeight = bedHeight + evacuationRouteWidth + wcHeight;

        }

    }

    ///FUNKCJE FITNESS  
    int prizonShapeParameter(int X1, int H1, int X2, int H2, int cellWidth, int cellHeight) {

        if (X2 >= X1) {
            return 0;
        }

        if (H2 >= H1) {
            return 0;
        }

        if ((X1 - X2) < (cellHeight + evacuationRouteWidth)) {
            return 0;
        }

        if ((H1 - H2) < (cellHeight + evacuationRouteWidth)) {
            return 0;
        }

        if (X1 == 0 || H1 == 0 || X2 == 0 || H2 == 0) {
            return 0;//return 0;
        }

        return 1;

    }

    int cellShapeParamater(int cellWidth, int cellHeight, int minCellWidth, int minCellHeight) {

        if (cellWidth < minCellWidth || cellHeight < minCellHeight) {
            return -50;  
           //return 0;
        }

        return 1;

    }

    double cellQuantityParameter(int maxCellQuantity, int cellQuantity) {

        if (maxCellQuantity == 0 ) {
            return -50;
        }

        if (cellQuantity > maxCellQuantity) {
            return 1;//-1*((double)cellQuantity/(double)maxCellQuantity-1); // np: 12/4 zwroci jako -2, 5/4 zwroci jako -0.2 itd...
        } 
        return (double) cellQuantity / (double) maxCellQuantity;

    }

    double bedQuantityParameter(int maxBedQuantity, int bedQuantity) {

        if (maxBedQuantity == 0) {
            return -50;
        }

  /*      if (bedQuantity > maxBedQuantity) {
            return -50;//-1*((double)bedQuantity/(double)maxBedQuantity-1);
        } */
        if (bedQuantity > maxBedQuantity) return -50;
        
        return (double) bedQuantity / (double) maxBedQuantity;

    }

    double cameraQuantityParameter(int optimalCameraQuantity, int cameraQuantity) {

        if (optimalCameraQuantity == 0) {
            return 0;
        }

        if (cameraQuantity > optimalCameraQuantity) {
            return 0.8;
        } 
        return (double) cameraQuantity / (double) optimalCameraQuantity;

    }

    double budgetParameter(int budget, int maxCellQuantity, int maxBedQuantity, int cameraQuantity) {

        if (budget == 0) {
            return 0;
        }

        int money = cameraQuantity * 20 + maxBedQuantity * 10 + maxCellQuantity * 50;
        if (money > budget) {
            return 0;
        } 
        return (double) money / (double) budget;
    }
    
    double densityParameter(int X1, int H1, int X2, int H2, int cellWidth, int cellHeight, int cellQuantity, int maxCellQuantity) {

        double prizonArea = X1 * H1 - X2 * H2;
        double cellArea = cellWidth * cellHeight * cellQuantity;
        if(cellQuantity>maxCellQuantity) 
            cellArea = cellWidth * cellHeight * maxCellQuantity;
        
        if (prizonArea < cellArea) {
            return 0.4;
        }

        return (double) cellArea / (double) prizonArea;

    }

    double cellComfortParameter(int cellQuantity, int bedQuantity){
          
        if(maxCellQuantity==0) return 0;
        if(bedQuantity>maxBedQuantity) return 0.5;
        if ((double)bedQuantity/(double)maxCellQuantity > 8) return 0.1;
        else if ((double)bedQuantity/(double)cellQuantity > 6) return 0.2;
        else if ((double)bedQuantity/(double)cellQuantity > 4) return 0.3;
        else if ((double)bedQuantity/(double)cellQuantity >2) return 0.4;
        else if ((double)bedQuantity/(double)cellQuantity >1) return 1;
        return 0;
    }

    int maxCellQuantity(int X1, int H1, int X2, int H2, int cellWidth, int cellHeight) {

        int max = 0;
        if (cellWidth == 0) {
            return 0;
        }
        max += (H1 - cellWidth) / cellWidth;
        max += (X1 - cellHeight - evacuationRouteWidth) / cellWidth;
        if ((X1 - X2) >= (2 * cellHeight + evacuationRouteWidth)) {
            max += H2 / cellWidth;
        }
        if ((H1 - H2) >= (2 * cellHeight + evacuationRouteWidth)) {
            max += X2 / cellWidth;
        }
        max -= 1; // na security room
        if (max < 0) {
            max = 0;// chodzi o to żeby nie było ujemnych wartości
        }
        return max;

    }

    int maxBedQuantity(int maxCellQuantity, int cellQuantity, int cellWidth, int cellHeight) {
        
        int max=2*cellQuantity*maxBedColumnsOnOneSideInCell()*maxBedRowsInCell();
     /*   int x=cellQuantity;
        if(cellQuantity>maxCellQuantity) x=maxCellQuantity;
        
        int max = (int) 1.3*x * (2 * (cellHeight / (bedHeight + evacuationRouteWidth))
                * (((cellWidth - evacuationRouteWidth) / 2 - bedWidth) / (bedWidth + evacuationRouteWidth)) );
        if (max < 0) {
            max = 0; // chodzi o to żeby nie było ujemnych wartości

        }
        */
        return max;

    } 
    public int maxBedColumnsOnOneSideInCell() {
        int maxColumns = 0;
        int totalWidth = 0;
        if ((((cellWidth - evacuationRouteWidth) / 2) - (4 / 2)) >= bedWidth) {
            maxColumns++;
            totalWidth += (bedWidth + (4 / 2));
        }
        while (true) {
            if ((totalWidth + evacuationRouteWidth + bedWidth) <= ((cellWidth - evacuationRouteWidth - 4) / 2)) {
                maxColumns++;
                totalWidth += evacuationRouteWidth + bedWidth;
            } else {
                break;
            }
        }
        return maxColumns;
    }

    public int maxBedRowsInCell() {
        int maxRows = 0;
        int totalHeight = 0;
        if (cellHeight - 4 >= bedHeight) {
            maxRows++;
            totalHeight += (bedHeight + 4);
        }
        while (true) {
            if ((totalHeight + evacuationRouteWidth + bedHeight) <= cellHeight) {
                maxRows++;
                totalHeight += evacuationRouteWidth + bedHeight;
            } else {
                break;
            }
        }
        return maxRows;
    }

    int optimalCameraQuantity(int X1, int H1, int X2, int H2,
            int maxCellQuantity, int cellWidth, int cellHeight, int camerasPerCell) {
        int optimal;
        int cellsCameraQuantity = maxCellQuantity * camerasPerCell;
        int corridorCameraQuantity = (H1 - cellHeight) / (2 * cameraRange) + 1;
        corridorCameraQuantity += (X1 - cellWidth) / (2 * cameraRange) + 1;
        corridorCameraQuantity += (H2 - cellWidth) / (2 * cameraRange) + 1;
        corridorCameraQuantity += (X2 - cellWidth) / (2 * cameraRange) + 1;
        optimal = cellsCameraQuantity + corridorCameraQuantity;

        if (optimal < 0) {

            optimal = 0; // chodzi o to żeby nie było ujemnych wartości

        }

        return 2*optimal;

    }

    int camerasPerCell(int cellWidth, int cellHeight) {
        int cameras = 0;
        int diagonal = (int) Math.sqrt((Math.pow(cellWidth, 2) + Math.pow(cellHeight, 2)));
        if (diagonal <= 60) {

            cameras = 1; 

        } else {
            cameras = 2;
        }

        return cameras;
    }
    

    public double getFitness(String wynik) {

        double fitness = 0;
        interpreteKey(wynik);
        maxCellQuantity = maxCellQuantity(X1, H1, X2, H2, cellWidth, cellHeight);
        maxBedQuantity = maxBedQuantity(maxCellQuantity, cellQuantity, cellWidth, cellHeight);        
        optimalCameraQuantity = optimalCameraQuantity(X1, H1, X2, H2, maxCellQuantity, cellWidth, cellHeight, camerasPerCell);
        
        
        if (prizonShapeParameter(X1, H1, X2, H2, cellWidth, cellHeight) == 0) {

            fitness -= 1000; // do ustalenia
           
        }

        if (cellShapeParamater(cellWidth, cellHeight, minCellWidth, minCellHeight) == 0) {

            fitness -= 1000; // do ustalenia
            return 0;
        }

        fitness += cellQuantityParameter(maxCellQuantity, cellQuantity)
                + 2*bedQuantityParameter(maxBedQuantity, bedQuantity)
                + cameraQuantityParameter(optimalCameraQuantity, cameraQuantity)
                + budgetParameter(budget, maxCellQuantity, maxBedQuantity, cameraQuantity)
                + 2*densityParameter(X1, H1, X2, H2, cellWidth, cellHeight, cellQuantity, maxCellQuantity)
                + 4*cellComfortParameter(cellQuantity, bedQuantity);
      
        return fitness;

    }

    ///KONIEC FITNESS
    public Offspring getTheFittest(ArrayList<Offspring> list) { //biorę ostatniego bo posortowane

        Offspring result;

        //  Collections.sort(list);
        result = list.get(list.size() - 1);

        return result;

    }

    public void eliminateWeakest(ArrayList<Offspring> list) {

        //  Collections.sort(list);
        for (int i = 0; i < (int) (0.9 * popSize); i++) {
            list.remove(0);
        }
    }

    public void generateSeed() {

        Random y;
        char c;

        for (int i = 0; i < popSize; i++) {
            Offspring o1 = new Offspring();
            String seed1 = "";

            for (int j = 0; j < keySize; j++) {
                y = new Random();
                c = (char) (y.nextInt(2) + 48);
                seed1 += c;
            }

            o1.key = seed1;
            generation.add(o1);

        }

    }

    public void print(String key) { // tylko do testów

        System.out.println(popNumber + ". CQty: " + cellQuantityParameter(maxCellQuantity, cellQuantity) + " BQty: " 
                + bedQuantityParameter(maxBedQuantity, bedQuantity) 
                + " camQty: " + cameraQuantityParameter(optimalCameraQuantity, cameraQuantity) 
                + " budget: " + budgetParameter(budget, cellQuantity, bedQuantity, cameraQuantity)
                + " density: " + densityParameter(X1, H1, X2, H2, cellWidth, cellHeight, cellQuantity, maxCellQuantity) 
                + " comfort: " + cellComfortParameter(maxCellQuantity, maxBedQuantity) + " fit: " + getFitness(key)
                + " maxB: "+maxBedQuantity + " cellQ: "+cellQuantity+" maxC: "+maxCellQuantity 
                +" camQ: "+cameraQuantity+" maxCam: "+optimalCameraQuantity);

    }

    public void runPreAlgorithm() {

        popNumber = 0;
        generateSeed();
        popNumber++;
        Collections.sort(generation);
        theFittest = getTheFittest(generation);
        interpreteKey(theFittest.key);
        theFittestScore = getFitness(theFittest.key);
        print(theFittest.key);

    }
    int noChange=0;
    int longStop=0;
    public void runAlgorithm() {
       
        Offspring p1 = new Offspring();
        Offspring p2 = new Offspring();
        Offspring p3 = new Offspring();
        
        Collections.sort(generation);
        eliminateWeakest(generation);
             
        generation = genetics.populate(generation, p1, p2, p3);
        popNumber++;
        Collections.sort(generation);
        noChange++;
        
        System.out.println("TheFittest: ");
        
        if (getFitness(theFittest.key) < getFitness(getTheFittest(generation).key)) {
            theFittest = getTheFittest(generation);
            noChange=0;
            longStop=0;
            mutationRate=2;
        }
        interpreteKey(theFittest.key);
        theFittestScore = getFitness(theFittest.key);      
        print(theFittest.key);


    }

    /**
     * @return the popNumber
     */
    public int getPopNumber() {
        return popNumber;
    }

    /**
     * @return the popMaxNumber
     */
    public int getPopMaxNumber() {
        return popMaxNumber;
    }

    /**
     * @return the X1
     */
    public int getX1() {
        return X1;
    }

    /**
     * @return the H1
     */
    public int getH1() {
        return H1;
    }

    /**
     * @return the X2
     */
    public int getX2() {
        return X2;
    }

    /**
     * @return the H2
     */
    public int getH2() {
        return H2;
    }

    /**
     * @return the cellWidth
     */
    public int getCellWidth() {
        return cellWidth;
    }

    /**
     * @return the cellHeight
     */
    public int getCellHeight() {
        return cellHeight;
    }

    /**
     * @return the cellQuantity
     */
    public int getCellQuantity() {
        return cellQuantity;
    }

    /**
     * @return the bedQuantity
     */
    public int getBedQuantity() {
        return bedQuantity;
    }

    /**
     * @return the cameraQuantity
     */
    public int getCameraQuantity() {
        return cameraQuantity;
    }

    /**
     * @return the theFittestScore
     */
    public double getTheFittestScore() {
        return theFittestScore;
    }

    /**
     * @param cameraQuantity the cameraQuantity to set
     */
    public void setCameraQuantity(int cameraQuantity) {
        this.cameraQuantity = cameraQuantity;
    }
}
