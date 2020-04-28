package prison;

import prisonGUI.MainFrame;

import java.util.Random;

import javax.swing.JPanel;

import prisonGUI.PrisonGeneratorPanel;

public class Prison /*extends JPanel*/ {

    public static void main(String[] args) {

        MainFrame frame = new MainFrame("Prison");

        //Prison p1 = new Prison();
        frame.add(new PrisonGeneratorPanel());
        frame.pack();
        frame.setVisible(true);

        //Algorithm a1 = new Algorithm(100000, 1000000, 20, 80, 80, 2.9); // maxPop, popSize, budget, crossingRate, mutationRate, acceptedQuality,   
        // ciekawe przypadki do testowania:
        // 1. popSize=1000, crossingRate=50, mutationRate=2 
        // 2. popSize=20,   crossingRate=40, mutationRate=0.5  --- ma małe skoki w jakości, stopniowa poprawa, ale potrafi bardzo wolno dochodzić do końca
        // 3. popSize=50,   crossingRate=80, mutationRate=0.5
        // 4. popSize=50,   crossingRate=80, mutationRate=2
        // 5. popSize=50,   crossingRate=80, mutationRate=5  --- najszybciej dochodzi do optymalnych rozwiązań, ale skoki w jakości bywają duże
        // algorytm najfajniej działa dla niskich populacji, crossing rate około 40-50% i niskiej mutacji
        //a1.runPreAlgorithm();
        //while (a1.popNumber < a1.popMaxNumber /*&& a1.theFittestScore < a1.acceptedQuality*/) {
        //    a1.runAlgorithm();
        //    frame.add(new PrisonGeneratorPanel(a1.popNumber, a1.theFittestScore, a1.X1, a1.H1, a1.X2, a1.H2,
        //            a1.cellWidth, a1.cellHeight, a1.cellQuantity, a1.bedQuantity, a1.cameraQuantity));
        //    frame.pack();
        //    frame.setVisible(true);
            //runGui(a1.popNumber, a1.theFittestScore, a1.X1, a1.H1, X2, H2, cellWidth, cellHeight, cellQuantity, bedQuantity, cameraQuantity);
            //do twojejego sposobu rysowania podaje wszystkie potrzebne argumenty z każdej kolejnej generacji
            //popNumber i theFittestScore były by spoko do wyświetlania gdzieś obok, żeby użytkownik wiedział która to jest generacja i jaki ma wynik
        //}
    }

}
