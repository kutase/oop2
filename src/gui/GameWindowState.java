package gui;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import sun.plugin2.message.Conversation;

import javax.swing.JInternalFrame;
import java.io.*;
import java.util.*;
import com.google.common.collect.HashMultimap;

/**
 * Created by kutase123 on 23.04.2016.
 */
public class GameWindowState implements WindowState {
    private ArrayList<JInternalFrame> windows = new ArrayList<JInternalFrame>();

    public static final String path = "settings.txt";

    public ArrayList<Tuple<String, int[]>> gameState = new ArrayList<Tuple<String, int[]>>();

    public GameWindowState () {}

    public void addWindow (JInternalFrame window) {
        windows.add(window);
    }

    public void removeWindow (JInternalFrame window) { windows.remove(window); }

    public void saveWindowsState () {
        try (FileWriter writer = new FileWriter(path)) {
            windows.forEach((window) -> {
                try {
                    System.out.println(window.getName() + " " + window.getTitle() + " " + window.getLayer());
                    writer.write(window.getName() + "\n");
                    writer.write(String.format("%1$s\n%2$s\n", window.getX(), window.getY()));
                    writer.write(String.format("%1$s\n%2$s\n", window.getWidth(), window.getHeight()));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            });

            writer.write("e");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void restoreWindowsState () {
        File f = new File (path);
        if (f.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                ArrayList<String> lines = new ArrayList<String>();
                String line;

                while (!Objects.equals(line = reader.readLine(), "e")) {
                    lines.add(line);
                }

                // check if file not blank
                if (lines.size() > 0 && lines.get(0) != null && !Objects.equals(lines.get(0), "e") && lines.size()/5 != 0) {
                    int lineStart = 0;
                    for (int i = 0; i < lines.size()/5; i++) {
                        System.out.println(">>>>>>>>>>>>>>>>>>>>");
                        int[] state = new int[4];
                        for (int j = 1; j < 5; j++) {
                            System.out.println(String.format("%d %d", j, j+lineStart));
                            state[j-1] = Integer.parseInt(lines.get(j+lineStart));
                        }
                        System.out.println(lines.get(lineStart));
                        gameState.add(new Tuple<String, int[]>(lines.get(lineStart), state));
                        lineStart += 5;
                    }
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
