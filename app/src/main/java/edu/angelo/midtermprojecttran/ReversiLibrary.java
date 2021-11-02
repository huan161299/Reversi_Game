package edu.angelo.midtermprojecttran;

import java.util.Random;

/**
 * @author Huan Tran
 * ID: 81383734
 * Original gameplay I take inspiration and take note from: https://cardgames.io/reversi/.
 */

/**
 * The library for the basic game REVERSI.
 */
public class ReversiLibrary {

    /**
     * NUM_ROW, NUM_COLUMN: define the default row / column of the REVERSI table size from the input.
     *
     * playerNo: Storing the current player number to indicate the turn of the game. BLACK = 1, WHITE = 2.
     * playFirst: deciding who will play first at the beginning of the round. Either 1 (BLACK first) or 2 (WHITE first).
     *
     * reversiTable[][]: storing the game's table data: 0 = empty, 1 = BLACK piece, 2 = WHITE piece.
     */
    private final int NUM_ROW, NUM_COLUMN;
    private int playerNo, playFirst = 0;
    private int[][] reversiTable;

    /**
     * Default constructor to define the default Reversi table row and column with NUM_ROW and NUM_COLUMN, and start a new game.
     * @param NUM_ROW the maximum row of the Reversi table.
     * @param NUM_COLUMN the maximum column of the Reversi table.
     */
    ReversiLibrary(int NUM_ROW, int NUM_COLUMN) {
        //Update the default NUM_ROW and NUM_COLUMN in the ReversiTable with the input parameters.
        this.NUM_ROW = NUM_ROW;
        this.NUM_COLUMN = NUM_COLUMN;

        //Create a new game.
        newGame();
    }

    /**
     * Create a new game by:
     *    + Create a new game table.
     *    + If no defined playFirst (which player BLACK or WHITE to play first), then we will pick which player to play first
     *      using Random: 1 means BLACK goes first, while 2 means WHITE goes first.
     *    + If playFirst already define who to play first in the last round (BLACK / WHITE), it will pick the other player to go first
     *      in the new round (WHITE / BLACK).
     *    + Create the middle 2 BLACK pieces and 2 WHITE pieces for the new game.
     */
    public void newGame() {
        //Create the Reversi table with [NUM_ROW][NUM_COLUMN].
        reversiTable = new int[NUM_ROW][NUM_COLUMN];

        //If the game have not assign who to play first (playFirst = 0), then pick randomly between 1 (BLACK) and 2 (WHITE).
        if (playFirst == 0) {
            Random ran = new Random();

            //Random between 1 and 2;
            playFirst = ran.nextInt(2) + 1;
        }

        //If the game already assign who to play first previously (playFirst != 0), switch to the other player to play first in the new match.
        else {
            if (playFirst == 1) playFirst = 2;
            else playFirst = 1;
        }

        //Assign the first player to go first.
        playerNo = playFirst;

        //Assign the color pieces in the middle of the playBoard.
        reversiTable[2][2] = playerNo;
        reversiTable[3][3] = playerNo;
        reversiTable[2][3] = otherPlayerNum();
        reversiTable[3][2] = otherPlayerNum();

    }

    /**
     * Return the other player tag.
     * @return 1 (BLACK) or 2 (WHITE).
     */
    private int otherPlayerNum() {
        if (playerNo == 1) return 2;
        else return 1;
    }

