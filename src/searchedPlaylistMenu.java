import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class searchedPlaylistMenu extends JFrame {

    searchedPlaylistMenu(String email){

        //this will be a jtable with the result
        //if you click on a row it will open another jtable with the playlist and the songs in it

        JTable playlistsTable = null;

        playlistsTable = createTableWithPlaylists(email);

        JTable finalPlaylistsTable = playlistsTable;

        playlistsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                String playlist_name = finalPlaylistsTable.getValueAt(finalPlaylistsTable.getSelectedRow(),1).toString();

                //System.out.println(email + " " + playlist_name);

                JTable results = playlistContent(email,playlist_name);

                JFrame contentOfPlaylist = new JFrame();

                final boolean[] executed = {false};

                if(results!=null){

                    results.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {

                            int nrOfRows = results.getRowCount();
                            int selectedRow = results.getSelectedRow();

                            String nextSongs[][] = new String[1][results.getColumnCount()];

                            nextSongs[0][0] = results.getValueAt(selectedRow,0).toString();
                            nextSongs[0][1] = results.getValueAt(selectedRow,1).toString();

                            int size = 1;

                            for(int pos = selectedRow + 1; pos < nrOfRows ;pos++,size++){
                                String newData[][] = new String[size + 1][results.getColumnCount()];

                                for(int i = 0; i < pos - selectedRow; i++){
                                    newData[i] = nextSongs[i];
                                }

                                newData[pos - selectedRow][0] = results.getValueAt(pos,0).toString();
                                newData[pos - selectedRow][1] = results.getValueAt(pos,1).toString();

                                nextSongs = newData;

                            }

                            /*if(!executed[0]) {
                                executed[0] = true;
                                new MusicPlayer(nextSongs);
                            }*/

                            new MusicPlayer(nextSongs);

                        }
                    });

                    JScrollPane pane = new JScrollPane(results);

                    contentOfPlaylist.setTitle(playlist_name);

                    contentOfPlaylist.add(results.getTableHeader(), BorderLayout.PAGE_START);

                    contentOfPlaylist.add(pane);

                    Dimension maxLoginSize = new Dimension(500,200);
                    contentOfPlaylist.setSize(maxLoginSize);
                    contentOfPlaylist.setMaximumSize(maxLoginSize);
                    contentOfPlaylist.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
                    contentOfPlaylist.setIconImage(icon.getImage());
                    contentOfPlaylist.setLocationRelativeTo(null);
                    contentOfPlaylist.setVisible(true);

                    dispose();
                }

            }
        });

        add(playlistsTable.getTableHeader(), BorderLayout.PAGE_START);

        JScrollPane pane = new JScrollPane(playlistsTable);

        add(pane);

        setTitle("Your Playlists!");

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);

    }

    searchedPlaylistMenu(String email,String title, String artist){

        //this will be a jtable with the result
        //if you click on a row it will open another jtable with the playlist and the songs in it

        JTable playlistsTable = null;

        playlistsTable = createTableWithPlaylists(email);

        JTable finalPlaylistsTable = playlistsTable;

        playlistsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {

                String playlist_name = finalPlaylistsTable.getValueAt(finalPlaylistsTable.getSelectedRow(),1).toString();

                //System.out.println(email + " " + playlist_name);

                String tableName = "\"Project\".\"playlists_content\"";

                String insertSongInPlaylistQuery = "insert into "+tableName+" values(DEFAULT,'{"+email+"}','{"+playlist_name+"}','{"+title+"}','{"+artist+"}');";

                String url = "jdbc:postgresql://localhost:5432/Project";
                String user = "postgres";
                String pass = "admin";

                Connection connection = null;
                try {
                    connection = DriverManager.getConnection(url, user, pass);
                    Statement statement = connection.createStatement();

                    statement.execute(insertSongInPlaylistQuery);

                    System.out.println("Song added!");

                    dispose();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        add(playlistsTable.getTableHeader(), BorderLayout.PAGE_START);

        JScrollPane pane = new JScrollPane(playlistsTable);

        add(pane);

        setTitle("Your Playlists!");

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);

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

    private JTable playlistContent(String email, String playlist_name){

        JTable results = null;

        try {

            String tableName = "\"Project\".\"playlists_content\"";

            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            String selectPlaylistQuery = "select * from "+tableName+" where email = '{"+email+"}' and playlist_name = '{"+playlist_name+"}'";

            statement.execute(selectPlaylistQuery);

            ResultSet resultSet = statement.getResultSet();

            if(!resultSet.next()){
                throw new ExceptionNoExistingPlaylists("Empty Playlist!");
            }

            int nrOfColumns = resultSet.getMetaData().getColumnCount();
            String data[][] = new String[1][nrOfColumns];

            String replacement = resultSet.getString("artist").replace("{", "");
            replacement = replacement.replace("}", "");

            data[0][0] = replacement;

            replacement = resultSet.getString("title").replace("{", "");
            replacement = replacement.replace("}", "");
            replacement = replacement.replace("\"", "");
            data[0][1] = replacement;

            int rows = 1;
            while (resultSet.next()) {

                String newData[][] = new String[data.length + 1][nrOfColumns];

                for (int i = 0; i < data.length; i++) {
                    newData[i] = data[i];
                }

                replacement = resultSet.getString("artist").replace("{", "");
                replacement = replacement.replace("}", "");

                newData[rows][0] = replacement;

                replacement = resultSet.getString("title").replace("{", "");
                replacement = replacement.replace("}", "");
                replacement = replacement.replace("\"", "");

                newData[rows][1] = replacement;

                data = newData;

                rows++;

            }

            String columns[] = {"Artist", "Title"};

            results = new JTable(data, columns);

            //this will make the cells of the table not editable
            DefaultTableModel tableModel = new DefaultTableModel(data, columns) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        } catch (ExceptionNoExistingPlaylists e) {
            JOptionPane.showMessageDialog(new JFrame(),"Empty playlist!","Empty playlist!",JOptionPane.WARNING_MESSAGE);
        }

        return results;

    }

}
