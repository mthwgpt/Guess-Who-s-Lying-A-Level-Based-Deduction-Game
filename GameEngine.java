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

/** Logic engine: case generation and the solver that verifies every case. */
public final class GameEngine {
    /** Which suspects could be the (only) liar given every statement and piece of evidence? */
    static List<Integer> candidates(Case c) {
        List<Integer> out = new ArrayList<>();
        int P = c.sc.where.length;
        for (int k = 0; k < c.n; k++) {
            boolean ok = false;
            for (int p = 0; p < P && !ok; p++) {
                if (p == c.self[k]) continue;                    // the liar's own alibi must be false
                int[] w = new int[c.n];
                for (int i = 0; i < c.n; i++) w[i] = (i == k) ? p : c.self[i];   // truth-tellers' alibis are true
                boolean good = true;
                for (int e = 0; e < c.evS.length && good; e++) if (w[c.evS[e]] != c.evP[e]) good = false;
                for (int i = 0; i < c.n && good; i++) {
                    if (i == k) continue;
                    for (int q = 0; q < 2; q++) if (w[c.obsT[i][q]] != c.obsP[i][q]) good = false;
                }
                ok = good;
            }
            if (ok) out.add(k);
        }
        return out;
    }

    static int suspectCount(int level) { return Math.min(6, 3 + (level - 1) / 25); }
    static int questionBudget(int level) { int n = suspectCount(level); return Math.max(n + 2, 2 * n - (level - 1) / 12); }

    static Case generate(int level, Random r) {
        int n = suspectCount(level);
        for (int attempt = 0; ; attempt++) {
            Case c = new Case();
            c.n = n; c.sc = SCENARIOS[r.nextInt(SCENARIOS.length)];
            int P = c.sc.where.length;
            c.loc = new int[n];
            if (level <= 25) {                                   // early levels: everyone in a different place
                List<Integer> idx = new ArrayList<>(); for (int i = 0; i < P; i++) idx.add(i);
                Collections.shuffle(idx, r); for (int i = 0; i < n; i++) c.loc[i] = idx.get(i);
            } else for (int i = 0; i < n; i++) c.loc[i] = r.nextInt(P);
            c.liar = r.nextInt(n);
            c.self = c.loc.clone();
            int fake; do { fake = r.nextInt(P); } while (fake == c.loc[c.liar]);
            c.self[c.liar] = fake;
            c.obsT = new int[n][2]; c.obsP = new int[n][2]; c.tpl = new int[n];
            for (int i = 0; i < n; i++) {
                List<Integer> others = new ArrayList<>(); for (int j = 0; j < n; j++) if (j != i) others.add(j);
                Collections.shuffle(others, r); c.tpl[i] = r.nextInt(3);
                for (int q = 0; q < 2; q++) {
                    c.obsT[i][q] = others.get(q);
                    c.obsP[i][q] = (i == c.liar) ? r.nextInt(P) : c.loc[others.get(q)];
                }
            }
            int ec = level <= 60 ? 2 : 1;
            List<Integer> ev = new ArrayList<>(); for (int i = 0; i < n; i++) ev.add(i);
            Collections.shuffle(ev, r);
            c.evS = new int[ec]; c.evP = new int[ec];
            for (int e = 0; e < ec; e++) { c.evS[e] = ev.get(e); c.evP[e] = c.loc[ev.get(e)]; }
            List<Integer> cand = candidates(c);
            if (cand.size() != 1 || cand.get(0) != c.liar) continue;     // keep only fair, uniquely solvable cases

            List<String> fn = new ArrayList<>(Arrays.asList(FEMALE)), mn = new ArrayList<>(Arrays.asList(MALE));
            Collections.shuffle(fn, r); Collections.shuffle(mn, r);
            List<String> roles = new ArrayList<>(Arrays.asList(c.sc.roles)); Collections.shuffle(roles, r);
            c.names = new String[n]; c.roles = new String[n]; c.traits = new Traits[n];
            for (int i = 0; i < n; i++) {
                boolean female = r.nextBoolean();
                c.names[i] = female ? fn.remove(0) : mn.remove(0);
                c.roles[i] = roles.get(i);
                c.traits[i] = Traits.make(female, r);
            }
            c.budget = questionBudget(level);
            return c;
        }
    }
}
