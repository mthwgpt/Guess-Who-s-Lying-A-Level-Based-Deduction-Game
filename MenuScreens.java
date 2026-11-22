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

/** Menu, How-to, Settings, Level select and Leaderboard screens. */
public abstract class MenuScreens extends ProgressStore {

    JPanel buildMenu() {
        Screen p = new Screen(new BorderLayout(0, 14));
        JPanel col = new JPanel(); col.setOpaque(false); col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        JLabel kicker = lbl("A  100-CASE  DETECTIVE  DEDUCTION  GAME", sans(Font.BOLD, 13), MUTED);
        JLabel title = lbl("GUESS WHO'S LYING", serif(Font.BOLD, 54), GOLD);
        JLabel sub = lbl("Every alibi has a crack. Find it before the liar walks free.", serif(Font.ITALIC, 18), TEXT);
        for (JLabel l : new JLabel[]{kicker, title, sub}) l.setAlignmentX(Component.CENTER_ALIGNMENT);
        col.add(Box.createVerticalGlue()); col.add(kicker); col.add(Box.createVerticalStrut(6)); col.add(title); col.add(Box.createVerticalStrut(6)); col.add(sub);
        JPanel faces = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); faces.setOpaque(false);
        Random fr = new Random(2024);
        boolean[] fem = {true, false, true, false, true};
        for (boolean f : fem) { Portrait po = new Portrait(Traits.make(f, fr), false); po.setPreferredSize(new Dimension(130, 160)); faces.add(po); }
        faces.setAlignmentX(Component.CENTER_ALIGNMENT); faces.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        col.add(Box.createVerticalStrut(24)); col.add(faces); col.add(Box.createVerticalStrut(26));
        String[] names = {"Play", "How to Play", "Leaderboard", "Settings", "Exit"};
        Color[] cols = {GREEN, BLUE, BLUE, BLUE, new Color(120, 70, 70)};
        for (int i = 0; i < names.length; i++) {
            Btn b = new Btn(names[i], cols[i]); b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(260, 42)); b.setPreferredSize(new Dimension(260, 42));
            final String nm = names[i];
            b.addActionListener(e -> {
                switch (nm) {
                    case "Play": { String n = JOptionPane.showInputDialog(this, "Enter your detective name:", playerName);
                        if (n == null) return;
                        if (score > 0) saveScore();   // keep the history of the player we are switching away from
                        if (!n.trim().isEmpty()) playerName = n.trim().replace(",", " ");
                        maxUnlocked = levelFor(playerName); score = 0; level = maxUnlocked; saveScore();   // every name gets its own leaderboard row   // a different name is a different player: new names start at level 1
                        saveProgress(); showLevels(); break; }
                    case "How to Play": show("HOWTO"); break;
                    case "Leaderboard": showBoard(); break;
                    case "Settings": show("SETTINGS"); break;
                    default: dispose(); System.exit(0);
                }
            });
            col.add(b); col.add(Box.createVerticalStrut(9));
        }
        col.add(Box.createVerticalGlue());
        p.add(col, BorderLayout.CENTER);
        return p;
    }

    Btn backBtn() { Btn b = new Btn("Back to Menu", BLUE); b.addActionListener(e -> show("MENU")); return b; }

    JPanel buildHowTo() {
        Screen p = new Screen(new BorderLayout(0, 14));
        p.add(lbl("How to Play", serif(Font.BOLD, 34), GOLD), BorderLayout.NORTH);
        Paper card = new Paper(new BorderLayout(), false);
        JTextArea t = area(serif(Font.PLAIN, 17), INK);
        t.setText("THE GOAL\nIn every case exactly one suspect is lying about where they were. Find the liar and accuse them.\n\n"
            + "HOW IT WORKS\n1.  Read the case briefing, the evidence, and each suspect's alibi.\n"
            + "2.  Interrogate suspects. Each question reveals who they saw, and where. You only have a limited number of questions, so choose wisely. Re-reading an answer you already have is free.\n"
            + "3.  Compare everything in your Case File. Truthful suspects never contradict the hard evidence. A liar's alibi will clash with what the evidence or other witnesses say.\n"
            + "4.  Accuse the liar. A correct accusation scores points and unlocks the next case. A wrong one costs a life.\n\n"
            + "TIPS\n- Evidence is always true.\n- If two independent people place a suspect somewhere other than where that suspect claims to be, be suspicious.\n"
            + "- The liar's own sightings may be false too, so don't trust one witness blindly.\n- Unused questions give bonus points.\n\n"
            + "You start every run with 3 lives. Higher levels have more suspects, less evidence and fewer questions. Good luck, detective!");
        card.add(t, BorderLayout.CENTER); p.add(card, BorderLayout.CENTER);
        JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT)); s.setOpaque(false); s.add(backBtn()); p.add(s, BorderLayout.SOUTH);
        return p;
    }

    JPanel buildSettings() {
        Screen p = new Screen(new BorderLayout(0, 14));
        p.add(lbl("Settings", serif(Font.BOLD, 34), GOLD), BorderLayout.NORTH);
        Paper card = new Paper(new GridLayout(0, 1, 0, 14), true);
        JCheckBox tw = new JCheckBox("Typewriter dialogue with talking animation", typewriter);
        tw.setOpaque(false); tw.setForeground(TEXT); tw.setFont(sans(Font.PLAIN, 16)); tw.setFocusPainted(false);
        tw.addActionListener(e -> { typewriter = tw.isSelected(); saveProgress(); });
        Btn reset = new Btn("Reset Progress and Leaderboard", new Color(150, 62, 62));
        reset.setMaximumSize(new Dimension(320, 42));
        reset.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Erase all unlocked levels and the leaderboard?", "Reset", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                maxUnlocked = 1; playerLevels.clear(); leaderboard.clear(); saveProgress(); writeLeaderboard();
            }
        });
        JPanel rr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); rr.setOpaque(false); rr.add(reset);
        card.add(tw); card.add(rr);
        card.add(lbl("Guess Who's Lying  |  Java Swing Edition  |  100 verified, solvable cases", sans(Font.ITALIC, 13), MUTED));
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setOpaque(false); wrap.add(card, BorderLayout.NORTH);
        p.add(wrap, BorderLayout.CENTER);
        JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT)); s.setOpaque(false); s.add(backBtn()); p.add(s, BorderLayout.SOUTH);
        return p;
    }

    void showLevels() {
        if (levelsScreen != null) root.remove(levelsScreen);
        Screen p = new Screen(new BorderLayout(0, 14));
        p.add(lbl("Case Files  -  Select a Level", serif(Font.BOLD, 32), GOLD), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(10, 10, 8, 8)); grid.setOpaque(false);
        for (int i = 1; i <= 100; i++) {
            final int n = i; boolean open = i <= maxUnlocked;
            Btn b = new Btn(String.valueOf(i), i == maxUnlocked ? GOLD : open ? new Color(70, 138, 100) : new Color(48, 58, 74));
            b.setBorder(new EmptyBorder(4, 2, 4, 2)); b.setEnabled(open);
            b.addActionListener(e -> startRun(n));
            grid.add(b);
        }
        p.add(grid, BorderLayout.CENTER);
        JPanel s = new JPanel(new BorderLayout()); s.setOpaque(false); s.add(backBtn(), BorderLayout.WEST);
        s.add(lbl("Solve a case to unlock the next one.   Gold = your frontier.   ", sans(Font.ITALIC, 13), MUTED), BorderLayout.EAST);
        p.add(s, BorderLayout.SOUTH);
        levelsScreen = p; root.add(p, "LEVELS"); show("LEVELS");
    }

    void showBoard() {
        if (boardScreen != null) root.remove(boardScreen);
        Screen p = new Screen(new BorderLayout(0, 14));
        p.add(lbl("Leaderboard  -  All Detectives", serif(Font.BOLD, 32), GOLD), BorderLayout.NORTH);
        String[] cols = {"Rank", "Detective", "Score", "Level Reached"};
        Object[][] rows = new Object[leaderboard.size()][4];
        for (int i = 0; i < rows.length; i++) { PlayerScore s = leaderboard.get(i); rows[i] = new Object[]{i + 1, s.name, s.score, s.level}; }
        JTable table = new JTable(new DefaultTableModel(rows, cols) { public boolean isCellEditable(int r, int c) { return false; } });
        table.setRowHeight(34); table.setFont(sans(Font.PLAIN, 16)); table.setBackground(PAPER); table.setForeground(INK);
        table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0, 0)); table.setFocusable(false);
        table.getTableHeader().setFont(sans(Font.BOLD, 15)); table.getTableHeader().setBackground(PANEL); table.getTableHeader().setForeground(GOLD);
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c); setHorizontalAlignment(CENTER);
                setBackground(r == 0 ? new Color(250, 232, 180) : r % 2 == 0 ? PAPER : PAPER2); setForeground(INK); return this; }
        };
        for (int c = 0; c < 4; c++) table.getColumnModel().getColumn(c).setCellRenderer(cr);
        JScrollPane sp = new JScrollPane(table); sp.getViewport().setBackground(PAPER); sp.setBorder(new LineBorder(alpha(GOLD, 120)));
        if (rows.length == 0) p.add(lbl("No scores yet  -  solve some cases!", serif(Font.ITALIC, 20), MUTED), BorderLayout.CENTER); else p.add(sp, BorderLayout.CENTER);
        JPanel s = new JPanel(new FlowLayout(FlowLayout.LEFT)); s.setOpaque(false); s.add(backBtn()); p.add(s, BorderLayout.SOUTH);
        boardScreen = p; root.add(p, "BOARD"); show("BOARD");
    }

}
