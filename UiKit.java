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

/** Small Swing factory helpers (labels, text areas, heart icon). */
public final class UiKit {
    static JLabel lbl(String s, Font f, Color c) { JLabel l = new JLabel(s); l.setFont(f); l.setForeground(c); return l; }
    static JTextArea area(Font f, Color c) {
        JTextArea a = new JTextArea(); a.setEditable(false); a.setLineWrap(true); a.setWrapStyleWord(true); a.setOpaque(false);
        a.setFont(f); a.setForeground(c); a.setFocusable(false); a.setBorder(null); return a;
    }
    static void heart(Graphics2D g, double cx, double cy, double s, Color c) {
        Path2D p = new Path2D.Double(); p.moveTo(cx, cy + s * 0.9);
        p.curveTo(cx - s * 1.4, cy + s * 0.1, cx - s * 0.8, cy - s * 0.9, cx, cy - s * 0.3);
        p.curveTo(cx + s * 0.8, cy - s * 0.9, cx + s * 1.4, cy + s * 0.1, cx, cy + s * 0.9); g.setColor(c); g.fill(p);
    }
}
