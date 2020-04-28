package prison;
import java.util.ArrayList;
import java.util.Random;
import prison.Algorithm.Offspring;
public class GeneticFunctions {

    Random r = new Random();

    int keySize;
    int popSize;
    double crossingRate;
    double mutationRate;

    public GeneticFunctions(int keySize, int popSize, double crossingRate, double mutationRate) {
        this.keySize = keySize;
        this.popSize = popSize;
        this.crossingRate = crossingRate;
        this.mutationRate = mutationRate;

    }
    
    public void cross(ArrayList<Offspring> generation1, Offspring s1, Offspring s2, Offspring p1, Offspring p2) {

        
        int border = r.nextInt(keySize - 2) + 1;
   /*     if(longStop==1){
            border = r.nextInt(14) + 37; 
           
        } */
        String pom1 = "";
        String pom2 = "";

        for (int i = 0; i < keySize; i++) {
      
            if (i< border) {
                pom1 += s1.key.charAt(i);
                pom2 += s2.key.charAt(i);

            } else {
                pom1 += s2.key.charAt(i);
                pom2 += s1.key.charAt(i);
            }

        }

        p1.key = pom1;
        p2.key = pom2;
        generation1.add(p1);
        generation1.add(p2);

    }
       
    public void mutate(ArrayList<Offspring> generation1, Offspring s1, Offspring p3) {

        int idx1 = r.nextInt(keySize);        
        
        char c = (char) (r.nextInt(2) + 48);      
        char ch[] = s1.key.toCharArray();

        ch[idx1] = c;

        p3.key = "";

        for (int i = 0; i < ch.length; i++) {

            p3.key += ch[i];

        }

        generation1.add(p3);

    }

    public ArrayList<Offspring> populate(ArrayList<Offspring> list, Offspring p1, Offspring p2, Offspring p3) {

        int losowanie;
        int index1;
        int index2;

        // int fittestIndex=list.size()-1; // żeby nie mutować go
        list.addAll(list);

        while (list.size() < popSize) // uzupełniamy populacje
        {

            losowanie = r.nextInt(100);

            if (losowanie > crossingRate) {
                index1 = r.nextInt(list.size());
                index2 = r.nextInt(list.size());
                cross(list, list.get(index1), list.get(index2), p1, p2);
            
            }
            losowanie = r.nextInt(10000);

            if (losowanie <= mutationRate * 100) {
                index1 = r.nextInt(list.size());
                mutate(list, list.get(index1), p3);

            }
        }

        return list;

    }

}
