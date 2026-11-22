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

/** Run flow: start run, new case, quit run. */
public abstract class GameFlow extends ResultScreens {

    void startRun(int lvl) { level = lvl; score = 0; lives = 3; newCase(); }

    void newCase() {
        cur = generate(level, random); qLeft = cur.budget; revealed = new boolean[cur.n][2]; selected = 0;
        fillBriefing(); repaintHuds(); show("BRIEF");
    }

    void quitRun() {
        if (JOptionPane.showConfirmDialog(this, "Quit this run? Your score will be saved to the leaderboard.", "Quit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (score > 0) saveScore(); show("MENU");
        }
    }

}
