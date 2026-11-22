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

/** A generated case: who is where, who is lying, and every clue. */
class Case {
    Scenario sc; int n, liar, budget; String[] names, roles; Traits[] traits;
    int[] loc, self; int[][] obsT, obsP; int[] evS, evP; int[] tpl;
    String first(int i) { return names[i].substring(0, names[i].indexOf(' ')); }
    String selfLine(int i) {
        String w = sc.where[self[i]], d = sc.doing[self[i]];
        switch (tpl[i] % 3) {
            case 0: return "At " + sc.time + " I was " + w + ", " + d + ". Ask anyone.";
            case 1: return "I was " + w + " at " + sc.time + ", " + d + ". That's all there is to it.";
            default: return "Around " + sc.time + " you would have found me " + w + ", " + d + ".";
        }
    }
    String obsLine(int i, int q) {
        String f = first(obsT[i][q]), w = sc.where[obsP[i][q]];
        switch ((i + q + tpl[i]) % 3) {
            case 0: return "I noticed " + f + " " + w + " around " + sc.time + ".";
            case 1: return "I'm fairly sure " + f + " was " + w + " at " + sc.time + ".";
            default: return "When I looked over, " + f + " was " + w + ".";
        }
    }
    String evLine(int k) {
        String f = first(evS[k]), w = sc.where[evP[k]];
        switch ((k + tpl[0]) % 3) {
            case 0: return "Security footage places " + f + " " + w + " at " + sc.time + ".";
            case 1: return "A keycard log confirms " + f + " was " + w + " at " + sc.time + ".";
            default: return "A time-stamped photo shows " + f + " " + w + " at " + sc.time + ".";
        }
    }
}
