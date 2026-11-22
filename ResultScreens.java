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

/** Result screen, accusation resolution and game-over screen. */
public abstract class ResultScreens extends InterrogationScreen {

    JPanel buildResult() {
        Screen p = new Screen(new BorderLayout(0, 12));
        banner = lbl("", serif(Font.BOLD, 34), Color.WHITE); banner.setHorizontalAlignment(SwingConstants.CENTER); banner.setOpaque(true); banner.setBorder(new EmptyBorder(10, 0, 10, 0));
        p.add(banner, BorderLayout.NORTH);
        JPanel mid = new JPanel(new BorderLayout(18, 0)); mid.setOpaque(false);
        resPortrait = new Portrait(Traits.make(true, new Random(3)), true); resPortrait.setPreferredSize(new Dimension(250, 310));
        JPanel pw = new JPanel(new BorderLayout()); pw.setOpaque(false); pw.add(resPortrait, BorderLayout.NORTH); mid.add(pw, BorderLayout.WEST);
        JPanel col = new JPanel(new BorderLayout(0, 12)); col.setOpaque(false);
        Paper q = new Paper(new BorderLayout(), false); resQuote = area(serif(Font.ITALIC, 19), INK); q.add(resQuote, BorderLayout.CENTER); q.setPreferredSize(new Dimension(100, 110));
        Paper truth = new Paper(new BorderLayout(0, 6), false);
        truth.add(lbl("THE TRUTH", sans(Font.BOLD, 13), new Color(140, 90, 40)), BorderLayout.NORTH);
        resTruth = area(serif(Font.PLAIN, 16), INK); truth.add(resTruth, BorderLayout.CENTER);
        resStats = lbl("", sans(Font.BOLD, 14), TEXT);
        Paper nar = new Paper(new BorderLayout(), true); resNarrator = area(serif(Font.ITALIC, 16), TEXT); nar.add(resNarrator, BorderLayout.CENTER); nar.setPreferredSize(new Dimension(100, 90));
        JPanel stack = new JPanel(new BorderLayout(0, 12)); stack.setOpaque(false);
        stack.add(truth, BorderLayout.CENTER); stack.add(nar, BorderLayout.SOUTH);
        col.add(q, BorderLayout.NORTH); col.add(stack, BorderLayout.CENTER); col.add(resStats, BorderLayout.SOUTH);
        mid.add(col, BorderLayout.CENTER); p.add(mid, BorderLayout.CENTER);
        JPanel s = new JPanel(new BorderLayout()); s.setOpaque(false);
        Btn menu = new Btn("Main Menu", BLUE); menu.addActionListener(e -> show("MENU"));
        resNext = new Btn("Next", GOLD); s.add(menu, BorderLayout.WEST); s.add(resNext, BorderLayout.EAST); p.add(s, BorderLayout.SOUTH);
        return p;
    }

    static final String[] WIN_LINES = {
        "Sharp work, detective. That alibi never stood a chance against the facts.",
        "Excellent deduction. You spotted the crack in the story before anyone else did.",
        "Nothing gets past you. The liar's story fell apart exactly where you said it would."};
    static final String[] LOSE_LINES = {
        "Not this time. Look again at who actually clashes with the evidence and the other witnesses.",
        "A tough call, detective, and an innocent person paid for it. Compare the whereabouts more carefully.",
        "Close, but no. Remember: hard evidence never lies, and a liar's alibi always has a weak spot."};

    void resolve(int choice) {
        boolean correct = choice == cur.liar;
        int gained = 0;
        if (correct) {
            gained = 100 + level * 5 + qLeft * 20 + lives * 10; score += gained;
            if (level < 100 && level + 1 > maxUnlocked) maxUnlocked = level + 1; saveProgress(); saveScore();
        } else lives--;
        if (typer != null) typer.stop();

        String lw = cur.sc.where[cur.self[cur.liar]], tw = cur.sc.where[cur.loc[cur.liar]];
        banner.setText(correct ? "CASE SOLVED!" : "WRONG ACCUSATION!"); banner.setBackground(correct ? new Color(52, 140, 82) : new Color(170, 58, 58));
        resPortrait.set(cur.traits[choice], correct ? Mood.WORRIED : Mood.SAD);
        if (correct) resQuote.setText("\u201CAll right, you got me. I said I was " + lw + ", but I was really " + tw + ".\u201D\n\n\u2014 " + cur.names[choice]);
        else resQuote.setText("\u201CI told you the truth! I was " + cur.sc.where[cur.loc[choice]] + ". Why would I lie to you?\u201D\n\n\u2014 " + cur.names[choice]);
        StringBuilder tr = new StringBuilder("The liar was " + cur.names[cur.liar] + ", who claimed to be " + lw + " but was actually " + tw + ".\n\nWhere everyone really was at " + cur.sc.time + ":\n");
        for (int i = 0; i < cur.n; i++) tr.append("\u2022 ").append(cur.first(i)).append(": ").append(cur.sc.where[cur.loc[i]]).append(i == cur.liar ? "   (the liar)" : "").append("\n");
        resTruth.setText(tr.toString());
        resStats.setText(correct ? "\u2605  +" + gained + " points      \u2665  Lives: " + lives + "      Unused questions: " + qLeft
                                 : "\u2665  You lost a life.  Lives left: " + lives + "      Score: " + score);
        resNarrator.setText("Inspector Hale:  " + (correct ? WIN_LINES : LOSE_LINES)[random.nextInt(3)]);
        for (ActionListener al : resNext.getActionListeners()) resNext.removeActionListener(al);
        if (!correct && lives <= 0) { saveScore(); showOver(false); return; }
        if (correct && level >= 100) { resNext.setText("Finish Game  >"); resNext.addActionListener(e -> { saveScore(); showOver(true); }); }
        else if (correct) { resNext.setText("Next Case  >"); resNext.addActionListener(e -> { level++; newCase(); }); }
        else { resNext.setText("Try a New Case  >"); resNext.addActionListener(e -> newCase()); }
        repaintHuds(); show("RESULT");
    }

    JPanel buildGameOver() {
        Screen p = new Screen(new BorderLayout());
        JPanel col = new JPanel(); col.setOpaque(false); col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        overTitle = lbl("", serif(Font.BOLD, 46), GOLD); overDetails = lbl("", sans(Font.PLAIN, 18), TEXT);
        overTitle.setAlignmentX(Component.CENTER_ALIGNMENT); overDetails.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(Box.createVerticalGlue()); col.add(overTitle); col.add(Box.createVerticalStrut(18)); col.add(overDetails); col.add(Box.createVerticalStrut(28));
        Btn lb = new Btn("View Leaderboard", BLUE), home = new Btn("Back to Menu", GREEN);
        for (Btn b : new Btn[]{lb, home}) { b.setAlignmentX(Component.CENTER_ALIGNMENT); b.setMaximumSize(new Dimension(260, 42)); }
        lb.addActionListener(e -> showBoard()); home.addActionListener(e -> show("MENU"));
        col.add(lb); col.add(Box.createVerticalStrut(10)); col.add(home); col.add(Box.createVerticalGlue());
        p.add(col, BorderLayout.CENTER); return p;
    }

    void showOver(boolean won) {
        overTitle.setText(won ? "Master Detective!" : "Out of Lives");
        overDetails.setText("<html><div style='text-align:center'>" + (won ? "You solved all 100 cases. The city is safe.<br><br>" : "The liars got away this time.<br><br>")
            + playerName + "<br>Final score: " + score + "<br>Highest level reached: " + level + "</div></html>");
        show("GAMEOVER");
    }

}
