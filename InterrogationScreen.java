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

/** Interrogation screen: tiles, speech, questions, case file. */
public abstract class InterrogationScreen extends BriefingScreen {

    JPanel buildInterrogation() {
        Screen p = new Screen(new BorderLayout(0, 12));
        p.add(hud(), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(14, 0)); body.setOpaque(false);

        Paper file = new Paper(new BorderLayout(0, 6), false); file.setPreferredSize(new Dimension(320, 100));
        file.add(lbl("CASE FILE", sans(Font.BOLD, 13), new Color(140, 90, 40)), BorderLayout.NORTH);
        notes = area(serif(Font.PLAIN, 14), INK);
        JScrollPane ns = new JScrollPane(notes); ns.setBorder(null); ns.setOpaque(false); ns.getViewport().setOpaque(false);
        file.add(ns, BorderLayout.CENTER); body.add(file, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout(0, 12)); center.setOpaque(false);
        tilesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); tilesRow.setOpaque(false); tilesRow.setPreferredSize(new Dimension(100, 118));
        center.add(tilesRow, BorderLayout.NORTH);

        JPanel stagePane = new JPanel(new BorderLayout(16, 0)); stagePane.setOpaque(false);
        stage = new Portrait(Traits.make(true, new Random(1)), true); stage.setPreferredSize(new Dimension(250, 310));
        JPanel stageWrap = new JPanel(new BorderLayout()); stageWrap.setOpaque(false); stageWrap.add(stage, BorderLayout.NORTH); stagePane.add(stageWrap, BorderLayout.WEST);
        JPanel right = new JPanel(new BorderLayout(0, 10)); right.setOpaque(false);
        stageName = lbl("", serif(Font.BOLD, 24), GOLD); right.add(stageName, BorderLayout.NORTH);
        Paper bubble = new Paper(new BorderLayout(), false); bubble.setPreferredSize(new Dimension(100, 120));
        speech = area(serif(Font.ITALIC, 19), INK); bubble.add(speech, BorderLayout.CENTER);
        JPanel rc = new JPanel(new BorderLayout(0, 10)); rc.setOpaque(false); rc.add(bubble, BorderLayout.NORTH);
        qPanel = new JPanel(new GridLayout(0, 1, 0, 8)); qPanel.setOpaque(false);
        JPanel qWrap = new JPanel(new BorderLayout()); qWrap.setOpaque(false); qWrap.add(qPanel, BorderLayout.NORTH); rc.add(qWrap, BorderLayout.CENTER);
        right.add(rc, BorderLayout.CENTER); stagePane.add(right, BorderLayout.CENTER);
        center.add(stagePane, BorderLayout.CENTER);
        body.add(center, BorderLayout.CENTER);
        p.add(body, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout()); south.setOpaque(false);
        Btn back = new Btn("<  Briefing", BLUE); back.addActionListener(e -> show("BRIEF"));
        accuseBtn = new Btn("Accuse", RED); accuseBtn.setFont(sans(Font.BOLD, 16));
        accuseBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Accuse " + cur.names[selected] + " of lying?\nA wrong accusation costs one life.", "Make your accusation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) resolve(selected);
        });
        south.add(back, BorderLayout.WEST); south.add(accuseBtn, BorderLayout.EAST); p.add(south, BorderLayout.SOUTH);
        return p;
    }

    void say(String text) {
        if (typer != null) typer.stop();
        if (!typewriter) { speech.setText(text); stage.setMood(Mood.NEUTRAL); return; }
        speech.setText(""); typed = 0; stage.setMood(Mood.TALK);
        typer = new Timer(18, e -> {
            typed = Math.min(text.length(), typed + 2); speech.setText(text.substring(0, typed)); stage.setTalk((typed / 2) % 3);
            if (typed >= text.length()) { typer.stop(); stage.setMood(Mood.NEUTRAL); }
        });
        typer.start();
    }

    void refreshInterrogation() {
        tilesRow.removeAll();
        for (int i = 0; i < cur.n; i++) {
            final int idx = i;
            JPanel tile = new JPanel(new BorderLayout(0, 2)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(idx == selected ? alpha(GOLD, 70) : alpha(PANEL, 200)); g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                    g2.setColor(idx == selected ? GOLD : alpha(Color.WHITE, 40)); g2.setStroke(new BasicStroke(idx == selected ? 2.4f : 1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14); g2.dispose();
                }
            };
            tile.setOpaque(false); tile.setBorder(new EmptyBorder(5, 5, 4, 5));
            Portrait po = new Portrait(cur.traits[i], false); po.setPreferredSize(new Dimension(62, 70)); po.setEnabled(false);
            tile.add(po, BorderLayout.CENTER);
            JLabel nm = lbl(cur.first(i), sans(Font.BOLD, 12), TEXT); nm.setHorizontalAlignment(SwingConstants.CENTER); tile.add(nm, BorderLayout.SOUTH);
            tile.setPreferredSize(new Dimension(82, 112)); tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            MouseAdapter ma = new MouseAdapter() { public void mouseClicked(MouseEvent e) { selected = idx; refreshInterrogation(); } };
            tile.addMouseListener(ma); po.addMouseListener(ma);
            tilesRow.add(tile);
        }
        tilesRow.revalidate(); tilesRow.repaint();
        stage.set(cur.traits[selected], Mood.NEUTRAL);
        stageName.setText(cur.names[selected] + "  \u2014  " + cur.roles[selected]);
        accuseBtn.setText("Accuse " + cur.first(selected) + " of Lying");
        say("\u201C" + cur.selfLine(selected) + "\u201D");
        refreshQuestions(); refreshNotes(); repaintHuds();
    }

    static final String[] Q_TEXT = {"Who did you see nearby, and where?", "Did you notice anyone else?"};

    void refreshQuestions() {
        qPanel.removeAll();
        for (int q = 0; q < 2; q++) {
            final int qi = q; boolean done = revealed[selected][q];
            Btn b = new Btn((done ? "\u2713  " : "?  ") + Q_TEXT[q] + (done ? "   (asked, re-read free)" : "   (costs 1)"), done ? new Color(70, 92, 120) : BLUE);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.addActionListener(e -> ask(qi));
            qPanel.add(b);
        }
        qPanel.revalidate(); qPanel.repaint();
    }

    void refreshNotes() {
        StringBuilder sb = new StringBuilder("EVIDENCE (always true)\n");
        for (int k = 0; k < cur.evS.length; k++) sb.append("\u2022 ").append(cur.first(cur.evS[k])).append(": ").append(cur.sc.where[cur.evP[k]]).append("\n");
        sb.append("\nCLAIMED ALIBIS\n");
        for (int i = 0; i < cur.n; i++) sb.append("\u2022 ").append(cur.first(i)).append(": ").append(cur.sc.where[cur.self[i]]).append("\n");
        sb.append("\nSIGHTINGS\n");
        boolean any = false;
        for (int i = 0; i < cur.n; i++) for (int q = 0; q < 2; q++) if (revealed[i][q]) {
            any = true; sb.append("\u2022 ").append(cur.first(i)).append(" saw ").append(cur.first(cur.obsT[i][q])).append(": ").append(cur.sc.where[cur.obsP[i][q]]).append("\n"); }
        if (!any) sb.append("(none yet \u2014 interrogate a suspect)\n");
        notes.setText(sb.toString()); notes.setCaretPosition(0);
    }

    void ask(int q) {
        if (!revealed[selected][q]) {
            if (qLeft <= 0) { JOptionPane.showMessageDialog(this, "You're out of questions. Time to make your accusation, detective!", "No questions left", JOptionPane.INFORMATION_MESSAGE); return; }
            qLeft--; revealed[selected][q] = true;
        }
        say("\u201C" + cur.obsLine(selected, q) + "\u201D");
        refreshQuestions(); refreshNotes(); repaintHuds();
    }

}
