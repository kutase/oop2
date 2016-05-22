package gui;

import javax.swing.*;
import java.awt.*;

public class GameCoords extends JInternalFrame
{
    private JLabel coordsX, coordsY;

    public GameCoords()
    {
        super("Координаты робота", true, true, true, true);
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;

        coordsX = new JLabel("");
        getContentPane().add(coordsX, constraints);

        constraints.gridy = 1;
        coordsY = new JLabel("");
        getContentPane().add(coordsY, constraints);

        pack();
    }

    public void updateCoordsContent(String x, String y)
    {
        coordsX.setText("X: " + x);
        coordsY.setText("Y: " + y);
    }
}