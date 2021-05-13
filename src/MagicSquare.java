import java.util.Arrays;

public class MagicSquare {

    private int[][] population;
    private boolean[] fixed;
    private boolean enableDebug;
    private final int gridSize;
    private final int populationSize;
    private final int sum;

    public MagicSquare(int gridSize, int populationSize, boolean[] fixed,
                       int[] initialState, boolean enableDebug) {
        this(gridSize, populationSize, enableDebug);
        this.fixed = fixed.clone();

        for (int i = 0; i < populationSize; i++){
            population[i] = initialState.clone();
        }
    }

    public MagicSquare(int gridSize, int populationSize) {
        this(gridSize, populationSize, false);
    }

    public MagicSquare(int gridSize, int populationSize, boolean enableDebug) {
        this.gridSize = gridSize;
        this.populationSize = populationSize;
        this.enableDebug = enableDebug;
        population = new int[populationSize][gridSize*gridSize];
        this.fixed = new boolean[gridSize*gridSize];
        this.sum = (1 + gridSize * gridSize) * gridSize / 2;
    }

    public void initializePopulation(){
        for (int i = 0; i < populationSize; i++){
            for (int j = 0; j < gridSize*gridSize; j++) {
                population[i][j] = j+1;
            }
            for (int j = 0; j < gridSize*gridSize; j++) {
                int r = (int)(Math.random()*gridSize*gridSize);
                int temp = population[i][j];
                population[i][j] = population[i][r];
                population[i][r] = temp;
            }
        }
    }

    public void startGeneticAlgorithm(int generationSize, double pMut){
        initializePopulation();
        int t = 0;
        int lastFitness = 0;
        for (int i = 0; i < generationSize; i++) {
            int[][] newPopulation = new int[populationSize][];
            int[][] selectionPool = getSelectionPool();
            for (int j = 0; j < populationSize; j++) {
                int[] individualX = randomSelection(selectionPool);
                int[] individualY = randomSelection(selectionPool);
                int[] child = reproduce(individualX, individualY);
                if (Math.random() < pMut)
                    mutate(child);
                newPopulation[j] = child;
            }
            population = newPopulation;

            int minFitness = Integer.MAX_VALUE;
            for (int j = 0; j < populationSize; j++) {
                int fitness = calculateFitness(population[j]);
                if(fitness == 0){
                    System.out.println("Current generation: " + i);
                    printSquare(population[j]);
                    return;
                }
                else if(fitness < minFitness){
                    minFitness = fitness;
                }
            }

            if(lastFitness == minFitness)   t++;
            else                            t = 0;
            lastFitness = minFitness;
            if(t > gridSize*gridSize*500){
                t = 0;
                initializePopulation();
            }

            if (enableDebug) {
                System.out.println("Generation: " + i);
                System.out.println("Minimum fitness: " + minFitness);
            }
        }
    }

    public int[][] getSelectionPool(){
        PopulationFitness[] pf = new PopulationFitness[populationSize];
        for (int i = 0; i < populationSize; i++)
            pf[i] = new PopulationFitness(population[i], calculateFitness(population[i]));
        Arrays.sort(pf);
//        printSquare(pf[0].population);
        int[][] selectionPool = new int[populationSize / 2][];
        for (int i = 0; i < populationSize / 2; i++) {
            selectionPool[i] = pf[i].population;
        }
        return selectionPool;
    }

    private static class PopulationFitness implements Comparable<PopulationFitness>{
        int[] population;
        int fitness;

        public PopulationFitness(int[] population, int fitness){
            this.population = population;
            this.fitness = fitness;
        }

        @Override
        public int compareTo(PopulationFitness o) {
            if (this.fitness < o.fitness)
                return -1;
            else if (this.fitness > o.fitness)
                return 1;
            return 0;
        }
    }

    public int[] randomSelection(int[][] selectionPool){
        int x = (int) (Math.random() * populationSize/2);
        return selectionPool[x];
    }

    public int[] reproduce(int[] x, int[] y){
        int[] invX = getInversionSequence(x);
        int[] invY = getInversionSequence(y);

        // exchange
        int r = (int)(Math.random() * x.length);
        int temp = invX[r];
        invX[r] = invY[r];
        invY[r] = temp;

        int[] child1 = getOriginSequence(invX);
        int[] child2 = getOriginSequence(invY);
        if(calculateFitness(child1) < calculateFitness(child2))
            return child1;
        else
            return child2;
    }

    private int[] getInversionSequence(int[] arr){
        int[] inv = new int[arr.length];
        for (int i = 0; i < inv.length; i++) {
            int count = 0;
            for (int j = 0; j < arr.length; j++) {
                if(arr[j] == i+1){
                    break;
                }
                else if(arr[j] > i+1){
                    count++;
                }
            }
            inv[i] = count;
        }
        return inv;
    }

    private int[] getOriginSequence(int[] inv) {
        int[] pos = new int[inv.length];
        for (int i = pos.length-1; i >= 0; i--) {
            pos[i] = inv[i];
            for (int j = i+1; j < pos.length; j++) {
                if (pos[i] <= pos[j]){
                    pos[j]++;
                }
            }
        }
        int[] origin = new int[inv.length];
        for (int i = 0; i < inv.length; i++) {
            origin[pos[i]] = i+1;
        }
        return origin;
    }

    public void mutate(int[] child){
        int x = (int)(Math.random() * child.length);
        int y = (int)(Math.random() * child.length);
        int temp = child[x];
        child[x] = child[y];
        child[y] = temp;
    }

    // O(2n^2+2n): n is the gridSize
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

    public void printSquare(int[] arr){
        for (int k = 0; k < arr.length; k++) {
            System.out.printf("%4d ", arr[k]);
            if(k % gridSize == gridSize-1)
                System.out.println();
        }
    }

}
