import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class createPlaylistMenu extends JFrame {

    private final ImageIcon smile_face = new ImageIcon("Image/smile_face.png");

    createPlaylistMenu(String email) {
        //this will be a menu similar with upload song menu

        JLabel nameLabel = new JLabel("Please name your playlist:");

        JTextField nameField = new JTextField();

        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();

            }
        });

        JButton createButton = new JButton("Create playlist");

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String title = nameField.getText();
                createPlaylist(title, email);

            }
        });

        JPanel panel = new JPanel(new GridLayout(2,2));

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(cancelButton);
        panel.add(createButton);

        add(panel);

        setTitle("Create a Playlist!");

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createPlaylist(String title, String email){

        Connection connection = null;
        Savepoint savepoint = null;

        try {

            if(title == null || title.equals("")){
                throw new ExceptionMissingDetail("No title!");
            }

            email = email.replace("{", "");
            email = email.replace("}", "");

            String tableName = "\"Project\".\"playlists\"";

            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            String searchForTitleQuery = "select * from "+ tableName +" where playlist_name = '{"+title+"}' and email = '{"+email+"}'";

            statement.execute(searchForTitleQuery);

            ResultSet resultSet = statement.getResultSet();

            if(resultSet.next()){
                throw new ExceptionNameExists("Name already exists!");
            }

            String insertPlaylist = "insert into "+tableName+" values(DEFAULT,'{"+email+"}','{"+title+"}');";

            statement.execute(insertPlaylist);


            JOptionPane.showMessageDialog(new JFrame(),"Playlist created successfully!","OK",JOptionPane.INFORMATION_MESSAGE,smile_face);

            dispose();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ExceptionMissingDetail e) {
            JOptionPane.showMessageDialog(new JFrame(),"The title of the playlist can't be empty!","Missing title!",JOptionPane.WARNING_MESSAGE);
        } catch (ExceptionNameExists e) {
            JOptionPane.showMessageDialog(new JFrame(),"There is a playlist with this name already!","Existing title!",JOptionPane.WARNING_MESSAGE);
        }

    }

}