    /**
     * Check if the current position on the Reversi table is a legit play for the current player by returning true or false. If colorFlip is toggle True, it will
     *  also switch the color that the player win by placing the piece down and block the other opponent's pieces in both side. In Reversi, you are not allow to
     *  make a play where that piece of your did not win any opponent pieces (which will return false). You win the opponent's pieces and score by blocking two sides
     *  of their pieces with your pieces (Straight: up / down / left / right or Diagonally). If there is no valid play for the current player, then it will immediately
     *  switch turn to the other player (getValidPlayChoice()). If both player cannot play, then the game ends.
     *
     * @param row the current Row position on the Reversi table.
     * @param column the current Column position on the Reversi table.
     * @param colorFlip Toggle False: this function will only check if that position is a valid play for the current player. Toggle True: if this position is a valid play for the current player, it will switch the opponent's pieces (that the current player win) to the player's pieces.
     * @return True if the current position is a valid play choice for the current player, False if it is not a valid play for the current player.
     */
    public boolean checkGameBoard(int row, int column, boolean colorFlip) {
        //A boolean variable to return at the function end. True = valid play choice, False = invalid play choice. The default is False.
        boolean validPlay = false;

        //If the current position on Reversi table is already occupied (!= 0) then return False;
        if (reversiTable[row][column] != 0) return false;

        //Check upwards (row - 1, column).
        try {
            //If the upward position of the current position has the opponent piece (otherPlayerNum()) then we will start checking upward.
            if (reversiTable[row - 1][column] == otherPlayerNum()) {
                //Starting at the position up upward and continue on.
                int rowNum = row - 2;

                //If the current upward pieces is not empty (0), then we will keep moving upward.
                while (reversiTable[rowNum][column] != 0) {

                    //Keep moving upward if the current is the opponent's piece.
                    if (reversiTable[rowNum][column] == otherPlayerNum()) {
                        //Continue the loop.
                        --rowNum;
                    }

                    //If the current piece is now the player's piece (we block both sides of the opponent's piece line).
                    else if (reversiTable[rowNum][column] == playerNo) {
                        //Confirm that this is a valid play.
                        validPlay = true;
                        //If not choose to flip the color, then return true (valid play).
                        if (!colorFlip) return true;

                        //If choose to flip the color (colorFlip = true), flip all the color upward from the current position (row, column). (downward direction from (rowNum, column)).
                        while (++rowNum != row) {
                            reversiTable[rowNum][column] = playerNo;
                        }

                        //Break the current while loop to check for the other direction (for color flipping).
                        break;
                    }
                }
            }
        }

        //If the current upward position is out of the Reversi table (out of bound), do nothing.
        catch(Exception ArrayIndexOutOfBoundsException) {}

        //Check leftwards (row, column - 1).
        try {
            //If the leftward position of the current position has the opponent piece (otherPlayerNum()) then we will start checking leftward.
            if (reversiTable[row][column - 1] == otherPlayerNum()) {
                //Starting at the position left leftward and continue on.
                int columnNum = column - 2;

                //If the current upward pieces is not empty (0), then we will keep moving upward.
                while (reversiTable[row][columnNum] != 0) {

                    //Keep moving leftward if the current is the opponent's piece.
                    if (reversiTable[row][columnNum] == otherPlayerNum()) {
                        //Continue the loop.
                        --columnNum;
                    }

                    //If the current piece is now the player's piece (we block both sides of the opponent's piece line).
                    else if (reversiTable[row][columnNum] == playerNo) {
                        //Confirm that this is a valid play.
                        validPlay = true;

                        //If not choose to flip the color, then return true (valid play).
                        if (!colorFlip) return true;

                        //If choose to flip the color (colorFlip = true), flip all the color leftward from the current position (row, column). (rightward direction from (row, columnNum)).
                        while (++columnNum != column) {
                            reversiTable[row][columnNum] = playerNo;
                        }

                        //Break the loop to check for the other direction (for color flipping).
                        break;
                    }
                }
            }
        }

        //If the current upward position is out of the Reversi table (out of bound), do nothing.
        catch(Exception ArrayIndexOutOfBoundsException) {}

        //Check downwards (row + 1, column).
        try {
            //If the downward position of the current position has the opponent piece (otherPlayerNum()) then we will start checking downward.
            if (reversiTable[row + 1][column] == otherPlayerNum()) {
                //Starting at the position down downward and continue on.
                int rowNum = row + 2;

                //If the current downward pieces is not empty (0), then we will keep moving downward.
                while (reversiTable[rowNum][column] != 0) {

                    //Keep moving downward if the current is the opponent's piece.
                    if (reversiTable[rowNum][column] == otherPlayerNum()) {
                        //Continue the loop.
                        ++rowNum;
                    }

                    //If the current piece is now the player's piece (we block both sides of the opponent's piece line).
                    else if (reversiTable[rowNum][column] == playerNo) {
                        //Confirm that this is a valid play.
                        validPlay = true;
                        //If not choose to flip the color, then return true (valid play).
                        if (!colorFlip) return true;

                        //If choose to flip the color (colorFlip = true), flip all the color downward from the current position (row, column). (upward direction from (rowNum, column)).
                        while (--rowNum != row) {
                            reversiTable[rowNum][column] = playerNo;
                        }

                        //Break the loop to check for the other direction (for color flipping).
                        break;
                    }
                }
            }
        }

        //If the current upward position is out of the Reversi table (out of bound), do nothing.
        catch(Exception ArrayIndexOutOfBoundsException) {}

        //Check rightwards (row, column + 1).
        try {
            //If the rightward position of the current position has the opponent piece (otherPlayerNum()) then we will start checking rightward.
            if (reversiTable[row][column + 1] == otherPlayerNum()) {
                //Starting at the position right rightward and continue on.
                int columnNum = column + 2;

                //If the current rightward pieces is not empty (0), then we will keep moving rightward.
                while (reversiTable[row][columnNum] != 0) {

                    //Keep moving rightward if the current is the opponent's piece.
                    if (reversiTable[row][columnNum] == otherPlayerNum()) {
                        //Continue the loop.
                        ++columnNum;
                    }

                    //If the current piece is now the player's piece (we block both sides of the opponent's piece line).
                    else if (reversiTable[row][columnNum] == playerNo) {
                        //Confirm that this is a valid play.
                        validPlay = true;
                        //If not choose to flip the color, then return true (valid play).
                        if (!colorFlip) return true;

                        //If choose to flip the color (colorFlip = true), flip all the color rightward from the current position (row, column). (leftward direction from (row, columnNum)).
                        while (--columnNum != column) {
                            reversiTable[row][columnNum] = playerNo;
                        }

                        //Break the loop to check for the other direction (for color flipping).
                        break;
                    }
                }
            }
        }

        //If the current upward position is out of the Reversi table (out of bound), do nothing.
        catch(Exception ArrayIndexOutOfBoundsException) {}

        //Check the upper-left diagonal line (row - 1, column - 1).
        try {
            //If the rightward position of the current position has the opponent piece (otherPlayerNum()) then we will start checking upper-leftward.
            if (reversiTable[row - 1][column - 1] == otherPlayerNum()) {
                //Starting at the position upper-left upper-leftward and continue on.
                int rowNum = row - 2;
                int columnNum = column - 2;

                //If the current upper-leftward pieces is not empty (0), then we will keep moving upper-leftward.
                while (reversiTable[rowNum][columnNum] != 0) {

                    //Keep moving upper-leftward if the current is the opponent's piece.
                    if (reversiTable[rowNum][columnNum] == otherPlayerNum()) {
                        //Continue the loop.
                        --rowNum;
                        --columnNum;
                    }

                    //If the current piece is now the player's piece (we block both sides of the opponent's piece line).
                    else if (reversiTable[rowNum][columnNum] == playerNo) {
                        //Confirm that this is a valid play.
                        validPlay = true;
                        //If not choose to flip the color, then return true (valid play).
                        if (!colorFlip) return true;

                        //If choose to flip the color (colorFlip = true), flip all the color upper-leftward from the current position (row, column). (lower-rightward direction from (rowNum, columnNum)).
                        while (++rowNum != row && ++columnNum != column) {
                            reversiTable[rowNum][columnNum] = playerNo;
                        }

                        //Break the loop to check for the other direction (for color flipping).
                        break;
                    }
                }
            }
        }

        //If the current upward position is out of the Reversi table (out of bound), do nothing.
        catch(Exception ArrayIndexOutOfBoundsException) {}

        //Check the lower-left diagonal line (row + 1, column - 1).
        try {
            //If the lower-leftward position of the current position has the opponent piece (otherPlayerNum()) then we will start checking lower-leftward.
            if (reversiTable[row + 1][column - 1] == otherPlayerNum()) {
                //Starting at the position lower-left lower-leftward and continue on.
                int rowNum = row + 2;
                int columnNum = column - 2;

                //If the current lower-leftward pieces is not empty (0), then we will keep moving lower-leftward.
                while (reversiTable[rowNum][columnNum] != 0) {
                    if (reversiTable[rowNum][columnNum] == otherPlayerNum()) {
                        //Continue the loop.
                        ++rowNum;
                        --columnNum;
                    }

                    //If the current piece is now the player's piece (we block both sides of the opponent's piece line).
                    else if (reversiTable[rowNum][columnNum] == playerNo) {
                        //Confirm that this is a valid play.
                        validPlay = true;
                        //If not choose to flip the color, then return true (valid play).
                        if (!colorFlip) return true;

                        //If choose to flip the color (colorFlip = true), flip all the color lower-leftward from the current position (row, column). (upper-rightward direction from (rowNum, columnNum)).
                        while (--rowNum != row && ++columnNum != column) {
                            reversiTable[rowNum][columnNum] = playerNo;
                        }

                        //Break the loop to check for the other direction (for color flipping).
                        break;
                    }
                }
            }
        }

        //If the current upward position is out of the Reversi table (out of bound), do nothing.
        catch(Exception ArrayIndexOutOfBoundsException) {}

        //Check the upper-right diagonal line (row - 1, column + 1).
        try {
            //If the upper-rightward position of the current position has the opponent piece (otherPlayerNum()) then we will start checking upper-rightward.
            if (reversiTable[row - 1][column + 1] == otherPlayerNum()) {
                //Starting at the position upper-right upper-rightward and continue on.
                int rowNum = row - 2;
                int columnNum = column + 2;

                //If the current upper-rightward pieces is not empty (0), then we will keep moving upper-rightward.
                while (reversiTable[rowNum][columnNum] != 0) {
                    if (reversiTable[rowNum][columnNum] == otherPlayerNum()) {
                        //Continue the loop.
                        --rowNum;
                        ++columnNum;
                    }

                    //If the current piece is now the player's piece (we block both sides of the opponent's piece line).
                    else if (reversiTable[rowNum][columnNum] == playerNo) {
                        //Confirm that this is a valid play.
                        validPlay = true;
                        //If not choose to flip the color, then return true (valid play).
                        if (!colorFlip) return true;

                        //If choose to flip the color (colorFlip = true), flip all the color upper-rightward from the current position (row, column). (lower-leftward direction from (rowNum, columnNum)).
                        while (++rowNum != row && --columnNum != column) {
                            reversiTable[rowNum][columnNum] = playerNo;
                        }

                        //Break the loop to check for the other direction (for color flipping).
                        break;
                    }
                }
            }
        }

        //If the current upward position is out of the Reversi table (out of bound), do nothing.
        catch(Exception ArrayIndexOutOfBoundsException) {}

        //Check the lower-right diagonal line (row + 1, column + 1).
        try {
            //If the lower-rightward position of the current position has the opponent piece (otherPlayerNum()) then we will start checking lower-rightward.
            if (reversiTable[row + 1][column + 1] == otherPlayerNum()) {
                //Starting at the position lower-right lower-rightward and continue on.
                int rowNum = row + 2;
                int columnNum = column + 2;

                //If the current lower-rightward pieces is not empty (0), then we will keep moving lower-rightward.
                while (reversiTable[rowNum][columnNum] != 0) {
                    if (reversiTable[rowNum][columnNum] == otherPlayerNum()) {
                        ++rowNum;
                        ++columnNum;
                        continue;
                    }

                    //If the current piece is now the player's piece (we block both sides of the opponent's piece line).
                    else if (reversiTable[rowNum][columnNum] == playerNo) {
                        //Confirm that this is a valid play.
                        validPlay = true;
                        //If not choose to flip the color, then return true (valid play).
                        if (!colorFlip) return true;

                        //If choose to flip the color (colorFlip = true), flip all the color lower-rightward from the current position (row, column). (upper-leftward direction from (rowNum, columnNum)).
                        while (--rowNum != row && --columnNum != column) {
                            reversiTable[rowNum][columnNum] = playerNo;
                        }

                        //Break the loop to check for the other direction (for color flipping).
                        break;
                    }
                }
            }
        }

        //If the current upward position is out of the Reversi table (out of bound), do nothing.
        catch(Exception ArrayIndexOutOfBoundsException) {}

        //If it is a confirm valid play from the player, and is allow to flip color then update the current position as the current player piece.
        if (validPlay && colorFlip) reversiTable[row][column] = playerNo;

        //Return true or false from validPlay.
        return validPlay;
    }

