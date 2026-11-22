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

/** Paper / dark card panel. */
class Paper extends JPanel {
    final boolean dark;
    Paper(LayoutManager lm, boolean dark) { super(lm); this.dark = dark; setOpaque(false); setBorder(new EmptyBorder(12, 16, 12, 16)); }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(new Color(0, 0, 0, 70)); g2.fillRoundRect(3, 4, w - 4, h - 4, 18, 18);
        g2.setPaint(dark ? new GradientPaint(0, 0, PANEL, 0, h, shade(PANEL, -0.25)) : new GradientPaint(0, 0, PAPER, 0, h, PAPER2));
        g2.fillRoundRect(0, 0, w - 4, h - 4, 18, 18); g2.dispose();
    }
}
