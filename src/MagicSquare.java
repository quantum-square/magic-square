public class MagicSquare {

    private int[][][] population1;
    private int[][][] population2;
    private final int gridSize;
    private final int populationSize;
    private final int sum;

    public MagicSquare(int gridSize, int populationSize) {
        this.gridSize = gridSize;
        this.populationSize = populationSize;
        population1 = new int[populationSize][gridSize][gridSize];
        population2 = new int[populationSize][gridSize][gridSize];
        this.sum = (1 + gridSize * gridSize) * gridSize / 2;
    }

    public int calculateFitness(int[][] square){
        int fit = 0;
        int length = square.length;

        for (int i = 0; i < length; i++) {
            int sumLine = 0;
            int sumColumn = 0;
            for (int j = 0; j < length; j++)
                sumLine += square[i][j];
            fit += Math.abs(sumLine - this.sum);
            for (int j = 0; j < length; j++)
                sumColumn += square[j][i];
            fit += Math.abs(sumColumn - this.sum);
        }

        int sumDiagonal = 0;
        for (int i = 0; i < length; i++)
            sumDiagonal += square[i][i];
        fit += Math.abs(sumDiagonal - this.sum);

        sumDiagonal = 0;
        for (int i = 0; i < length; i++)
            sumDiagonal += square[i][length-i-1];
        fit += Math.abs(sumDiagonal - this.sum);

        return fit;
    }

}
