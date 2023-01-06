import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class AddSong extends JFrame {

    private final ImageIcon smile_face = new ImageIcon("Image/smile_face.png");
    private File song;
    private String selectedFileName;

    AddSong(String name, String email){

        JTextField songName = new JTextField(100);
        JLabel songLabel = new JLabel("Song name:");

        JButton locationButton = new JButton("Select the song!");
        JLabel locationLabel = new JLabel("Please select the song you want to upload!");

        locationButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();

                int r = fileChooser.showOpenDialog(null);

                if(r == JFileChooser.APPROVE_OPTION){

                    song = fileChooser.getSelectedFile();

                }

                if(song != null) {
                    selectedFileName = song.getName();

                    locationButton.setText(selectedFileName);
                }

            }
        });


        JButton backToMenu = new JButton("Back To Menu");

        backToMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();

            }
        });

        JButton uploadButton = new JButton("Upload");

        uploadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String title = songName.getText();

                uploadSong(title,name,song,email);

            }
        });

        JPanel panel = new JPanel(new GridLayout(3,2));

        panel.add(songLabel);
        panel.add(songName);
        panel.add(locationLabel);
        panel.add(locationButton);
        panel.add(backToMenu);
        panel.add(uploadButton);

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

    private void uploadSong(String title,String artist, File song, String email){

        try{

            if(title.isEmpty() || artist.isEmpty()){
                throw new ExceptionMissingDetail("Empty fields!");
            }

            if(title.length()>100){
                throw new ExceptionDetailsTooLong("Details too long!");
            }

            if(song == null){
                throw new ExceptionNoSlectedFile("No file selected!");
            }

            String location = song.getName();

            String fileExtension = location.substring(location.length() - 4, location.length());

            if(!fileExtension.equals(".mp3")){
                throw new ExceptionInvalidAudioFile("Wrong File!");
            }

            location = song.getAbsolutePath();

            location = location.replace("\\","\\\\");

            email = email.replace("{","");
            email = email.replace("}","");

            File newDirectory = new File(".\\Music\\" + email);

            if(newDirectory.mkdir()){
                System.out.println("ok");
            }

            String saveLocation = ".\\Music\\"+email+"\\"+title+".mp3";

            Files.copy(Path.of(location), Path.of(saveLocation),REPLACE_EXISTING);

            saveLocation = saveLocation.replace("\\","\\\\");

            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            String tableName = "\"Project\".\"songs\"";

            Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement();

            String addSongQuery = "insert into " + tableName + " values ('{"+saveLocation+"}','{"+title+"}','{"+artist+"}');";

            statement.execute(addSongQuery);

            JOptionPane.showMessageDialog(new JFrame(),"Song uploaded successfully!","OK",JOptionPane.INFORMATION_MESSAGE,smile_face);

            dispose();

        }
        catch (ExceptionMissingDetail e) {
            JOptionPane.showMessageDialog(new JFrame(),"Required fields are empty!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (ExceptionDetailsTooLong e) {
            JOptionPane.showMessageDialog(new JFrame(),"Title is too long!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            System.out.println(e);
        } catch(ExceptionInvalidAudioFile e){
            JOptionPane.showMessageDialog(new JFrame(),"Invalid audio file!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (ExceptionNoSlectedFile e) {
            JOptionPane.showMessageDialog(new JFrame(),"No file selected!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
