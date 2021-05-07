public class Main {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        MagicSquare magicSquare = new MagicSquare(3, 10, true);
        magicSquare.startGeneticAlgorithm(1000000000, 0.2);

        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }
}
