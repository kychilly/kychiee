package com.kychilly.DiscordBot.classes;

import java.util.Random;

public class MinesweeperGame {
    private final int width;
    private final int height;
    private final int bombCount;
    private int[][] board;
    private String[][] visibleBoard;
    private boolean firstClick;
    private boolean gameOver;
    private final Random random = new Random();

    public MinesweeperGame(int width, int height, int bombCount) {
        this.width = width;
        this.height = height;
        this.bombCount = bombCount;
        this.board = new int[height][width];
        this.visibleBoard = new String[height][width];
        this.firstClick = true;
        this.gameOver = false;

        initializeBoards();
    }

    private void initializeBoards() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                board[y][x] = 0;
                visibleBoard[y][x] = "||❔||";
            }
        }
    }

    public void placeBombs(int safeX, int safeY) {
        int bombsPlaced = 0;

        // Ensure safe area around first click
        while (bombsPlaced < bombCount) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            // Don't place bombs near first click
            if (Math.abs(x - safeX) <= 1 && Math.abs(y - safeY) <= 1) {
                continue;
            }

            if (board[y][x] != -1) {
                board[y][x] = -1; // -1 represents a bomb
                bombsPlaced++;

                // Increment numbers around bomb
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dy == 0 && dx == 0) continue;
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height && board[ny][nx] != -1) {
                            board[ny][nx]++;
                        }
                    }
                }
            }
        }
    }

    public boolean reveal(int x, int y) {
        if (firstClick) {
            placeBombs(x, y);
            firstClick = false;
            return revealArea(x, y); // Auto-reveal area around first click
        }

        if (x < 0 || x >= width || y < 0 || y >= height || !visibleBoard[y][x].equals("||❔||")) {
            return false; // Invalid move
        }

        if (board[y][x] == -1) {
            // Hit a bomb
            revealAllBombs();
            gameOver = true;
            return false;
        }

        return revealArea(x, y);
    }

    private boolean revealArea(int x, int y) {
        if (board[y][x] == -1) return false;

        if (board[y][x] > 0) {
            visibleBoard[y][x] = getNumberEmoji(board[y][x]);
            return true;
        }

        // Empty space - reveal recursively
        visibleBoard[y][x] = "⬜";

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dy == 0 && dx == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height &&
                        visibleBoard[ny][nx].equals("||❔||")) {
                    revealArea(nx, ny);
                }
            }
        }
        return true;
    }

    private void revealAllBombs() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (board[y][x] == -1) {
                    visibleBoard[y][x] = "💣";
                }
            }
        }
    }

    private String getNumberEmoji(int num) {
        String[] emojis = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣"};
        return emojis[num - 1];
    }

    public String[][] getVisibleBoard() {
        return visibleBoard;
    }

    public boolean isGameOver() {
        return gameOver;
    }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean hasWon() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // If there's an unopened square that isn't a bomb
                if (visibleBoard[y][x].equals("||❔||") && board[y][x] != -1) {
                    return false;
                }
            }
        }
        return true;
    }
}