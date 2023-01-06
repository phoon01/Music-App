import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class Signup extends JFrame {

    private final ImageIcon smile_face = new ImageIcon("Image/smile_face.png");

    Signup(){

        JTextField nameField = new JTextField(100);
        JLabel nameLabel = new JLabel("Name(this will be visible to people):");

        JTextField emailField = new JTextField(50);
        JLabel emailLabel = new JLabel("Email:");

        JPasswordField passwordField = new JPasswordField(50);
        JLabel passwordLabel = new JLabel("Password:");

        JPasswordField passwordConfirmField = new JPasswordField(50);
        JLabel passwordConfirmLabel = new JLabel("Confirm password:");

        JButton signupButton = new JButton("Sign-Up");

        signupButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //we need to check if all the fields are filled out
                //check if the email is ok
                //check if the passwords match

                String name = nameField.getText();
                String email = emailField.getText();
                String password = String.valueOf(passwordField.getPassword());
                String passwordConfirm = String.valueOf(passwordConfirmField.getPassword());

                checkCredentials(name,email,password,passwordConfirm);

            }
        });

        JButton backToLoginButton = new JButton("Back to Login");

        backToLoginButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true);
                dispose();

            }
        });

        JPanel panel = new JPanel(new GridLayout(5,1));

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(passwordConfirmLabel);
        panel.add(passwordConfirmField);

        panel.add(signupButton);
        panel.add(backToLoginButton);

        add(panel);

        setTitle("Spotify(fake)");

        Dimension maxLoginSize = new Dimension(500,200);
        setSize(maxLoginSize);
        setMaximumSize(maxLoginSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("Image/spotify_logo.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void checkCredentials(String name,String email,String password,String passwordConfirm){

        try {
            //CHECK IF THE FIELDS ARE EMPTY
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                throw new ExceptionMissingDetail("Missing Details");
            }

            if(name.length()>100 || email.length()>50 || password.length()>100){
                throw new ExceptionDetailsTooLong("Details too long!");
            }

            //CHECK IF THERE IS AN EMAIL REGISTERED ALREADY
            String url = "jdbc:postgresql://localhost:5432/Project";
            String user = "postgres";
            String pass = "admin";

            String tableName = "\"Project\".\"account\"";

            Connection connection = DriverManager.getConnection(url,user,pass);
            Statement statement = connection.createStatement();

            String checkEmailQuery = "select \"email\" from "+tableName+" where email = '{"+email+"}'";
            statement.execute(checkEmailQuery);

            ResultSet resultSet = statement.getResultSet();

            if(resultSet.next()) {
                throw new ExceptionExistingEmail("Existing Email!");
            }
            else {

                if(!checkEmail(email)){

                    throw new ExceptionInvalidEmail("Email invalid!");

                }

                if(!password.equals(passwordConfirm)){

                    throw new ExceptionPasswordMatch("Passwords dont match!");

                }

                String query = "insert into " + tableName + " values ('{"+name+"}','{"+email+"}','{"+password+"}');";

                statement.executeUpdate(query);

                JOptionPane.showMessageDialog(new JFrame(),"Account created successfully!","OK",JOptionPane.INFORMATION_MESSAGE,smile_face);

                LoginPage lp = new LoginPage();
                lp.setVisible(true);
                dispose();

            }

        }
        catch(ExceptionMissingDetail e){
            JOptionPane.showMessageDialog(new JFrame(),"Required fields are empty!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            System.out.println(e);
        } catch (ExceptionExistingEmail e) {
            JOptionPane.showMessageDialog(new JFrame(),"There is another account associated with this email!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (ExceptionInvalidEmail e) {
            JOptionPane.showMessageDialog(new JFrame(),"The email you entered is invalid! Try another email!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (ExceptionPasswordMatch e) {
            JOptionPane.showMessageDialog(new JFrame(),"Passwords do not match!","ERROR",JOptionPane.WARNING_MESSAGE);
        } catch (ExceptionDetailsTooLong e) {
            JOptionPane.showMessageDialog(new JFrame(),"One of the credentials is too long!","ERROR",JOptionPane.WARNING_MESSAGE);
        }

    }

    private boolean checkEmail(String email){

        if (!isValidEmailAddress(email)) {
            return false;
        }

        return true;

    }

    private boolean isValidEmailAddress(String email) {
        String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailPattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

}
