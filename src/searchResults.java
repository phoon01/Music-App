import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.EventObject;

public class searchResults extends JFrame {

    searchResults(ResultSet resultSet, String email) throws SQLException {

        int nrOfColumns = resultSet.getMetaData().getColumnCount();
        String data[][] = new String[1][nrOfColumns];

        String replacement = resultSet.getString("artist_name").replace("{","");
        replacement = replacement.replace("}","");

        data[0][0] = replacement;

        replacement = resultSet.getString("song_name").replace("{","");
        replacement = replacement.replace("}","");
        replacement = replacement.replace("\"","");
        data[0][1] = replacement;

        int rows = 1;
        while(resultSet.next()){

            String newData[][] = new String[data.length+1][nrOfColumns];

            for(int i=0;i<data.length;i++){
                newData[i] = data[i];
            }

            replacement = resultSet.getString("artist_name").replace("{","");
            replacement = replacement.replace("}","");

            newData[rows][0] = replacement;

            replacement = resultSet.getString("song_name").replace("{","");
            replacement = replacement.replace("}","");
            replacement = replacement.replace("\"","");

            newData[rows][1] = replacement;

            data = newData;

            rows++;

        }

        String columns[] = {"Artist", "Title"};

        JTable searchResultTable = new JTable(data,columns);

        //this will make the cells of the table not editable
        DefaultTableModel tableModel = new DefaultTableModel(data, columns) {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        searchResultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                // do some actions here, for example
                // print first column value from selected row

                String artist = searchResultTable.getValueAt(searchResultTable.getSelectedRow(), 0).toString();
                String title = searchResultTable.getValueAt(searchResultTable.getSelectedRow(), 1).toString();

                JFrame actionOption = new JFrame();

                Dimension maxLoginSize = new Dimension(300,100);
                actionOption.setSize(maxLoginSize);
                actionOption.setMaximumSize(maxLoginSize);
                actionOption.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
                actionOption.setIconImage(icon.getImage());
                actionOption.setLocationRelativeTo(null);
                actionOption.setVisible(false);

                Object options[] = {"Play song!","Add to playlist!"};

                int option = JOptionPane.showOptionDialog(actionOption,
                        "What do you want to do?","What to do?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if(option == JOptionPane.YES_OPTION){
                    //System.out.println("Playing!");
                    new MusicPlayer(artist,title);
                }
                else if(option == JOptionPane.NO_OPTION){

                    new searchedPlaylistMenu(email,title,artist);

                }

            }
        });

        searchResultTable.setModel(tableModel);

        searchResultTable.setFont(new Font(searchResultTable.getFont().getFontName(),Font.PLAIN,15));

        add(searchResultTable.getTableHeader(), BorderLayout.PAGE_START);

        JScrollPane pane = new JScrollPane(searchResultTable);

        add(pane);

        setTitle("Search Results!");

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