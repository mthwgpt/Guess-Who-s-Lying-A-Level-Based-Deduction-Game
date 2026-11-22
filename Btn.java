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

/** Rounded custom button. */
class Btn extends JButton {
    Color base; boolean hover;
    Btn(String t, Color base) {
        super(t); this.base = base;
        setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false); setOpaque(false);
        setFont(sans(Font.BOLD, 14)); setBorder(new EmptyBorder(9, 18, 9, 18)); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = !isEnabled() ? shade(base, -0.45) : hover ? shade(base, 0.14) : base;
        double lum = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
        setForeground(!isEnabled() ? MUTED : lum > 135 ? INK : TEXT);
        g2.setPaint(new GradientPaint(0, 0, shade(c, 0.1), 0, getHeight(), shade(c, -0.12)));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setColor(alpha(Color.WHITE, 50)); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.dispose(); super.paintComponent(g);
    }
}
