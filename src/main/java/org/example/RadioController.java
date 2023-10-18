package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RadioController {
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String RADIO_CHANNEL_FILE_NAME = "/validRadioChannels.csv";
    private final Radio radio;
    private final Map<String, Float> validRadioChannelMap;
    private boolean continueRunning = true;

    public RadioController() {
        this.radio = new Radio();
        this.validRadioChannelMap = readCSV(RADIO_CHANNEL_FILE_NAME);
    }

    public void startRadio() {

        while (this.continueRunning) {
            printRadioState();
            changeSetting();
        }
    }

    private Map<String, Float> readCSV(String fileName) {
        Map<String, Float> radioChannels = new HashMap<>();
        try {
            InputStream inputStream = RadioController.class.getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + fileName);
            }
            Reader reader = new InputStreamReader(inputStream);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withDelimiter(','));

            for (CSVRecord record : csvParser) {
                String channel = record.get("radioChannel");
                float frequency = Float.parseFloat(record.get("frequency"));
                radioChannels.put(channel, frequency);
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            System.exit(0);
        }
        return radioChannels;
    }


    private void printRadioState() {
        String radioPlayingStatus = "Off";
        if (this.radio.isPlaying()) {
            radioPlayingStatus = "On";
        }
        int volume = this.radio.getVolume();
        float frequency = this.radio.getFrequency();

        String radioChannel = getChannelByFrequency(frequency);


        String radioState = String.format("Radio is %s, Volume: %d%%, Frequency: %s MHz %s",
                radioPlayingStatus, volume, frequency, radioChannel);
        String ansi_color;
        if (radio.isPlaying()) {
            ansi_color = ANSI_GREEN;
        } else {
            ansi_color = ANSI_RED;
        }

        System.out.println(ansi_color + radioState + ANSI_RESET);
    }

    private String getChannelByFrequency(float frequency) {
        final float TOLERANCE = 0.05f;
        for (Map.Entry<String, Float> radioChannel : this.validRadioChannelMap.entrySet()) {
            if (Math.abs(radioChannel.getValue() - frequency) <= TOLERANCE) {
                return radioChannel.getKey();
            }
        }
        return "";
    }

    private void changeSetting() {
        Scanner scanner = new Scanner(System.in);

        System.out.println(String.format("\n%-15s Setting", "Input")
                + String.format("\n%-15s Lower Volume", "Low")
                + String.format("\n%-15s Up Volume", "Up")
                + String.format("\n%-15s Turn Radio On", "On")
                + String.format("\n%-15s Turn Radio Off", "Off")
                + String.format("\n%-15s Change Frequency", "f {frequency}")
                + "\n\nPlease Input desired setting" + "\nExit Program with: exit ");

        String input = scanner.nextLine();
        input = input.trim();

        switch (input.toLowerCase()) {
            case "low":
                radio.lowerVolume();
                break;
            case "up":
                radio.upVolume();
                break;
            case "on":
                radio.turnOn();
                break;
            case "off":
                radio.turnOff();
                break;
            case "exit":
                this.continueRunning = false;
                break;
            default:
                String invalidInput = "Invalid input. (no setting changed)";
                if (!input.trim().toLowerCase().startsWith("f")) {
                    System.out.println(invalidInput);
                    break;
                }
                Pattern pattern = Pattern.compile("(^f)\\s*(?<frequency>[0-9]{1,3}([.,][0-9]*)?)$");
                Matcher matcher = pattern.matcher(input);
                if (!matcher.find()) {
                    System.out.println(invalidInput);
                }
                if (matcher.matches()) {
                    String frequencyString = matcher.group("frequency").replace(',', '.');
                    try {
                        float frequency = Float.parseFloat(frequencyString);
                        this.radio.changeFrequency(frequency);
                    } catch (Exception e) {
                        System.out.println("Invalid frequency input." + frequencyString);
                    }
                }
        }
    }
}


