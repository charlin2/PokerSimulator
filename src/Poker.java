import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;

public class Poker {
    
    private List<Card> deck;

    private Map<String, Double> frequencyTable;

    int[] ranks = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    int[] suits = {0x8000, 0x4000, 0x2000, 0x1000};

    public Poker() {
        frequencyTable = new HashMap<>();
        deck = new ArrayList<>();
        for (int suit : suits) {
            for (int rank : ranks) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    public void simulateRound(int n) {
        Collections.shuffle(deck);

        Card[][] players = new Card[n][2];
        Card[] communityCards = new Card[7];

        int j = 0;

        for (int i = 0; i < n; i++) {
            players[i][0] = deck.get(j++);
            players[i][1] = deck.get(j++);
        }

        for (int i = 0; i < 5; i++) {
            communityCards[i] = deck.get(j++);
        }

        int[] handValues = new int[n];
        Arrays.fill(handValues, Integer.MAX_VALUE);

        int i = 0;
        for (Card[] player : players) {
            communityCards[5] = player[0];
            communityCards[6] = player[1];
            List<Card[]> hands = generateHands(communityCards);
            for (Card[] hand : hands) {
                handValues[i] = Math.min(Hand.evaluate(hand), handValues[i]);
            }
            i++;
        }
       getWinner(handValues, players);
    }

    private void getWinner(int[] handValues, Card[][] players) {
        int lowestInd = 0;
        int lowestVal = handValues[0];
        for (int i = 1; i < handValues.length; i++) {
            if (handValues[i] < lowestVal) {
                lowestVal = handValues[i];
                lowestInd = i;
            }
        }
        char[] pairArr = {players[lowestInd][0].getRankChar(), players[lowestInd][1].getRankChar()};
        Arrays.sort(pairArr);
        String pair = new String(pairArr);
        if (players[lowestInd][0].getSuit() == players[lowestInd][1].getSuit()) {
            pair += "s";
        } else if (players[lowestInd][0].getRank() != players[lowestInd][1].getRank()){
            pair += "o";
        }
        frequencyTable.put(pair, frequencyTable.getOrDefault(pair, 0.0) + 1);
    }

    private List<Card[]> generateHands(Card[] cards) {
        List<Card[]> hands = new ArrayList<>();
        int n = cards.length;
        for (int bitmask = 0; bitmask < (1 << n); bitmask++) {
            List<Card> current = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((bitmask & (1 << i)) != 0) {
                    current.add(cards[i]);
                }
            }
            if (current.size() == 5)
                hands.add(current.toArray(new Card[0]));
        }
        return hands;

    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the number of players: ");
        int n = scan.nextInt();

        System.out.println("Enter the number of rounds you would like to simulate: ");
        int rounds = scan.nextInt();
        scan.close();

        Poker poker = new Poker();
        for (int i = 0; i < rounds; i++)
            poker.simulateRound(n);
        for (String pair : poker.frequencyTable.keySet()) {
            if (pair.length() == 2) {
                poker.frequencyTable.put(pair, Math.floor(poker.frequencyTable.get(pair) / (1.0/17)*100) / 100);
            } else if (pair.charAt(2) == 's') {
                poker.frequencyTable.put(pair, Math.floor(poker.frequencyTable.get(pair) / (1.0/4)*100) / 100);
            } else {
                poker.frequencyTable.put(pair, Math.floor(poker.frequencyTable.get(pair) / (47.0/68)*100) / 100);
            }
        }

        List<Map.Entry<String, Double>> freqList = new ArrayList<>(poker.frequencyTable.entrySet());
        Collections.sort(freqList, (e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));
        System.out.println(freqList);
        
    }
}