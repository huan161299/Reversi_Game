package edu.angelo.midtermprojecttran;

/**
 * @author Huan Tran
 * ID: 81383734
 *
 * All of these codes I did it myself without assistant, with some extra functions that were not taught in class
 *  to improve the game experience. I will listed them here, and will list them again in the code when I used it:
 *
 *     + "https://developer.android.com/guide/topics/resources/drawable-resource" and "https://angrytools.com/android/button/"(the Drawable xml file in the res/drawable that I design myself).
 *     --> I did this because the default graphic of the button is rectangle, which is kinda annoying and not eye-catching at all.
 *
 *     + "https://stackoverflow.com/questions/12523005/how-set-background-drawable-programmatically-in-android"(the function ResourcesCompat.getDrawable()
 *          to get the Drawable xml file imported and draw out on the gameBoard UI).
 *     --> I look up on the internet for this because the default functino setBackgroundColor() we learn in class will keep reset the button to the default shape (rectangle) and only a solid color, which
 *          makes my define button graphic .xml file (in res/drawable) useless. I then switch to setBackground(Drawable file) which solve my problem.
 *
 *     + "https://stackoverflow.com/questions/7919173/android-set-textview-textstyle-programmatically" (Format the text in TextView).
 *
 *     + "https://stackoverflow.com/questions/25837449/setbackgroundcolor-with-hex-color-codes-androidstudio/25837656" (Set the color of an object using HEX color).
 *
 *     + "https://stackoverflow.com/questions/1520887/how-to-pause-sleep-thread-or-process-in-android" (all the possible pause game method).
 *     + "https://abhiandroid.com/ui/countdown-timer" (a good guide on how to use CountDownTimer).
 *     --> Since when the bot make a move and update the UI, I found out that it happen so quick that the Viewer (or the human player) will not be able to catch up or understand what is happenning (if the
 *      bot has already play or not, or it is skipping turn). The solution for this would be make a short pause for the bot when playing, and updating the UI before continue on the game.
 *      The function handler.postDelayed() not only cause memory leak when using normally, but when using on static class (to avoid memory leak), it yield a very complicated solution.
 *      Thread.sleep() function pause the WHOLE game (including the UI), which makes the game kinda buggy. I then turn to use CountDownTimer function.
 *      More details will be explain in function botMode()(operating the AI bot) of how I use this function.
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**
     * Declaring:
     *    + the ReversiLibrary gameBoard for the whole game.
     *    + gameButton[][] that will store the Reversi table buttons in the UI.
     *    + NUM_ROW, NUM_COLUMN: the default final size of the Reversi table. The default size is 6x6.
     *    + botMode: check if the botMode is turning on or off to run the botPlay() function. 0 is off, 1 is on.
     */
    private ReversiLibrary gameBoard;
    private Button[][] gameButton;
    private final int NUM_ROW = 6, NUM_COLUMN = 6;
    private int botMode = 0;

    /**
     * Update the UI:
     *    + the status of the Reversi table and with the graphic buttons from the res/drawable .xml file.
     *    + the total score of the BLACK and WHITE player.
     *    + the current turn of the BLACK and WHITE player.
     *    + the button to reset the game, or switch the playMode (Human mode or BOT mode).
     *
     * The format I use for the TextView object is made possible of the function setTypeface(), which I learn from
     *  StackOverflow "https://stackoverflow.com/questions/7919173/android-set-textview-textstyle-programmatically".
     *
     * To choose a desire color of my option, I use Color.parseColor("HEX COLOR") learning from:
     *  "https://stackoverflow.com/questions/25837449/setbackgroundcolor-with-hex-color-codes-androidstudio/25837656".
     */
    private void updateGameBoard() {

        //Scanning each button on the gameBoard.
        for (int i = 0; i < NUM_ROW; ++i)
            for (int j = 0; j < NUM_COLUMN; ++j) {

                //For each button, get the appropriate button graphic from res/drawable.
                Drawable buttonColor;
                switch (gameBoard.getTableNum(i, j)) {

                    //Empty position with no color pieces.
                    case 0:
                        //If that position is a valid play choice for the current player, draw a oval shape button on the Reversi table.
                        if (gameBoard.checkGameBoard(i, j, false)) {
                            buttonColor = ResourcesCompat.getDrawable(getResources(), R.drawable.possible_button, null);
                            gameButton[i][j].setBackground(buttonColor);
                        }

                        //If that position is not a valid play choice for the current player, draw a empty rectangle.
                        else {
                            buttonColor = ResourcesCompat.getDrawable(getResources(), R.drawable.default_button, null);
                            gameButton[i][j].setBackground(buttonColor);
                        }
                        break;

                    //The position with a BLACK piece.
                    case 1:
                        buttonColor = ResourcesCompat.getDrawable(getResources(), R.drawable.black_button, null);
                        gameButton[i][j].setBackground(buttonColor);
                        break;

                    //The position with a WHITE piece.
                    case 2:
                        buttonColor = ResourcesCompat.getDrawable(getResources(), R.drawable.white_button, null);
                        gameButton[i][j].setBackground(buttonColor);
                        break;
                }

            }

        //Update and write the total BLACK score on the TextView id object.
        TextView blackScore = findViewById(R.id.blackScore);

        //setText
        //since the setText function only accept CharSequence or a predefine "abc" char inside the function, I use
        //CharSequence, which is predefine inside the Java Intellij.
        CharSequence blackScoreText = "Black: " + gameBoard.getPlayerScore(1) + "\0";
        blackScore.setText(blackScoreText);

        //Set size and set TextColor for the text.
        blackScore.setTextSize(20);
        blackScore.setTextColor(Color.BLACK);

        //Update and write the total WHITE score on the TextView id object.
        TextView whiteScore = findViewById(R.id.whiteScore);
        CharSequence whiteScoreText = "White: " + gameBoard.getPlayerScore(2) + "\0";
        whiteScore.setText(whiteScoreText);
        whiteScore.setTextSize(20);
        whiteScore.setTextColor(Color.BLACK);

        //Update and write the current Game Mode on the TextView id object. If botMode == 1 then the player is playing with bot, else
        //  botMode == 0 then the player is playing with another human.
        TextView gameModeStatus = findViewById(R.id.gameModeText);

        //Write the desire text into gameModeText (with BOT or with Human), then export it using setText().
        CharSequence gameModeText;
        if (botMode == 1)
            gameModeText = "Game Mode: vs BOT\0";
        else gameModeText = "Game MODE: vs Human\0";
        gameModeStatus.setText(gameModeText);

        //Set textSize, color and the format ITALIC.
        gameModeStatus.setTextSize(15);
        gameModeStatus.setTextColor(Color.BLACK);
        gameModeStatus.setTypeface(null, Typeface.ITALIC);

        //Set the text for the current game status.
        TextView gameStatus = findViewById(R.id.playerTurn);

        //Define the text format: BOLD.
        gameStatus.setTypeface(null, Typeface.BOLD);
        CharSequence gameStatusText;

        //If the game ends, output the winner onto TextView.
        if (gameBoard.endGame()) {
            //If the score of the BLACK player > WHITE player, output BLACK player win.
            if (gameBoard.getPlayerScore(1) > gameBoard.getPlayerScore(2)) {
                gameStatusText = "Player BLACK win!\0";
            }
            //If the score of the WHITE player > BLACK player, output WHITE player win.
            else if (gameBoard.getPlayerScore(1) < gameBoard.getPlayerScore(2)) {
                gameStatusText = "Player WHITE win!\0";
            }
            //If the score of the BLACK player == WHITE player, output a tie!
            else {
                gameStatusText = "It's a TIE!\0";
            }

            //Set the text to TextView, and change its textSize, textColor and the BackgroundColor using parseColor("HEX Color").
            gameStatus.setText(gameStatusText);
            gameStatus.setTextSize(20);
            gameStatus.setTextColor(Color.parseColor("#ec8232"));
            gameStatus.setBackgroundColor(Color.parseColor("#aa500202"));
        }

        //If the game is not end, write out the current player turn: BLACK is 1, and WHITE is 2.
        else if (gameBoard.getPlayerNo() == 1) {
            //Set the text output, textSize, textColor and the BackgroundColor.
            gameStatusText = "Player turn: BLACK\0";
            gameStatus.setText(gameStatusText);
            gameStatus.setTextSize(20);
            gameStatus.setTextColor(Color.BLACK);
            gameStatus.setBackgroundColor(Color.WHITE);
        }
        else if (gameBoard.getPlayerNo() == 2) {
            //Set the text output, textSize, textColor and the BackgroundColor.
            gameStatusText = "Player turn: WHITE\0";
            gameStatus.setText(gameStatusText);
            gameStatus.setTextSize(20);
            gameStatus.setTextColor(Color.BLACK);
            gameStatus.setBackgroundColor(Color.WHITE);
        }
    }

    /**
     * Control the action of the bot. The bot will only play if: the botMode is enable ( == 1), and the current
     *  turn is WHITE (player indicator number 2).
     *
     * The CountDownTimer function yield useful in this situation: I modify it so it will pause the functions
     *  until a specific milliseconds that it will run those function. I pause the AI bot function and the updateGameBoard() UI function
     *  so that only after 1 seconds that it will run the bot (and also the click function to switch the player, so in the pause time the player cannot click
     *  on the board either), and then update the UI right after.
     */
    private void botPlay() {
        if (botMode == 1 && gameBoard.getPlayerNo() == 2) {

            /**
             * The CountDownTimer function that will execute a function after the end of an interval. I am making this function to pause for 1 second.
             * @param millisInFuture: the total pause time.
             * @param countDownInterval: the interval in between the total pause time.
             */
            new CountDownTimer(1000, 1000) {
                /**
                 * This is the default override function that must be included in order to run the CountDownTimer.
                 * It will execute a desire function in a specific count down interval time(milliseconds).
                 *
                 * Since I am not using this function so I will leave it blank.
                 *
                 * @param millisUntilFinished the count down interval time using milliseconds.
                 */
                @Override
                public void onTick(long millisUntilFinished) {
                    //do nothing.
                }

                /**
                 * Execute the command inside the function onFinish() after the pause time ends (millisInFuture).
                 *
                 * In this case, I make it execute the AI Bot function, and update the UI after 1 second pause.
                 * If after the AI make a play, if in the next turn the AI is allow to play again (which mean that BLACK has no turn to play), recursive this function again.
                 */
                @Override
                public void onFinish() {
                    //Make the bot play.
                    gameBoard.agentSmolWhiteCat();

                    //Update the gameBoard UI.
                    updateGameBoard();

                    //If after the AI play, the current player turn is still WHITE (AI turn), recursive this whole function botPlay().
                    if (gameBoard.getPlayerNo() == 2) botPlay();
                }

            }.start();
        }
    }

    /**
     * On opening the app the first time, this function will initialize the new gameBoard, get the Button[][] array (so we can update the Button graphic in updateGameBoard()),
     *  and update the gameBoard the first time with updateGameBoard().
     * @param savedInstanceState the instance state of the app.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create new gameBoard on the first time the app opens.
        gameBoard = new ReversiLibrary(NUM_ROW, NUM_COLUMN);

        //Initializing the gameButton[][] array.
        gameButton = new Button[][] {
                {findViewById(R.id.button00), findViewById(R.id.button01),findViewById(R.id.button02), findViewById(R.id.button03), findViewById(R.id.button04),findViewById(R.id.button05)},
                {findViewById(R.id.button10), findViewById(R.id.button11),findViewById(R.id.button12), findViewById(R.id.button13), findViewById(R.id.button14),findViewById(R.id.button15)},
                {findViewById(R.id.button20), findViewById(R.id.button21),findViewById(R.id.button22), findViewById(R.id.button23), findViewById(R.id.button24),findViewById(R.id.button25)},
                {findViewById(R.id.button30), findViewById(R.id.button31),findViewById(R.id.button32), findViewById(R.id.button33), findViewById(R.id.button34),findViewById(R.id.button35)},
                {findViewById(R.id.button40), findViewById(R.id.button41),findViewById(R.id.button42), findViewById(R.id.button43), findViewById(R.id.button44),findViewById(R.id.button45)},
                {findViewById(R.id.button50), findViewById(R.id.button51),findViewById(R.id.button52), findViewById(R.id.button53), findViewById(R.id.button54),findViewById(R.id.button55)},
        };

        //Update the game UI.
        updateGameBoard();
    }

    /**
     * Create a new game upon clicking on the New Game button. This will reset the Reversi board, and update the new UI to it.
     *
     * If the bot mode is enable (botMode == 1), play the bot (if the player turn is WHITE first).
     *
     * @param view Get the interaction upon clicking the New Game button (android:onClick) in the UI.
     */
    public void newGame(View view) {
        gameBoard.newGame();
        updateGameBoard();

        //Make the bot play if the bot is enable and the first turn is WHITE.
        botPlay();
    }

    /**
     * Changing the game mode by updating the botMode: play with Human (botMode == 0) and play with BOT (botMode == 1).
     * Create the new game after changing the game mode (using newGame() function from ReversiLibrary) and update the game UI.
     * @param view Get the interaction upon clicking the changeGameModeButton (android:onClick) in the UI.
     */
    public void changeGameModeButton(View view) {
        if (botMode == 0) botMode = 1;
        else botMode = 0;
        gameBoard.newGame();
        updateGameBoard();

        //Make the bot play if the bot is enable.
        botPlay();
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (0,0) (android:onClick) in the UI.
     */
    public void clickButton00(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(0, 0);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (0,1) (android:onClick) in the UI.
     */
    public void clickButton01(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(0, 1);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (0,2) (android:onClick) in the UI.
     */
    public void clickButton02(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(0, 2);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (0,3) (android:onClick) in the UI.
     */
    public void clickButton03(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(0, 3);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (0,4) (android:onClick) in the UI.
     */
    public void clickButton04(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(0, 4);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }

    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (0,5) (android:onClick) in the UI.
     */
    public void clickButton05(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(0, 5);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (1,0) (android:onClick) in the UI.
     */
    public void clickButton10(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(1, 0);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (1,1) (android:onClick) in the UI.
     */
    public void clickButton11(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(1, 1);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (1,2) (android:onClick) in the UI.
     */
    public void clickButton12(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(1, 2);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (1,3) (android:onClick) in the UI.
     */
    public void clickButton13(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(1, 3);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (1,4) (android:onClick) in the UI.
     */
    public void clickButton14(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(1, 4);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (1,5) (android:onClick) in the UI.
     */
    public void clickButton15(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(1, 5);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (2,0) (android:onClick) in the UI.
     */
    public void clickButton20(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(2, 0);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (2,1) (android:onClick) in the UI.
     */
    public void clickButton21(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(2, 1);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (2,2) (android:onClick) in the UI.
     */
    public void clickButton22(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(2, 2);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (2,3) (android:onClick) in the UI.
     */
    public void clickButton23(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(2, 3);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (2,4) (android:onClick) in the UI.
     */
    public void clickButton24(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(2, 4);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (2,5) (android:onClick) in the UI.
     */
    public void clickButton25(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(2, 5);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (3,0) (android:onClick) in the UI.
     */
    public void clickButton30(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(3, 0);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (3,1) (android:onClick) in the UI.
     */
    public void clickButton31(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(3, 1);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (3,2) (android:onClick) in the UI.
     */
    public void clickButton32(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(3, 2);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (3,3) (android:onClick) in the UI.
     */
    public void clickButton33(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(3, 3);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (3,4) (android:onClick) in the UI.
     */
    public void clickButton34(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(3, 4);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (3,5) (android:onClick) in the UI.
     */
    public void clickButton35(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(3, 5);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (4,0) (android:onClick) in the UI.
     */
    public void clickButton40(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(4, 0);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (4,1) (android:onClick) in the UI.
     */
    public void clickButton41(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(4, 1);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (4,2) (android:onClick) in the UI.
     */
    public void clickButton42(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(4, 2);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (4,3) (android:onClick) in the UI.
     */
    public void clickButton43(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(4, 3);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (4,4) (android:onClick) in the UI.
     */
    public void clickButton44(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(4, 4);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (4,5) (android:onClick) in the UI.
     */
    public void clickButton45(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(4, 5);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (5,0) (android:onClick) in the UI.
     */
    public void clickButton50(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(5, 0);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (5,1) (android:onClick) in the UI.
     */
    public void clickButton51(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(5, 1);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (5,2) (android:onClick) in the UI.
     */
    public void clickButton52(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(5, 2);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (5,3) (android:onClick) in the UI.
     */
    public void clickButton53(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(5, 3);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (5,4) (android:onClick) in the UI.
     */
    public void clickButton54(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(5, 4);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }

    /**
     * Register the player's click position on the Reversi board.
     * In BOT mode (botMode == 1), the player can only play if it is the current BLACK player turn.
     *
     * If BOT mode is turning on, make the AI play the game after the current player has played (botPlay()).
     *
     * @param view Get the interaction upon clicking the button position (5,5) (android:onClick) in the UI.
     */
    public void clickButton55(View view) {
        if (botMode == 0 || gameBoard.getPlayerNo() == 1) {
            gameBoard.clickButton(5, 5);

            updateGameBoard();

            //Make the bot play if the bot is enable.
            botPlay();
        }
    }
}