public class MagicSquare {

    private int[][] population1;
    private int[][] population2;
    private final int gridSize;
    private final int populationSize;
    private final int sum;

    public MagicSquare(int gridSize, int populationSize) {
        this.gridSize = gridSize;
        this.populationSize = populationSize;
        population1 = new int[populationSize][gridSize*gridSize];
        population2 = new int[populationSize][gridSize*gridSize];
        this.sum = (1 + gridSize * gridSize) * gridSize / 2;
    }

    public int calculateFitness(int[] square){
        int fit = 0;

        for (int i = 0; i < gridSize; i++) {
            int sumLine = 0;
            for (int j = i*gridSize; j < (i+1)*gridSize; j++)
                sumLine += square[j];
            fit += Math.abs(sumLine - this.sum);
        }

        for (int i = 0; i < gridSize; i++) {
            int sumColumn = 0;
            for (int j = 0; j < gridSize; j++)
                sumColumn += square[i+j*gridSize];
            fit += Math.abs(sumColumn - this.sum);
        }

        int sumDiagonal = 0;
        for (int i = 0; i < gridSize*gridSize; i+=(gridSize+1))
            sumDiagonal += square[i];
        fit += Math.abs(sumDiagonal - this.sum);

        sumDiagonal = 0;
        for (int i = gridSize-1; i < gridSize*gridSize-1; i+=(gridSize-1))
            sumDiagonal += square[i];
        fit += Math.abs(sumDiagonal - this.sum);

        return fit;
    }

}