    /**
     * Get the number indicating which player is playing at the current turn. BLACK = 1, WHITE = 2.
     * @return return the number indication the current player of the current turn.
     */
    public int getPlayerNo() {return playerNo;}

    //Count how many valid play choices of the current Player.
    private int getValidPlayChoice() {
        int count = 0;
        for (int i = 0; i < NUM_ROW; ++i)
            for (int j = 0; j < NUM_COLUMN; ++j) {
                if (reversiTable[i][j] == 0)
                    if (checkGameBoard(i, j, false)) ++count;
            }

        return count;
    }

    /**
     * Pick the current row / column position from the parameter and if it is a valid position, flip the opposite color pieces around it and switch the player turn,
     *  if it is not a valid position, do nothing.
     * @param row The current row position on the Reversi table.
     * @param column The current column position on the Reversi table.
     */
    public void clickButton(int row, int column) {
        //Check the player's choice, flip the color and update the game board if it is true (colorFlip toggle True).
        if (checkGameBoard(row, column, true))
            updatePlayerTurn();
    }

    /**
     * Switch the current player to the next opposite player. 1 to 2 (BLACK to WHITE) or 2 to 1 (WHITE to BLACK).
     */
    public void updatePlayerTurn() {
        playerNo = otherPlayerNum();
        if (getValidPlayChoice() == 0)
            playerNo = otherPlayerNum();
    }

