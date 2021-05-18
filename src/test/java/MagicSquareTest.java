import MagicSquareSolver.MagicSquare;
import org.junit.*;

import static org.junit.Assert.*;

public class MagicSquareTest {

    @Test
    public void test_calculateFitness() {
        MagicSquare ms3 = new MagicSquare(3, 1);
        int[] arr = {1, 2, 3 ,4, 5, 6, 7, 8, 9};
        assertEquals(24, ms3.calculateFitness(arr));

        arr = new int[]{9, 2, 4, 3, 5, 7, 6, 8, 1};
        assertEquals(6, ms3.calculateFitness(arr));

        MagicSquare ms4 = new MagicSquare(4, 1);
        arr = new int[]{1, 14, 7, 12, 8, 11, 2, 13, 10, 5, 16, 3, 15, 4, 9, 6};
        assertEquals(0, ms4.calculateFitness(arr));
    }
}
