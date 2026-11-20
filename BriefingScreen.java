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

/** Case briefing screen. */
public abstract class BriefingScreen extends MenuScreens {

    JPanel buildBriefing() {
        Screen p = new Screen(new BorderLayout(0, 12));
        p.add(hud(), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 12)); body.setOpaque(false);
        JPanel top = new JPanel(new GridLayout(1, 2, 14, 0)); top.setOpaque(false);
        Paper story = new Paper(new BorderLayout(0, 6), false);
        briefTitle = lbl("", serif(Font.BOLD, 26), INK); briefStory = area(serif(Font.PLAIN, 16), INK);
        story.add(briefTitle, BorderLayout.NORTH); story.add(briefStory, BorderLayout.CENTER);
        Paper ev = new Paper(new BorderLayout(0, 6), false);
        ev.add(lbl("EVIDENCE ON FILE", sans(Font.BOLD, 13), new Color(140, 90, 40)), BorderLayout.NORTH);
        briefEvidence = area(serif(Font.PLAIN, 16), INK); ev.add(briefEvidence, BorderLayout.CENTER);
        top.add(story); top.add(ev); top.setPreferredSize(new Dimension(100, 250));
        body.add(top, BorderLayout.NORTH);
        briefSuspects = new JPanel(); briefSuspects.setOpaque(false); body.add(briefSuspects, BorderLayout.CENTER);
        p.add(body, BorderLayout.CENTER);
        JPanel s = new JPanel(new BorderLayout()); s.setOpaque(false);
        Btn quit = new Btn("Quit Run", new Color(120, 70, 70)); quit.addActionListener(e -> quitRun());
        Btn go = new Btn("Begin Interrogation  >", GOLD); go.addActionListener(e -> { selected = 0; refreshInterrogation(); show("INTERROGATE"); });
        s.add(quit, BorderLayout.WEST); s.add(go, BorderLayout.EAST); p.add(s, BorderLayout.SOUTH);
        return p;
    }

    void fillBriefing() {
        briefTitle.setText("<html><span style='font-size:11px;color:#8c5a28'>CASE #" + level + "</span><br>" + cur.sc.title + "</html>");
        briefStory.setText(cur.sc.intro + "\n\nAt " + cur.sc.time + " everyone gave a statement. Find the suspect whose story doesn't hold up.");
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < cur.evS.length; k++) sb.append("\u2022  ").append(cur.evLine(k)).append("\n\n");
        sb.append("Evidence is always true. Use it to test every alibi.");
        briefEvidence.setText(sb.toString());
        briefSuspects.removeAll(); briefSuspects.setLayout(new GridLayout(1, cur.n, 12, 0));
        for (int i = 0; i < cur.n; i++) {
            Paper c = new Paper(new BorderLayout(0, 6), true); c.setBorder(new EmptyBorder(10, 10, 10, 10));
            Portrait po = new Portrait(cur.traits[i], false); po.setPreferredSize(new Dimension(150, 170)); c.add(po, BorderLayout.CENTER);
            JPanel info = new JPanel(new BorderLayout(0, 3)); info.setOpaque(false);
            info.add(lbl(cur.names[i], sans(Font.BOLD, 15), TEXT), BorderLayout.NORTH);
            JPanel mid = new JPanel(new BorderLayout(0, 4)); mid.setOpaque(false);
            mid.add(lbl(cur.roles[i], sans(Font.ITALIC, 12), GOLD), BorderLayout.NORTH);
            JTextArea q = area(serif(Font.ITALIC, 14), TEXT); q.setText("\u201C" + cur.selfLine(i) + "\u201D"); mid.add(q, BorderLayout.CENTER);
            info.add(mid, BorderLayout.CENTER); info.setPreferredSize(new Dimension(100, 130)); c.add(info, BorderLayout.SOUTH);
            briefSuspects.add(c);
        }
        briefSuspects.revalidate(); briefSuspects.repaint();
    }

}
