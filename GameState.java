package guesswholying;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;

import static guesswholying.Theme.*;
import static guesswholying.UiKit.*;
import static guesswholying.ScenarioData.*;
import static guesswholying.GameEngine.*;

/** Shared game state, card navigation and the HUD bar. */
public abstract class GameState extends JFrame {

    final CardLayout cards = new CardLayout();
    final JPanel root = new JPanel(cards);
    final Random random = new Random();
    final List<PlayerScore> leaderboard = new ArrayList<>();
    final List<JComponent> huds = new ArrayList<>();
    final Map<String, Integer> playerLevels = new HashMap<>();   // player name -> highest unlocked level
    int maxUnlocked = 1, level = 1, score = 0, lives = 3, qLeft = 0, selected = 0;
    String playerName = "Detective";
    boolean typewriter = true;
    Case cur; boolean[][] revealed;

    JPanel levelsScreen, boardScreen;
    // briefing
    JLabel briefTitle; JTextArea briefStory, briefEvidence; JPanel briefSuspects;
    // interrogation
    Portrait stage; JLabel stageName; JTextArea speech, notes; JPanel tilesRow, qPanel; Btn accuseBtn;
    Timer typer; int typed;
    // result
    JLabel banner, resStats; Portrait resPortrait; JTextArea resQuote, resTruth, resNarrator; Btn resNext;
    // game over
    JLabel overTitle, overDetails;

    GameState() { super("Guess Who's Lying  -  A Detective Deduction Game"); }

    // Flow hooks implemented by the higher layers (they call each other in a cycle).
    abstract void startRun(int lvl);
    abstract void newCase();
    abstract void quitRun();
    abstract void refreshInterrogation();
    abstract void resolve(int choice);
    abstract void saveProgress();
    abstract void saveScore();
    abstract void writeLeaderboard();

    void show(String n) { cards.show(root, n); root.revalidate(); root.repaint(); }
    void repaintHuds() { for (JComponent h : huds) h.repaint(); }

    /** Top status bar: level, score, lives (hearts) and remaining questions. */
    JComponent hud() {
        JComponent h = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), hh = getHeight();
                g2.setColor(PANEL); g2.fillRoundRect(0, 0, w - 1, hh - 1, 16, 16);
                g2.setColor(alpha(GOLD, 110)); g2.drawRoundRect(0, 0, w - 1, hh - 1, 16, 16);
                int by = hh / 2 + 6;
                g2.setFont(sans(Font.BOLD, 16)); g2.setColor(GOLD); g2.drawString("LEVEL " + level + " / 100", 18, by);
                g2.setColor(TEXT); g2.drawString("SCORE  " + score, (int) (w * 0.27), by);
                g2.drawString("LIVES", (int) (w * 0.48), by);
                for (int i = 0; i < 3; i++) heart(g2, w * 0.48 + 66 + i * 26, hh / 2.0, 9, i < lives ? RED : new Color(70, 80, 96));
                g2.setColor(TEXT); g2.drawString("QUESTIONS  " + qLeft + (cur != null ? " / " + cur.budget : ""), (int) (w * 0.74), by);
                g2.dispose();
            }
        };
        h.setPreferredSize(new Dimension(100, 46)); huds.add(h); return h;
    }
}
