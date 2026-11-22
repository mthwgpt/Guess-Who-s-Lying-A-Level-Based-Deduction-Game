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

/** Randomised look of one suspect. */
class Traits {
    static final Color[] SKINS = {new Color(255, 224, 196), new Color(241, 194, 155), new Color(224, 172, 128),
        new Color(198, 134, 95), new Color(141, 85, 54), new Color(98, 60, 40)};
    static final Color[] HAIRS = {new Color(28, 22, 20), new Color(58, 38, 28), new Color(102, 68, 42), new Color(122, 54, 34),
        new Color(206, 172, 104), new Color(18, 16, 16)};
    static final Color[] EYES = {new Color(88, 56, 34), new Color(60, 100, 150), new Color(70, 120, 84), new Color(44, 34, 28), new Color(112, 112, 96)};
    static final Color[] CLOTHES = {new Color(52, 84, 140), new Color(130, 44, 56), new Color(46, 110, 90), new Color(70, 70, 82),
        new Color(160, 110, 50), new Color(96, 58, 120), new Color(36, 52, 74)};
    boolean female, glasses, tie; Color skin, hair, eye, cloth, bg; int hairStyle, beard, age; long seed;
    static Traits make(boolean female, Random r) {
        Traits t = new Traits(); t.female = female; t.seed = r.nextLong();
        t.skin = SKINS[r.nextInt(SKINS.length)]; t.age = r.nextInt(3);
        t.hair = HAIRS[r.nextInt(HAIRS.length)];
        if (t.age == 2 && r.nextInt(10) < 7) { int g = 170 + r.nextInt(50); t.hair = new Color(g, g, g + 4); }
        t.eye = EYES[r.nextInt(EYES.length)]; t.cloth = CLOTHES[r.nextInt(CLOTHES.length)];
        int[] f = {1, 2, 3, 4, 6}, m = {0, 1, 4, 5, 0, 1};
        t.hairStyle = female ? f[r.nextInt(f.length)] : m[r.nextInt(m.length)];
        if (t.hairStyle == 5 && t.age == 0) t.hairStyle = 0;
        t.beard = female ? 0 : new int[]{0, 0, 1, 2}[r.nextInt(4)];
        t.glasses = r.nextInt(10) < 3; t.tie = !female && r.nextInt(10) < 6;
        Color[] bgs = {new Color(70, 92, 120), new Color(64, 100, 100), new Color(110, 72, 82), new Color(92, 94, 66), new Color(84, 76, 120)};
        t.bg = bgs[r.nextInt(bgs.length)];
        return t;
    }
}