    /**
     * Count the Reversi table and get the current score of the indicate playerNum from the parameter. (1 is BLACK, 2 is WHITE).
     * @param playerNum The number indicates which player to return the score. (1 is BLACK, 2 is WHITE).
     * @return the total score of the current player (playerNum) counting from the Reversi table.
     */
    public int getPlayerScore(int playerNum) {
        int count = 0;
        for (int i = 0; i < NUM_ROW; ++i)
            for (int j = 0; j < NUM_COLUMN; ++j)

                //For each pieces on the Reversi table == current playerNum, ++count.
                if (reversiTable[i][j] == playerNum)
                    ++count;

        //Return the total count score (color pieces of the current playerNum) from the Reversi table.
        return count;
    }

    /**
     * Get the information at that position of the game board: 0 = Empty, 1 = Black, 2 = White.
     * @param row Current row position on the Reversi table.
     * @param column Current column position on the Reversi table.
     * @return 0 = Empty, 1 = Black piece, 2 = White piece.
     */
    public int getTableNum(int row, int column) {
        return reversiTable[row][column];
    }

    /**
     * Check if the game reach the end. The game will end when:
     *    + All the position on the Reversi table is all fill with the color pieces.
     *    + No more possible valid move for both the player.
     *    --> The function updatePlayerTurn() will first switch to the other opponent after clickButton(row, column). If that current player has
     *          no valid play choice, it will switch back to the previous player. It is here in the endGame that check if the current player can continue to play
     *          or not. If there exist a valid position that is still playable, it will continue the game.
     * @return Return False if the game is still playable, return True if both player cannot play anymore.
     */
    public boolean endGame() {
        for (int i = 0; i < NUM_ROW; ++i)
            for (int j = 0; j < NUM_COLUMN; ++j)
                if (reversiTable[i][j] == 0 && checkGameBoard(i, j, false)) return false;

        return true;
    }

