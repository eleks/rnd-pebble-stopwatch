package com.eleks.rnd.time.sw2.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.eleks.rnd.time.sw2.R;

public class Hardcoded {

    private static Random r = new Random();
    private static final String[] narrativeSamples = new String[] {
            "Fox Force Five. Fox, as in we're a bunch of foxy chicks. Force, as in we're a force to be reckoned with. And five, as in there's one, two, three, four, five of us. There was a blonde one, Sommerset O'Neal, she was a leader. The Japanese fox was a kung fu master.",
            "There's a passage I got memorized. Ezekiel 25:17. “The path of the righteous man is beset on all sides by the inequities of the selfish and the tyranny of evil men. Blessed is he who, in the name of charity and good will, shepherds the weak through the valley of the darkness, for he is truly his brother's keeper and the finder of lost children.",
            "I ain't saying it's right. But you're saying a foot massage don't mean nothing, and I'm saying it does. Now look, I've given a million ladies a million foot massages, and they all meant something.",
            "According to the show, she was the deadliest woman in the world with a knife. And she knew a zillion old jokes her grandfather, an old vaudevillian, taught her. And if we would have got picked up, they would have worked in a gimmick where every show I would have told another joke." };

    private List<TimeEntry> entries = new ArrayList<TimeEntry>();

    private Hardcoded() {
        createEntries();
    }

    private void createEntries() {
        entries.add(createEntry(entries.size(), "Google", "Project X Glass"));
        entries.add(createEntry(entries.size(), "Boston Consulting Group", "International company consulting"));
        entries.add(createEntry(entries.size(), "Edward Jones", "Financial Services & Insurance"));
        entries.add(createEntry(entries.size(), "salesforce", "Information Technology"));
        entries.add(createEntry(entries.size(), "DPR Construction", "Construction & Real Estate"));
        entries.add(createEntry(entries.size(), "Burns & McDonnell", "Engineering Consulting"));
        entries.add(createEntry(entries.size(), "USAA", "Corporate Insurance"));
        entries.add(createEntry(entries.size(), "Plante Moran", "Management Consulting"));
        entries.add(createEntry(entries.size(), "NuStar Energy", "Salary Compensation Strategy"));
    }

    private TimeEntry createEntry(int id, String client, String matter) {
        String narrative = narrativeSamples[r.nextInt(narrativeSamples.length)];
        return new TimeEntry(String.valueOf(id), client, matter, narrative);
    }

    public List<TimeEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    
    /**
     * For demo purposes, id is a position in a list.
     * For real purposes, something like UUID should be used.
     * @param id
     * @return
     */
    public TimeEntry getById(String id) {
        return entries.get(Integer.parseInt(id));
    }

    public static final Hardcoded DATA = new Hardcoded();
}
