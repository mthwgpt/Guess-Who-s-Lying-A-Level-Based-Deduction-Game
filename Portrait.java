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
import static guesswholying.PortraitPainter.*;

/** Swing component that shows an animated portrait. */
class Portrait extends JPanel {
    Traits t; Mood mood = Mood.NEUTRAL; int talk; boolean blink; final boolean animated; Timer timer; int tick, next = 38;
    Portrait(Traits t, boolean animated) { this.t = t; this.animated = animated; setOpaque(false); }
    void set(Traits t, Mood m) { this.t = t; this.mood = m; talk = 0; repaint(); }
    void setMood(Mood m) { mood = m; talk = 0; repaint(); }
    void setTalk(int p) { talk = p; repaint(); }
    @Override public void addNotify() {
        super.addNotify();
        if (animated && timer == null) {
            timer = new Timer(80, e -> { tick++; boolean was = blink;
                if (!blink && tick >= next) { blink = true; tick = 0; next = 28 + (int) (Math.random() * 30); }
                else if (blink && tick >= 2) { blink = false; tick = 0; }
                if (was != blink) repaint(); });
            timer.start();
        }
    }
    @Override public void removeNotify() { if (timer != null) { timer.stop(); timer = null; } super.removeNotify(); }
    @Override protected void paintComponent(Graphics gg) {
        Graphics2D g = (Graphics2D) gg.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        RoundRectangle2D rr = new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 18, 18);
        g.clip(rr);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(w / 2.0, h * 0.38), (float) (Math.max(w, h) * 0.85), new float[]{0f, 1f},
                new Color[]{shade(t.bg, 0.2), shade(t.bg, -0.65)}));
        g.fillRect(0, 0, w, h);
        drawPortrait(g, t, mood, talk, blink, w, h);
        g.setPaint(new GradientPaint(0, h * 0.7f, new Color(0, 0, 0, 0), 0, h, new Color(0, 0, 0, 70))); g.fillRect(0, 0, w, h);
        g.setClip(null); g.setColor(alpha(Color.WHITE, 60)); g.setStroke(new BasicStroke(1.4f)); g.draw(rr);
        g.dispose();
    }
}
