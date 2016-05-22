package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();

    private GameWindowState gameWindowState = new GameWindowState();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        gameWindowState.restoreWindowsState();

        gameWindowState.gameState.forEach((tuple) -> {
            String type = tuple.x;
            int[] params = tuple.y;
            System.out.println(String.format("%s", type));

            createWindowByType(type, params);
        });

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionOnExit();
//                super.windowClosing(e);
            }
        });
    }
    
    protected LogWindow createLogWindow(int[] params)
    {
        int locationX = params[0];
        int locationY = params[1];

        int sizeX = params[2];
        int sizeY = params[3];

        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(locationX, locationY);
        logWindow.setSize(sizeX, sizeY);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");

        logWindow.setName("log");

        return logWindow;
    }

    protected GameWindow createGameWindow(int[] params)
    {
        int locationX = params[0];
        int locationY = params[1];

        int sizeX = params[2];
        int sizeY = params[3];

        GameWindow gameWindow = new GameWindow();
        gameWindow.setLocation(locationX, locationY);
        gameWindow.setSize(sizeX, sizeY);

        GameCoords coordsView = createCoordsWindow(new int[] {400, 400, 210, 100});
        desktopPane.add(coordsView);
        coordsView.setVisible(true);

        gameWindow.robot.subscribe(new Observer() {
            @Override
            public void update(double x, double y) {
//                System.out.println(Double.toString(x) + " x " + Double.toString(y));
                coordsView.updateCoordsContent(Double.toString(GameMath.round(x)), Double.toString(GameMath.round(y)));
            }
        });

        gameWindow.setName("game");

        return gameWindow;
    }

    protected GameCoords createCoordsWindow(int[] params)
    {
        int locationX = params[0];
        int locationY = params[1];

        int sizeX = params[2];
        int sizeY = params[3];

        GameCoords coordsWindow = new GameCoords();
        coordsWindow.setLocation(locationX, locationY);
        coordsWindow.setSize(sizeX, sizeY);

        coordsWindow.setName("coords");

        return coordsWindow;
    }

    protected void createWindowByType(String type, int[] params) {
        switch (type) {
            case "log":
                addWindow(createLogWindow(params));
                break;
            case "game":
                addWindow(createGameWindow(params));
                break;
            case "coords":
                addWindow(createCoordsWindow(params));
                break;
        }
    }

    protected void addSubMenu (JMenuItem parent, String title, Integer keyEvent, ActionListener func) {
        JMenuItem child = new JMenuItem(title, keyEvent);
        child.addActionListener(func);
        parent.add(child);
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
        gameWindowState.addWindow(frame);
        frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing (InternalFrameEvent e) {
                gameWindowState.removeWindow(frame);
                frame.dispose();
            }
        });
    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        addSubMenu(lookAndFeelMenu, "Системная схема", KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });

        addSubMenu(lookAndFeelMenu, "Универсальная схема", KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        addSubMenu(testMenu, "Сообщение в лог", KeyEvent.VK_S, (event) -> {
            Logger.debug("Новая строка");
        });

        JMenu addWindowMenu = new JMenu("Добавить окно");
        addWindowMenu.setMnemonic(KeyEvent.VK_T);
        addWindowMenu.getAccessibleContext().setAccessibleDescription(
                "Добавление новых окон");

        addSubMenu(addWindowMenu, "Игровое поле", KeyEvent.VK_S, (event) -> {
            createWindowByType("game", new int[] {400, 400, 500, 500});
        });

        addSubMenu(addWindowMenu, "Протокол работы", KeyEvent.VK_S, (event) -> {
            createWindowByType("log", new int[] {400, 400, 500, 500});
        });

//        addSubMenu(addWindowMenu, "Координаты", KeyEvent.VK_S, (event) -> {
//            createWindowByType("coords", new int[] {100, 100, 100, 100});
//        });

        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X | KeyEvent.VK_ALT);
        exitItem.addActionListener((event) -> {
            actionOnExit();
        });

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(addWindowMenu);
        menuBar.add(exitItem);

        return menuBar;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    private void actionOnExit () {
        Object[] options = {"Да", "Нет"};
        int output = JOptionPane.showOptionDialog(this, "Вы уверены?", "Выход", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (output == JOptionPane.YES_OPTION) {
            gameWindowState.saveWindowsState();
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            System.exit(0);
        }
    }
}
