package MagicSquareSolver;

public class Main {
    public static void main(String[] args) {
        double p = 0.000038;
        double x = 15;
        int maxsize = 1000;
        int lastTime = MagicSquareHeuristic.test(x, p);
        for (int i = 0; i < maxsize; i++) {
            double T = schedule(i);
            if(T == 0)
                break;
            double delta_p = Math.random() > 0.5 ?
                    (Math.random() * 0.0000025) : (Math.random() * -0.0000025);
            double delta_x = Math.random() > 0.5 ?
                    (Math.random() * 2) : (Math.random() * -2);
            double nextP = p + delta_p;
            double nextX = x + delta_x;
            int curTime = MagicSquareHeuristic.test(nextX, nextP);
            double delta_time = lastTime - curTime;
            if (delta_time > 0 || Math.exp(delta_time / T) > Math.random()){
                p = nextP;
                x = nextX;
                lastTime = curTime;
            }
            System.out.printf("%d %.4f %.4f %.8f\n", i, nextX, x, T);
        }

//        for (int i = 0; i < 100; i++) {
//            double delta = Math.random() > 0.5 ?
//                    Math.random() * 0.000005 : Math.random() * -0.000005;
//            System.out.printf("%.8f \n", delta);
//        }
    }

    public static double schedule(int t) {
        return 2000 * Math.exp(-0.05 * t);
    }
}
