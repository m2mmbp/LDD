package org.sunflow.core.display;

import bublible.Utils;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;
import org.sunflow.image.Color;
import org.sunflow.system.ImagePanel;

public class FrameDisplay implements Display {

    private String filename;
    private RenderFrame frame;

    public FrameDisplay() {
        this(null);
    }

    public FrameDisplay(String filename) {
        this.filename = filename;
        frame = null;
    }

    public void imageBegin(int w, int h, int bucketSize) {
        //System.out.println("imageBegin ***********************************************");
        if (frame == null) {
            frame = new RenderFrame();
            frame.imagePanel.imageBegin(w, h, bucketSize);
            Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
            boolean needFit = false;
            if (w >= (screenRes.getWidth() - 200) || h >= (screenRes.getHeight() - 200)) {
                frame.imagePanel.setPreferredSize(new Dimension((int) screenRes.getWidth() - 200, (int) screenRes.getHeight() - 200));
                needFit = true;
            } else {
                frame.imagePanel.setPreferredSize(new Dimension(w, h));
            }
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            if (needFit) {
                frame.imagePanel.fit();
            }
        } else {
            frame.imagePanel.imageBegin(w, h, bucketSize);
        }
    }

    public void imagePrepare(int x, int y, int w, int h, int id) {
        //System.out.println("imagePrepare ***********************************************");
        frame.imagePanel.imagePrepare(x, y, w, h, id);
    }

    public void imageUpdate(int x, int y, int w, int h, Color[] data) {
        //System.out.println("imageUpdate ***********************************************");
        frame.imagePanel.imageUpdate(x, y, w, h, data);
    }

    public void imageFill(int x, int y, int w, int h, Color c) {
        //System.out.println("imageFill ***********************************************");
        frame.imagePanel.imageFill(x, y, w, h, c);
    }

    public void imageEnd() {
        //System.out.println("imageEnd ***********************************************");
        frame.imagePanel.imageEnd();
        if (filename != null) {
            frame.imagePanel.save(filename);
        }
    }

    @SuppressWarnings("serial")
    private static class RenderFrame extends JFrame {

        ImagePanel imagePanel;

        RenderFrame() {
            super(Utils.INFO);
            /*setDefaultCloseOperation(EXIT_ON_CLOSE);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        System.exit(0);
                    }
                }
            });*/
            imagePanel = new ImagePanel();
            setContentPane(imagePanel);
            pack();
        }
    }
}
