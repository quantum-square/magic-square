package web.model;

/**
 * Checkerboard parameters, front-end and back-end data transmission objects.
 *
 * @version 1.0
 * @date 2021/5/16 9:22
 */
public class BoardDTO {

    int[][] board;

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

}
