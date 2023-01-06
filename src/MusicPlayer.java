import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.*;
import jaco.mp3.player.MP3Player;

public class MusicPlayer extends JFrame {

    MusicPlayer(String artist, String title){

        File song = findSong(artist,title);

        MP3Player mp3Player = new MP3Player(song);
        mp3Player.play();


        JLabel songInfo = new JLabel(title + " - " + artist);

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(mp3Player.isStopped() || mp3Player.isPaused()){
                    mp3Player.play();
                }
            }
        });
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mp3Player.pause();
            }
        });

        JPanel panel = new JPanel(new GridLayout(2,2));

        JLabel emptyLabel = new JLabel("------------------------------------------------------------");

        panel.add(songInfo);
        panel.add(emptyLabel);
        panel.add(playButton);
        panel.add(pauseButton);
        add(panel);

        addWindowListener(new WindowAdapter() {
            //for closing
            @Override
            public void windowClosing(WindowEvent e) {
                mp3Player.stop();
            }
        });

        setTitle("Music player!");

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);

    }

    MusicPlayer(String playlist[][]){

        MP3Player mp3Player = null;

        String display[][] = new String[1000][2];
        final int[] currentSongInfo = {0};
        final int[] lastSong = {0};

        for(String currentSong[] : playlist){

            File song = findSong(currentSong[0],currentSong[1]);

            display[currentSongInfo[0]][0] = currentSong[1];
            display[currentSongInfo[0]][1] = currentSong[0];
            currentSongInfo[0]++;

            if(mp3Player == null){
                mp3Player = new MP3Player(song);
            }
            else{
                mp3Player.addToPlayList(song);
            }

        }

        lastSong[0] = currentSongInfo[0] - 1;
        currentSongInfo[0] = 0;

        assert mp3Player != null;
        mp3Player.play();

        JLabel songInfo = new JLabel(display[currentSongInfo[0]][0] + " - " + display[currentSongInfo[0]][1]);

        JButton skipButton = new JButton("Skip");
        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");

        MP3Player finalMp3Player = mp3Player;
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(finalMp3Player.isStopped() || finalMp3Player.isPaused()){
                    finalMp3Player.play();
                }
            }
        });
        MP3Player finalMp3Player1 = mp3Player;
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalMp3Player1.pause();
            }
        });
        MP3Player finalMp3Player2 = mp3Player;
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(currentSongInfo[0] < lastSong[0]){
                    finalMp3Player2.skipForward();
                    currentSongInfo[0]++;
                    songInfo.setText(display[currentSongInfo[0]][0] + " - " + display[currentSongInfo[0]][1]);
                }
                else{
                    finalMp3Player2.stop();
                    dispose();
                }

            }
        });



        JPanel panel = new JPanel(new GridLayout(2,2));

        panel.add(songInfo);
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(skipButton);

        add(panel);

        MP3Player finalMp3Player3 = mp3Player;
        addWindowListener(new WindowAdapter() {
            //for closing
            @Override
            public void windowClosing(WindowEvent e) {
                finalMp3Player3.stop();
            }
        });

        setTitle("Music player!");

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private File findSong(String artist,String title){

        File song = null;

        try{

            String tableName = "\"Project\".\"songs\"";

            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement();

            String getLocationQuery = "select location from "+tableName+" where song_name = '{"+title+"}' and artist_name = '{"+artist+"}'";

            statement.execute(getLocationQuery);

            ResultSet resultSet = statement.getResultSet();
            resultSet.next();

            String location = resultSet.getString("location");

            location = location.replace("\"","");
            location = location.replace("{","");
            location = location.replace("}","");
            location = location.replace("\\\\","\\");

            //System.out.println(location);

            song = new File(location);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return song;

    }

}
