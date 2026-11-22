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

/** Full-window gradient background panel. */
class Screen extends JPanel {
    Screen(LayoutManager lm) { super(lm); setBorder(new EmptyBorder(16, 24, 16, 24)); }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; g2.setPaint(new GradientPaint(0, 0, BG1, 0, getHeight(), BG2)); g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
