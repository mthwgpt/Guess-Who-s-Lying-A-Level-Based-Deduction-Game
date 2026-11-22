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

/** Java2D portrait drawing (hair, eyes, face, clothes). */
public final class PortraitPainter {
    static void paintHair(Graphics2D g, Shape sh, Color hair, Random rnd, double y0, double y1) {
        if (sh == null) return;
        g.setPaint(new GradientPaint(0, (float) y0, shade(hair, 0.24), 0, (float) y1, shade(hair, -0.22)));
        g.fill(sh);
        Shape old = g.getClip(); g.clip(sh);
        g.setStroke(new BasicStroke(0.9f));
        Rectangle2D b = sh.getBounds2D();
        for (int i = 0; i < 60; i++) {
            double x = b.getX() + rnd.nextDouble() * b.getWidth(), y = b.getY() + rnd.nextDouble() * b.getHeight() * 0.8;
            g.setColor(i % 2 == 0 ? alpha(shade(hair, 0.4), 80) : alpha(shade(hair, -0.55), 80));
            g.draw(new QuadCurve2D.Double(x, y, x + (rnd.nextDouble() - 0.5) * 14, y + 12, x + (rnd.nextDouble() - 0.5) * 16, y + 26 + rnd.nextDouble() * 16));
        }
        g.setClip(old);
        g.setColor(alpha(Color.BLACK, 70)); g.setStroke(new BasicStroke(1f)); g.draw(sh);
    }

