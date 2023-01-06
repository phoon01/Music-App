import javax.sound.midi.Soundbank;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage extends JFrame {

    LoginPage(){

        JTextField emailField = new JTextField(50);
        JLabel emailLabel = new JLabel("Email:");

        JPasswordField passwordField = new JPasswordField(50);
        JLabel passwordLabel = new JLabel("Password:");

        JButton loginButton = new JButton("LOGIN");
        //login button clicked
        loginButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());

                checkLogin(email,password);

            }
        });

        JButton signupButton = new JButton("SIGN-UP");
        //signup button clicked
        signupButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Signup signup = new Signup();
                signup.setVisible(true);
                setVisible(false);

            }
        });

        JPanel panel = new JPanel(new GridLayout(3,1));
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(signupButton);
        panel.add(loginButton);

        add(panel,BorderLayout.CENTER);

        setTitle("Spotify(fake)");

        Dimension maxLoginSize = new Dimension(500,150);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void checkLogin(String email,String password){

        try {
            if (email.isEmpty() || password.isEmpty()) {
                throw new ExceptionMissingDetail("Missing details!");
            }

            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            String tableName = "\"Project\".\"account\"";

            Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement();

            String getEmailAndPasswordQuery = "select \"email\",\"password\",\"name\" from "+tableName+" where email = '{"+email+"}'";
            statement.execute(getEmailAndPasswordQuery);

            ResultSet resultSet = statement.getResultSet();

            if(!resultSet.next()){
                throw new ExceptionInvalidEmail("Invalid email!");
            }

            String checkPassword = "{" + password + "}";
            if(!resultSet.getString("password").contains(checkPassword) ||
                    resultSet.getString("password").length() != checkPassword.length()){
                throw new ExceptionIncorrectPassword("Wrong password!");
            }

            Menu menu = new Menu(resultSet.getString("name"),resultSet.getString("email"),resultSet.getString("password") );
            menu.setVisible(true);
            dispose();


        }
        catch(ExceptionMissingDetail e){
            JOptionPane.showMessageDialog(new JFrame(),"Required fields are empty!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            System.out.println(e);
        } catch (ExceptionInvalidEmail e) {
            JOptionPane.showMessageDialog(new JFrame(),"No account associated with the email address!","Wrong Email!",JOptionPane.WARNING_MESSAGE);
        } catch (ExceptionIncorrectPassword e) {
            JOptionPane.showMessageDialog(new JFrame(),"The password is incorrect!!","Wrong Password!",JOptionPane.WARNING_MESSAGE);
        }
    }

}
