import java.util.Arrays;

// This is just a test class to check the correctness of certain methods
public class TestMethod {

    public static void main(String[] args) {
        int[] arr = new int[]{2, 7, 6, 9, 5, 1, 4, 3, 8};
        System.out.println(Arrays.toString(getInversionSequence(arr)));

        System.out.println(Arrays.toString(arr));
        arr = new int[]{1, 6, 6, 0, 0, 0, 2, 1, 0};
        System.out.println(Arrays.toString(getOriginSequence(arr)));
    }

    public static int[] getOriginSequence(int[] inv) {
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

    public static int[] getInversionSequence(int[] arr){
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
}
