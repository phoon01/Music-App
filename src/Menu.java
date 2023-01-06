import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;

public class Menu extends JFrame {
    private String newName;

    Menu(String name,String email,String password){

        email = email.replace("{", "");
        email = email.replace("}", "");

        newName = name;
        if(name.contains("{") || name.contains("}")) {
            newName = name.substring(1,name.length()-1);
        }

        JButton signOut = new JButton("Sign-out");

        signOut.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true);
                dispose();

            }
        });

        JButton addSongButton = new JButton("Upload Song");

        String finalEmail = email;
        addSongButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                AddSong addSong = new AddSong(newName, finalEmail);
                addSong.setVisible(true);

            }
        });

        JTextField searchBox = new JTextField("Search for a song or an artist!");
        searchBox.setHorizontalAlignment(JTextField.CENTER);

        searchBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                searchBox.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                searchBox.setText("Search for a song or an artist!");
            }

        });

        String finalEmail1 = email;
        searchBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String search = searchBox.getText();

                searchSongArtist(search, finalEmail1);

            }
        });

        JButton playlistButton = new JButton("My playlists");

        String finalEmail2 = email;
        playlistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {



                PlaylistMenu playlistMenu = new PlaylistMenu(newName, finalEmail2);

                playlistMenu.setVisible(true);

            }
        });

        JPanel panel = new JPanel(new GridLayout(4,1));

        panel.add(searchBox);
        panel.add(playlistButton);
        panel.add(addSongButton);
        panel.add(signOut);

        add(panel);

        setTitle(newName);

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void searchSongArtist(String search, String email) {

        try {

            if (search == null || search.equals("")) {
                throw new ExceptionMissingDetail("No input in search box!");
            }

            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement();

            String tableName = "\"Project\".\"songs\"";

            String getTitleOrArtist = "select song_name, artist_name from "+tableName+" where CAST(song_name as text) like '%"+search+"%' or CAST(artist_name as text) like '%"+search+"%'";

            /*SELECT song_name, artist_name
            FROM "Project".songs where CAST(song_name as text) like '%Raul%'
            or CAST(artist_name as text) like '%Raul%'*/

            statement.execute(getTitleOrArtist);

            ResultSet resultSet = statement.getResultSet();

            if(!resultSet.next()){
                throw new ExceptionNoSongOrArtist("No song or artist found!");
            }

            searchResults searchResults = new searchResults(resultSet,email);

        } catch (ExceptionMissingDetail e) {
            JOptionPane.showMessageDialog(new JFrame(),"Empty search box!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ExceptionNoSongOrArtist e) {
            JOptionPane.showMessageDialog(new JFrame(),"No matching song or artist","ERROR",JOptionPane.WARNING_MESSAGE);
        }

    }

}
