package az.araezarzosa.spotifyoverlay.spotify_in_game_overlay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class ErrorDialog {
    public static void showErrorMessage() {
    	JFrame frame = new JFrame("Error");
        URL iconURL = App.class.getResource("icon.png");
		ImageIcon icon = new ImageIcon(iconURL);
		Image iconImage = icon.getImage();
		Image resizedImage = iconImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		frame.setIconImage(resizedImage);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 100);
        frame.setResizable(false);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	    JPanel panel = new JPanel();
	    panel.setLayout(new GridBagLayout());
	    panel.setBackground(new Color(25, 20, 20));
	    
	    JLabel label = new JLabel("Error: Invalid Input");
	    label.setFont(new Font("Circular", Font.BOLD, 12));
	    label.setForeground(new Color(30, 215, 96));
	    GridBagConstraints labelConstraints = new GridBagConstraints();
	    labelConstraints.gridx = 0;
	    labelConstraints.gridy = 0;
	    labelConstraints.insets = new Insets(0, 0, 10, 0);
	    panel.add(label, labelConstraints);

        JButton errorButton = new JButton("Ok");
        errorButton.setFont(new Font("Circular", Font.BOLD, 12));
        errorButton.setBackground(new Color(30, 215, 96));
        errorButton.setForeground(Color.BLACK);
        errorButton.setBorder(BorderFactory.createEmptyBorder());
        errorButton.setBorderPainted(false);
        errorButton.setPreferredSize(new Dimension(125, 35)); 
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.gridy = 1;
        buttonConstraints.insets = new Insets(0, 0, 10, 0);
	    panel.add(errorButton, buttonConstraints);
        errorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	frame.dispose();
            }
        });
        frame.add(errorButton);

        frame.setVisible(true);
    }
}