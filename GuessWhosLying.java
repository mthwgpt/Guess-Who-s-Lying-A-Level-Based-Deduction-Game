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

/** Entry point. Run: java -cp out guesswholying.GuessWhosLying */
public class GuessWhosLying extends GameFlow {

    public GuessWhosLying() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        loadProgress(); loadLeaderboard();
        root.add(buildMenu(), "MENU");
        root.add(buildHowTo(), "HOWTO");
        root.add(buildSettings(), "SETTINGS");
        root.add(buildBriefing(), "BRIEF");
        root.add(buildInterrogation(), "INTERROGATE");
        root.add(buildResult(), "RESULT");
        root.add(buildGameOver(), "GAMEOVER");
        setContentPane(root);
        setSize(1120, 780); setMinimumSize(new Dimension(1020, 720)); setLocationRelativeTo(null);
        show("MENU");
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--selftest")) { selfTest(); return; }
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new GuessWhosLying().setVisible(true));
    }

    static void selfTest() {
        Random r = new Random(7); int bad = 0;
        for (int lvl = 1; lvl <= 100; lvl++) for (int rep = 0; rep < 20; rep++) {
            Case c = generate(lvl, r); List<Integer> cand = candidates(c);
            if (cand.size() != 1 || cand.get(0) != c.liar) { bad++; System.out.println("PROBLEM at level " + lvl); }
        }
        System.out.println("Checked 100 levels x 20 random cases each. Unsolvable/ambiguous cases: " + bad);
    }
}
