public class Main {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        MagicSquare magicSquare = new MagicSquare(4, 100);
        magicSquare.startGeneticAlgorithm(1000000000, 0.5);

        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
    }
}