    /**
     * The AI of the Bot mode (I keep it simple since making a hard-core bot will make this game not enjoyable at all, that is why it is a "smol cat").
     * It will search through the whole Reversi table and look for a valid play that yield the highest score, and then play that choice (clickButton(row, column)).
     * This strategy is simple, yet dangerous for those who play without being careful.
     * The default color piece of the AI is WHITE (or indicator number: 2).
     */
    public void agentSmolWhiteCat() {
        final int BLACK = 1, WHITE = 2;

        //Variable that will store the final choice of the row, column, and the maximum score that the AI can get.
        int choiceRow = 0, choiceColumn = 0;
        int maxScore = 0;

        //For each valid position that the AI can play on the table, count the score and add it up to the temporary countScore.
        //The method to scan and check if the current position is valid or not, is very similar to the checkGameBoard() function.
        //Thus I will only explain the first upward check (the rest are the same but at different directions).
        for (int i = 0; i < NUM_ROW; ++i) {
            for (int j = 0; j < NUM_COLUMN; ++j)
                //If that position is empty, start checking around for the total score that the AI can earn if play there.
                if (getTableNum(i, j) == 0) {
                    int countScore = 0;

                    //Check upwards.
                    try {
                        //If the upward piece is BLACK, start checking upward.
                        if (getTableNum(i - 1, j) == BLACK) {
                            int rowNum = i - 1;
                            int tempScore = 0;

                            //For each BLACK piece found, ++tempScore.
                            while (getTableNum(rowNum, j) == BLACK) {
                                ++tempScore;
                                --rowNum;

                                //If it reach a WHITE piece after going upward (--rowNum), add the tempScore to the total possible score countScore of this position.
                                if (getTableNum(rowNum, j) == WHITE) {
                                    countScore += tempScore;

                                    //Break the current while loop to check for the next direction.
                                    break;
                                }
                            }
                        }
                    }
                    //If the upward check goes out of bound, do nothing.
                    catch (Exception ArrayIndexOutOfBoundsException) {}

                    //Check leftwards.
                    try {
                        if (getTableNum(i, j - 1) == BLACK) {
                            int columnNum = j - 1;
                            int tempScore = 0;
                            while (getTableNum(i, columnNum) == BLACK) {
                                ++tempScore;
                                --columnNum;
                                if (getTableNum(i, columnNum) == WHITE) {
                                    countScore += tempScore;
                                    break;
                                }
                            }
                        }
                    } catch (Exception ArrayIndexOutOfBoundsException) {}

                    //Check downwards.
                    try {
                        if (getTableNum(i + 1, j) == BLACK) {
                            int rowNum = i + 1;
                            int tempScore = 0;
                            while (getTableNum(rowNum, j) == BLACK) {
                                ++tempScore;
                                ++rowNum;
                                if (getTableNum(rowNum, j) == WHITE) {
                                    countScore += tempScore;
                                    break;
                                }
                            }
                        }
                    } catch (Exception ArrayIndexOutOfBoundsException) {}

                    //Check rightwards.
                    try {
                        if (getTableNum(i, j + 1) == BLACK) {
                            int columnNum = j + 1;
                            int tempScore = 0;
                            while (getTableNum(i, columnNum) == BLACK) {
                                ++tempScore;
                                ++columnNum;
                                if (getTableNum(i, columnNum) == WHITE) {
                                    countScore += tempScore;
                                    break;
                                }
                            }
                        }
                    } catch (Exception ArrayIndexOutOfBoundsException) {}

                    //Check the upper-left diagonal line.
                    try {
                        if (getTableNum(i - 1, j - 1) == BLACK) {
                            int rowNum = i - 1, columnNum = j - 1;
                            int tempScore = 0;
                            while (getTableNum(rowNum, columnNum) == BLACK) {
                                ++tempScore;
                                --rowNum;
                                --columnNum;
                                if (getTableNum(rowNum, columnNum) == WHITE) {
                                    countScore += tempScore;
                                    break;
                                }
                            }
                        }
                    } catch (Exception ArrayIndexOutOfBoundsException) {}

                    //Check the lower-left diagonal line.
                    try {
                        if (getTableNum(i + 1, j - 1) == BLACK) {
                            int rowNum = i + 1, columnNum = j - 1;
                            int tempScore = 0;
                            while (getTableNum(rowNum, columnNum) == BLACK) {
                                ++tempScore;
                                ++rowNum;
                                --columnNum;
                                if (getTableNum(rowNum, columnNum) == WHITE) {
                                    countScore += tempScore;
                                    break;
                                }
                            }
                        }
                    } catch (Exception ArrayIndexOutOfBoundsException) {}

                    //Check the upper-right diagonal line.
                    try {
                        if (getTableNum(i - 1, j + 1) == BLACK) {
                            int rowNum = i - 1, columnNum = j + 1;
                            int tempScore = 0;
                            while (getTableNum(rowNum, columnNum) == BLACK) {
                                ++tempScore;
                                --rowNum;
                                ++columnNum;
                                if (getTableNum(rowNum, columnNum) == WHITE) {
                                    countScore += tempScore;
                                    break;
                                }
                            }
                        }
                    } catch (Exception ArrayIndexOutOfBoundsException) {}

                    //Check the lower-right diagonal line.
                    try {
                        if (getTableNum(i + 1, j + 1) == BLACK) {
                            int rowNum = i + 1, columnNum = j + 1;
                            int tempScore = 0;
                            while (getTableNum(rowNum, columnNum) == BLACK) {
                                ++tempScore;
                                ++rowNum;
                                ++columnNum;
                                if (getTableNum(rowNum, columnNum) == WHITE) {
                                    countScore += tempScore;
                                    break;
                                }
                            }
                        }
                    } catch (Exception ArrayIndexOutOfBoundsException) {}

                    //After check all the directions, check if the current play position yield the highest possible earning score for the AI.
                    //Record it into choiceRow, choiceColumn, and maxScore.
                    if (maxScore < countScore) {
                        maxScore = countScore;
                        choiceRow = i;
                        choiceColumn = j;
                    }
                }
        }

        //With the above method, the AI will always find a valid play position (not the default row = 0, column = 0). If there is no valid play for the AI, the game itself will switch player (in the updatePlayerTurn() function).
        clickButton(choiceRow, choiceColumn);
    }