    static void eye(Graphics2D g, double cx, double cy, boolean left, Traits t, boolean blink, Mood m) {
        // soft socket shadow
        g.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), 19, new float[]{0f, 1f}, new Color[]{new Color(60, 30, 20, 36), new Color(60, 30, 20, 0)}));
        g.fill(new Ellipse2D.Double(cx - 19, cy - 15, 38, 30));
        Path2D almond = new Path2D.Double();
        almond.moveTo(cx - 11.5, cy + 0.5); almond.quadTo(cx - 1, cy - 10, cx + 11.5, cy - 0.5); almond.quadTo(cx, cy + 7, cx - 11.5, cy + 0.5);
        if (blink) {
            g.setColor(shade(t.skin, -0.12)); g.fill(almond);
            g.setColor(alpha(new Color(30, 20, 16), 220)); g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new QuadCurve2D.Double(cx - 11.5, cy + 0.5, cx, cy + 5, cx + 11.5, cy - 0.5));
            return;
        }
        Shape old = g.getClip(); g.clip(almond);
        g.setPaint(new GradientPaint(0, (float) (cy - 8), new Color(190, 188, 184), 0, (float) (cy + 6), new Color(250, 249, 246)));
        g.fill(almond.getBounds2D());
        double look = m == Mood.SUSPICIOUS ? (left ? 3 : -3) * -1 : 0;
        if (m == Mood.SUSPICIOUS) look = 3.2;
        double ix = cx + look, iy = cy - 0.4;
        g.setPaint(new RadialGradientPaint(new Point2D.Double(ix, iy), 6.4f, new float[]{0f, 0.6f, 1f},
                new Color[]{shade(t.eye, 0.35), t.eye, shade(t.eye, -0.6)}));
        g.fill(new Ellipse2D.Double(ix - 6.2, iy - 6.2, 12.4, 12.4));
        g.setColor(new Color(10, 8, 8)); g.fill(new Ellipse2D.Double(ix - 2.8, iy - 2.8, 5.6, 5.6));
        g.setColor(new Color(255, 255, 255, 230)); g.fill(new Ellipse2D.Double(ix + 1.2, iy - 3.6, 2.8, 2.8));
        g.setColor(new Color(255, 255, 255, 110)); g.fill(new Ellipse2D.Double(ix - 3.2, iy + 1.2, 1.8, 1.8));
        g.setPaint(new GradientPaint(0, (float) (cy - 8), new Color(60, 30, 20, 110), 0, (float) (cy - 1), new Color(60, 30, 20, 0)));
        g.fill(almond.getBounds2D());
        g.setClip(old);
        g.setStroke(new BasicStroke(t.female ? 2.2f : 1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(34, 24, 20));
        g.draw(new QuadCurve2D.Double(cx - 11.5, cy + 0.5, cx - 1, cy - 10, cx + 11.5, cy - 0.5));
        if (t.female) { double ox = left ? -11.5 : 11.5; g.setStroke(new BasicStroke(1.3f)); g.draw(new Line2D.Double(cx + ox, cy - 0.5, cx + ox * 1.25, cy - 3)); }
        g.setStroke(new BasicStroke(1f)); g.setColor(alpha(shade(t.skin, -0.3), 120));
        g.draw(new QuadCurve2D.Double(cx - 10, cy + 3.2, cx, cy + 7.5, cx + 10, cy + 2.8));
        g.setColor(alpha(shade(t.skin, -0.35), 90));
        g.draw(new QuadCurve2D.Double(cx - 11, cy - 5.5, cx, cy - 12, cx + 11, cy - 5));
    }

    static void drawPortrait(Graphics2D g0, Traits t, Mood mood, int talk, boolean blink, int W, int H) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        double s = Math.min(W / 200.0, H / 236.0);
        g.translate((W - 200 * s) / 2.0, H - 236 * s); g.scale(s, s);
        Random rnd = new Random(t.seed);
        Color skin = t.skin, skinD = shade(skin, -0.26), skinL = shade(skin, 0.28);
        int st = t.hairStyle;

        // ---- torso (raised slightly so the neck is not too long)
        g.translate(0, -14);
        Path2D body = new Path2D.Double();
        body.moveTo(0, 300); body.lineTo(4, 238); body.curveTo(10, 206, 56, 194, 84, 184); body.lineTo(116, 184);
        body.curveTo(144, 194, 190, 206, 196, 238); body.lineTo(200, 300); body.closePath();
        g.setPaint(new GradientPaint(0, 190, shade(t.cloth, 0.14), 0, 260, shade(t.cloth, -0.35))); g.fill(body);
        g.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 90), 60, 0, new Color(0, 0, 0, 0))); g.fill(body);
        g.setPaint(new GradientPaint(200, 0, new Color(0, 0, 0, 90), 140, 0, new Color(0, 0, 0, 0))); g.fill(body);

        // ---- back hair
        Shape back = null;
        if (st == 2) { Path2D p = new Path2D.Double(); p.moveTo(48, 100); p.curveTo(36, 36, 70, 18, 100, 18); p.curveTo(130, 18, 164, 36, 152, 100);
            p.curveTo(160, 150, 162, 200, 152, 238); p.quadTo(100, 252, 48, 238); p.curveTo(38, 200, 40, 150, 48, 100); back = p; }
        else if (st == 6) { Path2D p = new Path2D.Double(); p.moveTo(50, 100); p.curveTo(38, 38, 70, 18, 100, 18); p.curveTo(130, 18, 162, 38, 150, 100);
            p.curveTo(158, 130, 154, 152, 148, 166); p.quadTo(100, 176, 52, 166); p.curveTo(46, 152, 42, 130, 50, 100); back = p; }
        else if (st == 3) back = new Ellipse2D.Double(80, 8, 40, 38);
        paintHair(g, back, t.hair, rnd, 20, 200);

        // ---- neck + collar
        Path2D neck = new Path2D.Double();
        neck.moveTo(83, 140); neck.lineTo(83, 188); neck.curveTo(92, 204, 108, 204, 117, 188); neck.lineTo(117, 140); neck.closePath();
        g.setPaint(new LinearGradientPaint(83, 0, 117, 0, new float[]{0f, 0.5f, 1f}, new Color[]{skinD, skin, skinD})); g.fill(neck);
        Shape oc = g.getClip(); g.clip(neck);
        g.setPaint(new GradientPaint(0, 150, new Color(40, 15, 10, 120), 0, 178, new Color(40, 15, 10, 0))); g.fill(new Rectangle2D.Double(80, 140, 40, 50));
        g.setClip(oc);
        if (t.female) {
            Path2D v = new Path2D.Double(); v.moveTo(82, 186); v.lineTo(100, 222); v.lineTo(118, 186); v.curveTo(108, 204, 92, 204, 82, 186);
            g.setPaint(new LinearGradientPaint(82, 0, 118, 0, new float[]{0f, 0.5f, 1f}, new Color[]{skinD, skin, skinD})); g.fill(v);
            g.setColor(new Color(214, 180, 96)); g.setStroke(new BasicStroke(1.4f)); g.draw(new QuadCurve2D.Double(86, 192, 100, 224, 114, 192));
            g.setColor(new Color(235, 210, 130)); g.fill(new Ellipse2D.Double(98, 211, 4, 4));
        } else {
            Path2D shirt = new Path2D.Double(); shirt.moveTo(82, 184); shirt.lineTo(100, 230); shirt.lineTo(118, 184); shirt.curveTo(108, 202, 92, 202, 82, 184);
            g.setColor(new Color(236, 236, 240)); g.fill(shirt);
            Path2D lw = new Path2D.Double(); lw.moveTo(78, 182); lw.lineTo(100, 208); lw.lineTo(84, 226); lw.lineTo(66, 198); lw.closePath();
            Path2D rw = new Path2D.Double(); rw.moveTo(122, 182); rw.lineTo(100, 208); rw.lineTo(116, 226); rw.lineTo(134, 198); rw.closePath();
            g.setPaint(new GradientPaint(0, 182, new Color(250, 250, 252), 0, 226, new Color(206, 208, 216))); g.fill(lw); g.fill(rw);
            g.setColor(new Color(0, 0, 0, 50)); g.setStroke(new BasicStroke(0.8f)); g.draw(lw); g.draw(rw);
            if (t.tie) {
                Color tc = shade(t.cloth, -0.1).equals(t.cloth) ? RED : new Color(150, 40, 48);
                Path2D knot = new Path2D.Double(); knot.moveTo(94, 206); knot.lineTo(106, 206); knot.lineTo(104, 216); knot.lineTo(96, 216); knot.closePath();
                Path2D tl = new Path2D.Double(); tl.moveTo(96, 216); tl.lineTo(104, 216); tl.lineTo(110, 270); tl.lineTo(100, 280); tl.lineTo(90, 270); tl.closePath();
                g.setPaint(new GradientPaint(90, 0, shade(tc, 0.2), 110, 0, shade(tc, -0.3))); g.fill(knot); g.fill(tl);
            }
        }

        g.translate(0, 14);

        // ---- ears
        for (int side = 0; side < 2; side++) {
            double ex = side == 0 ? 53 : 134;
            g.setPaint(new GradientPaint(0, 85, skin, 0, 120, skinD)); g.fill(new Ellipse2D.Double(ex, 88, 13, 26));
            g.setColor(alpha(skinD, 150)); g.fill(new Ellipse2D.Double(ex + (side == 0 ? 3 : 4), 94, 6, 14));
        }

        // ---- head
        Path2D head = new Path2D.Double();
        head.moveTo(100, 40); head.curveTo(143, 40, 150, 94, 141, 124); head.curveTo(133, 152, 116, 168, 100, 168);
        head.curveTo(84, 168, 67, 152, 59, 124); head.curveTo(50, 94, 57, 40, 100, 40);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(92, 90), 96, new float[]{0f, 0.65f, 1f}, new Color[]{skinL, skin, skinD}));
        g.fill(head);
        Shape oh = g.getClip(); g.clip(head);
        g.setPaint(new GradientPaint(0, 138, new Color(80, 30, 20, 0), 0, 170, new Color(80, 30, 20, 60))); g.fill(new Rectangle2D.Double(40, 130, 130, 50));
        g.setPaint(new RadialGradientPaint(new Point2D.Double(100, 62), 34, new float[]{0f, 1f}, new Color[]{new Color(255, 255, 255, 46), new Color(255, 255, 255, 0)}));
        g.fill(new Rectangle2D.Double(60, 40, 80, 50));
        double blush = t.female ? 62 : 36;
        for (int sd = 0; sd < 2; sd++) {
            double bx = sd == 0 ? 74 : 126;
            g.setPaint(new RadialGradientPaint(new Point2D.Double(bx, 128), 17, new float[]{0f, 1f}, new Color[]{new Color(222, 100, 100, (int) blush), new Color(222, 100, 100, 0)}));
            g.fill(new Ellipse2D.Double(bx - 17, 111, 34, 34));
        }
        g.setClip(oh);
        g.setColor(alpha(skinD, 130)); g.setStroke(new BasicStroke(1f)); g.draw(head);

        // age lines
        g.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (t.age >= 1) { g.setColor(alpha(skinD, 90));
            g.draw(new QuadCurve2D.Double(83, 121, 78, 132, 82, 144)); g.draw(new QuadCurve2D.Double(117, 121, 122, 132, 118, 144)); }
        if (t.age == 2) { g.setColor(alpha(skinD, 80));
            for (int i = 0; i < 3; i++) g.draw(new QuadCurve2D.Double(76, 66 + i * 5, 100, 63 + i * 5, 124, 66 + i * 5));
            g.draw(new Line2D.Double(60, 100, 66, 103)); g.draw(new Line2D.Double(140, 100, 134, 103)); }

        // ---- eyes + brows
        eye(g, 79, 99, true, t, blink, mood); eye(g, 121, 99, false, t, blink, mood);
        double inL = 0, inR = 0, outL = 0, outR = 0;
        switch (mood) {
            case WORRIED: case SAD: inL = inR = -6; outL = outR = 2; break;
            case SUSPICIOUS: inL = 3; inR = -5; outR = -3; break;
            case TALK: inL = inR = -1; break;
            case SMILE: inL = inR = -1.5; break;
            default: break;
        }
        Color bc = shade(t.hair, t.age == 2 ? 0 : -0.2);
        g.setColor(bc); g.setStroke(new BasicStroke(t.female ? 2.6f : 4.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new QuadCurve2D.Double(65, 86 + outL, 77, 77 + (inL + outL) / 2, 93, 84 + inL));
        g.draw(new QuadCurve2D.Double(135, 86 + outR, 123, 77 + (inR + outR) / 2, 107, 84 + inR));
        g.setStroke(new BasicStroke(1f));

        // ---- nose
        g.setColor(alpha(skinD, 90)); g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new CubicCurve2D.Double(105, 98, 109, 110, 110, 119, 107, 127));
        g.draw(new QuadCurve2D.Double(91, 129, 94, 134, 100, 133)); g.draw(new QuadCurve2D.Double(109, 129, 106, 134, 100, 133));
        g.setPaint(new RadialGradientPaint(new Point2D.Double(100, 124), 9, new float[]{0f, 1f}, new Color[]{alpha(skinL, 150), alpha(skinL, 0)}));
        g.fill(new Ellipse2D.Double(91, 115, 18, 18));
        g.setColor(new Color(60, 25, 20, 130)); g.fill(new Ellipse2D.Double(93, 129, 5, 3)); g.fill(new Ellipse2D.Double(102, 129, 5, 3));

        // ---- beard / mustache
        if (t.beard == 1) {
            Shape o2 = g.getClip(); g.clip(head);
            g.setPaint(new GradientPaint(0, 118, alpha(t.hair, 0), 0, 160, alpha(t.hair, 90))); g.fill(new Rectangle2D.Double(50, 112, 100, 70));
            g.setClip(o2);
        } else if (t.beard == 2) {
            Path2D bd = new Path2D.Double(); bd.moveTo(58, 104); bd.curveTo(58, 172, 142, 172, 142, 104); bd.curveTo(138, 126, 128, 130, 116, 132);
            bd.quadTo(100, 128, 84, 132); bd.curveTo(72, 130, 62, 126, 58, 104);
            paintHair(g, bd, t.hair, rnd, 110, 175);
        }
        if (t.beard >= 1) {
            Path2D mu = new Path2D.Double(); mu.moveTo(82, 142); mu.quadTo(91, 130, 100, 135); mu.quadTo(109, 130, 118, 142); mu.quadTo(109, 139, 100, 140); mu.quadTo(91, 139, 82, 142);
            g.setColor(shade(t.hair, -0.1)); g.fill(mu);
        }

        // ---- mouth
        Color lip = mix(skin, t.female ? new Color(168, 54, 70) : new Color(160, 80, 76), t.female ? 0.62 : 0.42);
        double my = 147, lx = 86, rx = 114, ly = my, ry = my, curve = 0;
        switch (mood) {
            case SMILE: ly = ry = my - 3; curve = 8; break;
            case SAD: case WORRIED: ly = ry = my + 3; curve = -7; break;
            case SUSPICIOUS: ly = my + 2; ry = my - 3; curve = 1; break;
            default: curve = 1.5; break;
        }
        if (mood == Mood.TALK && talk > 0) {
            double hgt = talk == 1 ? 4.5 : 9;
            g.setColor(new Color(70, 20, 26)); g.fill(new Ellipse2D.Double(88, my - hgt / 2 + 1, 24, hgt));
            g.setColor(new Color(245, 244, 238)); g.fill(new Rectangle2D.Double(91, my - hgt / 2 + 1.4, 18, Math.min(2.4, hgt / 2)));
            g.setColor(lip); g.setStroke(new BasicStroke(3f)); g.draw(new Ellipse2D.Double(87, my - hgt / 2, 26, hgt + 2));
        } else {
            Path2D up = new Path2D.Double(); up.moveTo(lx, ly); up.quadTo(100, my + curve - 9, rx, ry); up.quadTo(100, my + curve + 1, lx, ly);
            Path2D lo = new Path2D.Double(); lo.moveTo(lx, ly); lo.quadTo(100, my + curve + 1, rx, ry); lo.quadTo(100, my + curve + 14, lx, ly);
            g.setPaint(new GradientPaint(0, (float) my - 6, shade(lip, -0.18), 0, (float) my, lip)); g.fill(up);
            g.setPaint(new GradientPaint(0, (float) my, shade(lip, 0.1), 0, (float) my + 12, shade(lip, 0.3))); g.fill(lo);
            g.setColor(new Color(60, 20, 20, 170)); g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new QuadCurve2D.Double(lx, ly, 100, my + curve, rx, ry));
            g.setColor(new Color(255, 255, 255, 70)); g.fill(new Ellipse2D.Double(95, my + curve + 4, 9, 2.4));
        }
        g.setColor(alpha(skinD, 70)); g.setStroke(new BasicStroke(1f));
        g.draw(new QuadCurve2D.Double(92, 156, 100, 159, 108, 156));

        // ---- glasses
        if (t.glasses) {
            RoundRectangle2D l = new RoundRectangle2D.Double(59, 88, 40, 27, 11, 11), r = new RoundRectangle2D.Double(101, 88, 40, 27, 11, 11);
            g.setColor(new Color(170, 205, 230, 40)); g.fill(l); g.fill(r);
            g.setColor(new Color(255, 255, 255, 80)); g.setStroke(new BasicStroke(2f));
            g.draw(new Line2D.Double(66, 112, 76, 92)); g.draw(new Line2D.Double(108, 112, 118, 92));
            g.setColor(new Color(36, 34, 36)); g.setStroke(new BasicStroke(2.4f));
            g.draw(l); g.draw(r); g.draw(new QuadCurve2D.Double(99, 98, 100, 94, 101, 98));
            g.draw(new Line2D.Double(59, 95, 52, 92)); g.draw(new Line2D.Double(141, 95, 148, 92));
        }

        // ---- front hair
        Shape front = null; Shape extra = null;
        switch (st) {
            case 0: { Path2D p = new Path2D.Double(); p.moveTo(55, 98); p.curveTo(46, 50, 68, 26, 100, 26); p.curveTo(132, 26, 154, 50, 145, 98);
                p.curveTo(142, 80, 137, 66, 128, 60); p.curveTo(112, 52, 88, 52, 72, 60); p.curveTo(63, 66, 58, 80, 55, 98); front = p; break; }
            case 1: { Path2D p = new Path2D.Double(); p.moveTo(54, 102); p.curveTo(42, 50, 76, 22, 108, 24); p.curveTo(142, 28, 154, 58, 146, 102);
                p.curveTo(143, 84, 138, 68, 126, 60); p.curveTo(116, 56, 96, 56, 80, 70); p.curveTo(70, 78, 60, 86, 54, 102); front = p; break; }
            case 2: case 3: { Path2D p = new Path2D.Double(); p.moveTo(55, 102); p.curveTo(44, 48, 74, 24, 100, 24); p.curveTo(126, 24, 156, 48, 145, 102);
                p.curveTo(142, 82, 130, 64, 100, 53); p.curveTo(70, 64, 58, 82, 55, 102); front = p;
                if (st == 2) { Path2D lk = new Path2D.Double(); lk.moveTo(56, 92); lk.curveTo(44, 140, 46, 196, 60, 230); lk.curveTo(70, 200, 68, 150, 72, 104); lk.closePath();
                    lk.moveTo(144, 92); lk.curveTo(156, 140, 154, 196, 140, 230); lk.curveTo(130, 200, 132, 150, 128, 104); lk.closePath(); extra = lk; } break; }
            case 4: { Area a = new Area(); Random cr = new Random(t.seed ^ 77);
                for (int i = 0; i < 26; i++) { double ang = Math.PI * (1.02 + 0.96 * i / 25.0), rad = 46 + cr.nextDouble() * 6, rr = 12 + cr.nextDouble() * 5;
                    a.add(new Area(new Ellipse2D.Double(100 + Math.cos(ang) * rad - rr, 80 + Math.sin(ang) * rad - rr, rr * 2, rr * 2))); }
                for (int i = 0; i < 6; i++) { double yy = 82 + i * 6; a.add(new Area(new Ellipse2D.Double(50 - cr.nextInt(4), yy - 8, 15, 15))); a.add(new Area(new Ellipse2D.Double(135 + cr.nextInt(4), yy - 8, 15, 15))); }
                a.add(new Area(new Ellipse2D.Double(66, 38, 68, 22)));
                front = a; break; }
            case 6: { Path2D p = new Path2D.Double(); p.moveTo(52, 104); p.curveTo(42, 50, 76, 24, 104, 24); p.curveTo(138, 26, 154, 56, 148, 104);
                p.curveTo(146, 84, 138, 68, 120, 62); p.curveTo(100, 58, 74, 66, 60, 88); p.curveTo(56, 94, 54, 98, 52, 104); front = p;
                Path2D sd = new Path2D.Double(); sd.moveTo(52, 100); sd.curveTo(44, 130, 46, 150, 52, 164); sd.curveTo(62, 150, 62, 120, 62, 98); sd.closePath();
                sd.moveTo(148, 100); sd.curveTo(156, 130, 154, 150, 148, 164); sd.curveTo(138, 150, 138, 120, 138, 98); sd.closePath(); extra = sd; break; }
            default: { // bald: scalp highlight and graying temples
                g.setPaint(new RadialGradientPaint(new Point2D.Double(100, 50), 26, new float[]{0f, 1f}, new Color[]{new Color(255, 255, 255, 60), new Color(255, 255, 255, 0)}));
                g.fill(new Ellipse2D.Double(70, 38, 60, 26));
                Path2D tp = new Path2D.Double(); tp.moveTo(56, 98); tp.curveTo(54, 80, 58, 70, 64, 66); tp.lineTo(66, 96); tp.closePath();
                tp.moveTo(144, 98); tp.curveTo(146, 80, 142, 70, 136, 66); tp.lineTo(134, 96); tp.closePath();
                paintHair(g, tp, shade(t.hair, 0.1), rnd, 60, 100); }
        }
        paintHair(g, front, t.hair, rnd, 22, 104);
        paintHair(g, extra, t.hair, rnd, 90, 230);
        if (st == 3) { g.setColor(alpha(shade(t.hair, -0.4), 160)); g.setStroke(new BasicStroke(1.6f)); g.draw(new Arc2D.Double(86, 20, 28, 12, 0, -180, Arc2D.OPEN)); }
        if (st == 1) { g.setColor(alpha(Color.BLACK, 70)); g.setStroke(new BasicStroke(1.1f)); g.draw(new QuadCurve2D.Double(90, 26, 84, 40, 82, 62)); }

        // ---- sweat drop when nervous / caught
        if (mood == Mood.WORRIED) {
            Path2D d = new Path2D.Double(); d.moveTo(146, 56); d.curveTo(152, 68, 156, 72, 146, 82); d.curveTo(136, 72, 140, 68, 146, 56);
            g.setPaint(new GradientPaint(138, 56, new Color(200, 232, 255, 230), 156, 82, new Color(120, 180, 230, 230))); g.fill(d);
            g.setColor(new Color(255, 255, 255, 190)); g.fill(new Ellipse2D.Double(143, 66, 3, 5));
        }
        g.dispose();
    }
}
