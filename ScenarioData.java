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

/** Scenario catalogue and the name pools. */
public final class ScenarioData {
    static final Scenario[] SCENARIOS = {
        new Scenario("The Vanishing Sapphire", "9:30 PM",
            "During the Hartwell Museum's charity gala, the famous Blue Star Sapphire disappeared from its display case. "
          + "The alarm never sounded and the case was found unlocked. Everyone was questioned about the moment the lights flickered. "
          + "All but one of them are telling the truth.",
            "Curator,Night Guard,Caterer,Art Restorer,Gala Donor,Intern,Tour Guide",
            "in the East Gallery|admiring the paintings", "at the loading dock|signing for a delivery",
            "in the catering kitchen|plating desserts", "in the security office|watching the monitors",
            "in the gift shop|counting the day's receipts", "in the conservation lab|cleaning an old canvas",
            "in the rooftop garden|taking a phone call"),
        new Scenario("The Leaked Merger Memo", "6:30 PM",
            "Northbridge Capital's confidential merger memo reached a newspaper reporter minutes before the evening deadline. "
          + "The leak came from the 14th floor, and the building's cameras only cover a few rooms. One employee is lying about where they were.",
            "Senior Analyst,Office Manager,IT Technician,Intern,Executive Assistant,Accountant,Legal Counsel",
            "in the print room|collecting a stack of reports", "in the break room|making coffee",
            "in the server closet|replacing a network cable", "in Conference Room B|rehearsing a presentation",
            "at the reception desk|sorting the mail", "in the stairwell|taking a private call", "in the lobby|waiting for a taxi"),
        new Scenario("The Ruined Wedding Cake", "4:15 PM",
            "Three beautiful tiers of cake collapsed in the back of the banquet hall, just an hour before the Alvarez wedding reception. "
          + "It looks like sabotage, not an accident. Every vendor has an alibi, but one of them has been inventing theirs.",
            "Pastry Chef,Wedding Planner,Photographer,Florist,Best Man,Head Waiter,Bartender",
            "in the flower room|trimming centerpieces", "at the bar|polishing glasses", "in the garden|setting up the ceremony arch",
            "in the kitchen|checking the menu", "at the DJ booth|testing the microphone", "in the parking lot|unloading equipment",
            "in the photo tent|adjusting the lights"),
        new Scenario("The Stolen Exam Papers", "3:20 PM",
            "The final exam papers at Maplewood High vanished from the locked staff office right after the last bell. "
          + "The door was not forced. The principal has interviewed the staff and is certain that one of them is lying.",
            "Math Teacher,Caretaker,Student Council President,Librarian,Coach,School Secretary,Lab Technician",
            "in the library|reshelving books", "in the gymnasium|setting out equipment", "in the science lab|preparing an experiment",
            "in the cafeteria|wiping down tables", "in the front office|answering phones", "in the teachers' lounge|grading essays",
            "on the football field|marking the lines"),
        new Scenario("The Missing Orchid Seeds", "11:10 AM",
            "At the Larkspur Research Institute, a tray of rare orchid seeds worth a fortune disappeared from a locked cold-storage freezer. "
          + "Door logs were wiped clean. Whoever did it is hiding behind a false alibi.",
            "Lead Botanist,Lab Assistant,Safety Officer,Courier,Groundskeeper,Postdoctoral Fellow,Receptionist",
            "in the greenhouse|watering seedlings", "in the cold storage room|logging inventory", "in the sample prep lab|labeling tubes",
            "at the shipping bay|taping up boxes", "in the break room|reading the news", "in the records office|filing paperwork",
            "in the courtyard|eating a late breakfast"),
        new Scenario("The Penthouse Necklace", "8:00 PM",
            "A diamond necklace vanished from the safe of the penthouse suite at the Grand Meridian Hotel. "
          + "The safe was opened with the correct code. The staff on duty gave their statements, but one of them is bending the truth.",
            "Concierge,Housekeeper,Bellhop,Room Service Waiter,Hotel Manager,Pianist,Night Porter",
            "in the main lobby|greeting new guests", "in the laundry room|folding linens", "in the hotel bar|stocking the shelves",
            "in the service elevator|restocking supplies", "in the ballroom|tuning the piano", "in the kitchen|plating a late order",
            "on the 12th-floor corridor|delivering fresh towels"),
        new Scenario("The Sabotaged Premiere", "7:15 PM",
            "Minutes before opening night at the Aurora Theater, the stage lights failed and the lead actress's costume was found slashed. "
          + "The curtain was delayed by an hour. Somebody in this cast and crew is lying about where they were.",
            "Director,Stage Manager,Lead Actor,Costume Designer,Lighting Technician,Understudy,Head Usher",
            "backstage|checking the props table", "in the costume room|sewing a hem", "in the lighting booth|testing the dimmers",
            "in the green room|running lines", "in the box office|selling last-minute tickets", "in the orchestra pit|tuning instruments",
            "in the front lobby|handing out programs"),
        new Scenario("The Rare Manuscript", "2:40 PM",
            "A medieval manuscript disappeared from the climate-controlled vault of the Ashford Library. "
          + "The vault log shows no forced entry. The head librarian has collected statements, and one of them cannot be true.",
            "Head Librarian,Archivist,Visiting Professor,Security Guard,Student Volunteer,Book Conservator,Researcher",
            "in the reading room|taking notes", "in the rare books vault|checking the humidity gauges", "in the archive basement|cataloging boxes",
            "at the circulation desk|stamping returns", "in the cafe|having tea", "in the map room|studying an atlas",
            "in the courtyard|sketching the fountain")
    };

    static final String[] FEMALE = {"Maya Collins", "Sofia Alvarez", "Hannah Brooks", "Priya Nair", "Chloe Dubois", "Amara Johnson",
        "Elena Petrova", "Yuki Tanaka", "Grace Mitchell", "Lucia Moreno", "Nora Lindqvist", "Fatima Rahman"};
    static final String[] MALE = {"Daniel Okafor", "Marcus Bell", "Liam Walsh", "Rafael Santos", "Ethan Cho", "Omar Haddad",
        "Tobias Klein", "Noah Fischer", "Samir Patel", "Henry Doyle", "Diego Ramos", "Arthur Penn"};
}