    /**
     * The default output format for the Reversi Table (testing purpose).
     * @return The whole number table of the Reversi table.
     */
    public String toString() {
        String outPut = "";
        for (int i = 0; i < NUM_ROW; ++i) {
            for (int j = 0; j < NUM_COLUMN; ++j) {
                outPut += reversiTable[i][j];
            }
            outPut += '\n';
        }

        return outPut;
    }

    public static void main(String[] args) {

        ReversiLibrary gameBoard = new ReversiLibrary(6, 6);

        System.out.println("New game board:");
        System.out.print(gameBoard);
        System.out.println("--------------");


        //Choose to play at a invalid position.
        System.out.println("Player turn: " + gameBoard.getPlayerNo());
        System.out.println("Current valid plays: " + gameBoard.getValidPlayChoice());
        gameBoard.clickButton(0, 0);
        System.out.print(gameBoard);
        System.out.println("--------------");

        //Choose to play at a valid position.
        System.out.println("Player turn: " + gameBoard.getPlayerNo());
        System.out.println("Current valid plays: " + gameBoard.getValidPlayChoice());
        gameBoard.clickButton(4, 2);
        System.out.print(gameBoard);
        System.out.println("--------------");

        //Choose to play at a valid position.
        System.out.println("Player turn: " + gameBoard.getPlayerNo());
        System.out.println("Current valid plays: " + gameBoard.getValidPlayChoice());
        gameBoard.clickButton(2, 1);
        System.out.print(gameBoard);
        System.out.println("--------------");

        //Choose to play at a invalid position.
        System.out.println("Player turn: " + gameBoard.getPlayerNo());
        System.out.println("Current valid plays: " + gameBoard.getValidPlayChoice());
        gameBoard.clickButton(3, 2);
        System.out.print(gameBoard);
        System.out.println("--------------");

        //Choose to play at a valid position.
        System.out.println("Player turn: " + gameBoard.getPlayerNo());
        System.out.println("Current valid plays: " + gameBoard.getValidPlayChoice());
        gameBoard.clickButton(1, 0);
        System.out.print(gameBoard);
        System.out.println("--------------");

        //Player Score:
        System.out.println("Player 1 score: " + gameBoard.getPlayerScore(1));
        System.out.println("Player 2 score: " + gameBoard.getPlayerScore(2));
    }
}