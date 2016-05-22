package gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Created by kutase123 on 20.05.2016.
 */
public class Robot implements Observable {
    private ArrayList<Observer> subscribers;

    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    public Robot() {
        subscribers = new ArrayList<Observer>();
    }

    public void subscribe (Observer o) {
        subscribers.add(o);
    }

    public void dispose (Observer o) {
        subscribers.remove(o);
    }

    public void notifySubscribers () {
        for (Observer o : subscribers) {
            o.update(m_robotPositionX, m_robotPositionY);
        }
    }

    private void moveRobot (double velocity, double angularVelocity, double duration)
    {
        velocity = GameMath.applyLimits(velocity, 0, maxVelocity);
        angularVelocity = GameMath.applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection  + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = GameMath.asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;

        notifySubscribers();
    }

    public void update (int targetX, int targetY)
    {
        double distance = GameMath.distance(targetX, targetY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.5)
        {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = GameMath.angleTo(m_robotPositionX, m_robotPositionY, targetX, targetY);
        double angularVelocity = 0;
        if (angleToTarget > m_robotDirection)
        {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < m_robotDirection)
        {
            angularVelocity = -maxAngularVelocity;
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    public void draw(Graphics2D g)
    {
        int robotCenterX = GameMath.round(m_robotPositionX);
        int robotCenterY = GameMath.round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(m_robotDirection, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        GameGraphics.fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        GameGraphics.drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        GameGraphics.fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        GameGraphics.drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }
}
