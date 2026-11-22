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

/** Colours, fonts and small colour helpers shared by every screen. */
public final class Theme {
    static final Color BG1 = new Color(24, 32, 46), BG2 = new Color(10, 14, 22);
    static final Color PANEL = new Color(35, 47, 64), PAPER = new Color(245, 238, 223), PAPER2 = new Color(230, 220, 197);
    static final Color INK = new Color(40, 35, 30), GOLD = new Color(216, 178, 94), TEXT = new Color(232, 236, 242);
    static final Color MUTED = new Color(143, 155, 173), RED = new Color(194, 70, 70), GREEN = new Color(82, 168, 110);
    static final Color BLUE = new Color(84, 128, 184);

    static Font sans(int style, int size) { return new Font(Font.SANS_SERIF, style, size); }
    static Font serif(int style, int size) { return new Font(Font.SERIF, style, size); }
    static Color mix(Color a, Color b, double t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t), (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
    static Color shade(Color c, double f) { return f < 0 ? mix(c, Color.BLACK, -f) : mix(c, Color.WHITE, f); }
    static Color alpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }
    static String cap(String s) { return Character.toUpperCase(s.charAt(0)) + s.substring(1); }
}
