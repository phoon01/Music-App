import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class deletePlaylistMenu extends JFrame {

    private final ImageIcon smile_face = new ImageIcon("Image/smile_face.png");

    deletePlaylistMenu(String email){

        //this will be a jtable where it shows all of your playlists and if you click on one it will open an option pane
        //in the option pane if the option yes is clicked it will delete the playlist that was selected

        JTable deleteTable = createTableWithPlaylists(email);

        if(deleteTable != null) {
            add(deleteTable.getTableHeader(), BorderLayout.PAGE_START);
            add(deleteTable);
        }

        assert deleteTable != null;
        deleteTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row

                String playlistTitle = deleteTable.getValueAt(deleteTable.getSelectedRow(), 1).toString();

                JFrame confirmDelete = new JFrame();

                Dimension maxLoginSize = new Dimension(300,100);
                confirmDelete.setSize(maxLoginSize);
                confirmDelete.setMaximumSize(maxLoginSize);
                confirmDelete.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
                confirmDelete.setIconImage(icon.getImage());
                confirmDelete.setLocationRelativeTo(null);
                confirmDelete.setVisible(false);

                int option = JOptionPane.showConfirmDialog(confirmDelete,"Are you sure you want to delete "+playlistTitle+" playlist?");

                if(option == JOptionPane.YES_OPTION){
                    confirmDelete.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    deletePlaylist(email,playlistTitle);

                    dispose();


                }
                else{
                    confirmDelete.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                }

            }
        });

        JScrollPane pane = new JScrollPane(deleteTable);

        add(pane);

        setTitle("Select a playlist to delete!");

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void deletePlaylist(String email, String title){

        try{

            String tableName = "\"Project\".\"playlists\"";

            email = email.replace("{","");
            email = email.replace("}","");

            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            String deleteQuery = "delete from "+tableName+" where email = '{"+email+"}' and playlist_name = '{"+title+"}'";

            statement.execute(deleteQuery);

            JOptionPane.showMessageDialog(new JFrame(),"Successfully deleted!","OK",JOptionPane.INFORMATION_MESSAGE,smile_face);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private JTable createTableWithPlaylists(String email) {

        JTable results = null;

        try {
            String tableName = "\"Project\".\"playlists\"";

            email = email.replace("{","");
            email = email.replace("}","");

            String searchForPlaylistsQuery = "select * from " + tableName + " where email = '{"+email+"}'";

            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            statement.execute(searchForPlaylistsQuery);

            ResultSet resultSet = statement.getResultSet();

            int nrOfColumns = resultSet.getMetaData().getColumnCount();
            String data[][] = new String[1][nrOfColumns];
            String columns[] = {"ID","Name of the playlist"};

            results = new JTable(data,columns);


            if(!resultSet.next()){
                throw new ExceptionNoExistingPlaylists("No playlists!");
            }

            //creating the search table with the existing playlists for an account

            int rows = 1;

            String replacement = Integer.toString(rows);

            data[0][0] = replacement;

            replacement = resultSet.getString("playlist_name").replace("{","");
            replacement = replacement.replace("}","");
            replacement = replacement.replace("\"","");
            data[0][1] = replacement;

            while(resultSet.next()){

                String newData[][] = new String[data.length+1][nrOfColumns];

                for(int i=0;i<data.length;i++){
                    newData[i] = data[i];
                }

                replacement = Integer.toString(rows+1);

                newData[rows][0] = replacement;

                replacement = resultSet.getString("playlist_name").replace("{","");
                replacement = replacement.replace("}","");
                replacement = replacement.replace("\"","");

                newData[rows][1] = replacement;

                data = newData;

                rows++;

            }

            results = new JTable(data,columns);

            DefaultTableModel tableModel = new DefaultTableModel(data, columns) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };

            results.setModel(tableModel);
            results.setFont(new Font(results.getFont().getFontName(),Font.PLAIN,12));


        }
        catch (SQLException | ExceptionNoExistingPlaylists e){
            JOptionPane.showMessageDialog(new JFrame(),"No existing playlists!","ERROR",JOptionPane.WARNING_MESSAGE);
        }

        return results;

    }

}
