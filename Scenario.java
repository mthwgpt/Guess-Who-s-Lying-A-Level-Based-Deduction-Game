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

/** One case template: title, time, story, roles and the places/activities. */
class Scenario {
    String title, intro, time; String[] roles, where, doing;
    Scenario(String title, String time, String intro, String roles, String... places) {
        this.title = title; this.time = time; this.intro = intro; this.roles = roles.split(",");
        where = new String[places.length]; doing = new String[places.length];
        for (int i = 0; i < places.length; i++) { String[] p = places[i].split("\\|"); where[i] = p[0]; doing[i] = p[1]; }
    }
}
