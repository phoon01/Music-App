import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlaylistMenu extends JFrame {

    PlaylistMenu(String name, String email){

        JButton backToMenu  = new JButton("Back to menu!");

        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();

            }
        });

        JButton seePlaylists = new JButton("See my playlists");

        seePlaylists.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                searchedPlaylistMenu searchedPlaylistMenu = new searchedPlaylistMenu(email);
                searchedPlaylistMenu.setVisible(true);

            }
        });

        JButton createPlaylist = new JButton("Create a playlist");

        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                createPlaylistMenu createPlaylist = new createPlaylistMenu(email);
                createPlaylist.setVisible(true);


            }
        });

        JButton deletePlaylist = new JButton("Delete a playlist");

        deletePlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                deletePlaylistMenu deletePlaylistMenu = new deletePlaylistMenu(email);
                deletePlaylistMenu.setVisible(true);

            }
        });

        JPanel panel = new JPanel(new GridLayout(4,1));

        panel.add(seePlaylists);
        panel.add(createPlaylist);
        panel.add(deletePlaylist);
        panel.add(backToMenu);

        add(panel);

        setTitle(name);

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);

    }

}
