import java.awt.*;
import javax.swing.*;

public class StudentDatabaseGUI extends JFrame {
    private String userRole;

    public StudentDatabaseGUI(String accessLevel) {
        this.userRole = accessLevel;
        setTitle("Student Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Student Management", createStudentPanel());
        tabbedPane.addTab("Course Management", createCoursePanel());
        tabbedPane.addTab("Enrollment & Grades Management", createEnrollmentGradePanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        add(tabbedPane);
    }


    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.add(new JLabel("Student ID:"));
        JTextField studentIDInput = inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("First Name:"));
        JTextField firstNameInput = inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Last Name:"));
        JTextField lastNameInput = inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Email:"));
        JTextField emailInput = inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Major:"));
        JTextField majorInput = inputPanel.add(new JTextField());
        //inputPanel.add(new JLabel("Search:"));
        //inputPanel.add(new JTextField());//does search need a text field? i thought it would be a button //just uses the normal text fields for searching

        String[] columns = {"StudentID", "First Name", "Last Name", "Email", "Major"};
        Object[][] data = {{"123456", "Example", "Example", "example@example.com", "Example Major"}};
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        
        //TODO: add functionality to Student buttons //ideally it is successfully calling queries, but results need to be pushed back to the GUI

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = buttonPanel.add(new JButton("Add Student"));
        JButton updateButton = buttonPanel.add(new JButton("Update Student"));
        JButton deleteButton = buttonPanel.add(new JButton("Delete Student"));
        JButton searchButton = buttonPanel.add(new JButton("Search"));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = replaceIfEmpty(studentIDInput.getText(), "NULL");
                String firstName = replaceIfEmpty(firstNameInput.getText(), "NULL");
                String lastName = replaceIfEmpty(lastNameInput.getText(), "NULL");
                String email = replaceIfEmpty(emailInput.getText(), "NULL");
                String major = replaceIfEmpty(majorInput.getText(), "NULL");

                String query = "INSERT INTO Student(StudentID, FirstName, LastName, Email, Major) VALUES (?, ?, ?, ?, ?);";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, studentID);
                    stmt.setString(2, firstName);
                    stmt.setString(3, lastName);
                    stmt.setString(4, email);
                    stmt.setString(5, major);
                    ResultSet rs = stmt.executeQuery();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDatabaseGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = studentIDInput.getText();
                String firstName = replaceIfEmpty(firstNameInput.getText(), "*");
                String lastName = replaceIfEmpty(lastNameInput.getText(), "*");
                String email = replaceIfEmpty(emailInput.getText(), "*");
                String major = replaceIfEmpty(majorInput.getText(), "*");

                String query = "UPDATE Student SET Major = ?, FirstName = ?, LastName = ?, Email = ? WHERE StudentID = ?;"; 
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(5, studentID);
                    stmt.setString(2, firstName);
                    stmt.setString(3, lastName);
                    stmt.setString(4, email);
                    stmt.setString(1, major);
                    ResultSet rs = stmt.executeQuery();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDatabaseGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = replaceIfEmpty(studentIDInput.getText(), "*");
                String firstName = replaceIfEmpty(firstNameInput.getText(), "*");
                String lastName = replaceIfEmpty(lastNameInput.getText(), "*");
                String email = replaceIfEmpty(emailInput.getText(), "*");
                String major = replaceIfEmpty(majorInput.getText(), "*");

                String query = "DELETE FROM Student WHERE studentID = ?, Major = ?, FirstName = ?, LastName = ?, Email = ?;";  //some kind of confirm is a good idea, this will wipe the database if clicked with empty fields.
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, studentID);
                    stmt.setString(2, firstName);
                    stmt.setString(3, lastName);
                    stmt.setString(4, email);
                    stmt.setString(5, major);
                    ResultSet rs = stmt.executeQuery();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDatabaseGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentID = replaceIfEmpty(studentIDInput.getText(), "*");
                String firstName = replaceIfEmpty(firstNameInput.getText(), "*");
                String lastName = replaceIfEmpty(lastNameInput.getText(), "*");
                String email = replaceIfEmpty(emailInput.getText(), "*");
                String major = replaceIfEmpty(majorInput.getText(), "*");

                String query = "SELECT StudentID, FirstName, LastName, Email, Major FROM Student WHERE studentID = ?, Major = ?, FirstName = ?, LastName = ?, Email = ? ORDER BY studentID;";  //i don't know how to hook the results of the query to the results page.
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, studentID);
                    stmt.setString(2, firstName);
                    stmt.setString(3, lastName);
                    stmt.setString(4, email);
                    stmt.setString(5, major);
                    ResultSet rs = stmt.executeQuery();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDatabaseGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
        
    }

    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.add(new JLabel("Course ID:"));
        JTextField courseIDInput = inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Course Name:"));
        JTextField nameInput = inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Department:"));
        JTextField departmentInput = inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Credits:"));
        JTextField creditsInput = inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Instructor:"));
        JTestField instructorInput = inputPanel.add(new JTextField());
        //inputPanel.add(new JLabel("Search:"));
        //inputPanel.add(new JTextField());

        String[] columns = {"CourseID", "Course Name", "Department", "Credits", "Instructor"};
        Object[][] data = {{"CSEX123", "Example Class", "Example Department", "3", "Prof. Examples"}};
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        
        //TODO: add functionality to Course buttons

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = buttonPanel.add(new JButton("Add Course"));
        JButton updateButton = buttonPanel.add(new JButton("Update Course"));
        JButton deleteButton = buttonPanel.add(new JButton("Delete Course"));
        JButton searchButton = buttonPanel.add(new JButton("Search"));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseID = replaceIfEmpty(courseIDInput.getText(), "NULL");
                String name = replaceIfEmpty(nameInput.getText(), "NULL");
                String department = replaceIfEmpty(departmentInput.getText(), "NULL");
                String credits = replaceIfEmpty(creditsInput.getText(), "NULL");
                String instructor = replaceIfEmpty(instructorInput.getText(), "NULL");

                String query = "INSERT INTO Courses(CourseID, CourseName, Department, Credits, Instructor) VALUES (?, ?, ?, ?, ?);";//mismatch between the graph and the code, likely to cause an error.
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, courseID);
                    stmt.setString(2, name);
                    stmt.setString(3, department);
                    stmt.setString(4, credits);
                    stmt.setString(5, instructor);
                    ResultSet rs = stmt.executeQuery();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDatabaseGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseID = courseIDInput.getText()
                String name = replaceIfEmpty(nameInput.getText(), "*");
                String department = replaceIfEmpty(departmentInput.getText(), "*");
                String credits = replaceIfEmpty(creditsInput.getText(), "*");
                String instructor = replaceIfEmpty(instructorInput.getText(), "*");

                String query = "UPDATE Courses SET CourseName = ?, Department = ?, Credits = ?, Instructor = ? WHERE CourseID = ?;"; 
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(5, courseID);
                    stmt.setString(2, name);
                    stmt.setString(3, department);
                    stmt.setString(4, credits);
                    stmt.setString(1, instructor);
                    ResultSet rs = stmt.executeQuery();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDatabaseGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseID = replaceIfEmpty(courseIDInput.getText(), "*");
                String name = replaceIfEmpty(nameInput.getText(), "*");
                String department = replaceIfEmpty(departmentInput.getText(), "*");
                String credits = replaceIfEmpty(creditsInput.getText(), "*");
                String instructor = replaceIfEmpty(instructorInput.getText(), "*");

                String query = "DELETE FROM Coursese WHERE CourseID = ? CourseName = ?, Department = ?, Credits = ?, Instructor = ?";  //some kind of confirm is a good idea, this will wipe the database if clicked with empty fields.
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, courseID);
                    stmt.setString(2, name);
                    stmt.setString(3, department);
                    stmt.setString(4, credits);
                    stmt.setString(5, instructor);
                    ResultSet rs = stmt.executeQuery();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDatabaseGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseID = replaceIfEmpty(courseIDInput.getText(), "*");
                String name = replaceIfEmpty(nameInput.getText(), "*");
                String department = replaceIfEmpty(departmentInput.getText(), "*");
                String credits = replaceIfEmpty(creditsInput.getText(), "*");
                String instructor = replaceIfEmpty(instructorInput.getText(), "*");

                String query = "SELECT CourseID, CourseName, Department, Credits, Instructor FROM Courses WHERE CourseID = ? CourseName = ?, Department = ?, Credits = ?, Instructor = ?;";  //i don't know how to hook the results of the query to the results page.
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, courseID);
                    stmt.setString(2, name);
                    stmt.setString(3, department);
                    stmt.setString(4, credits);
                    stmt.setString(5, instructor);
                    ResultSet rs = stmt.executeQuery();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDatabaseGUI.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel createEnrollmentGradePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel enrollPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enrollPanel.setBorder(BorderFactory.createTitledBorder("Enrollment Management"));
        enrollPanel.add(new JLabel("Student ID:"));
        enrollPanel.add(new JTextField(10));
        enrollPanel.add(new JLabel("Course ID:"));
        enrollPanel.add(new JTextField(10));
        enrollPanel.add(new JLabel("Semester:"));
        enrollPanel.add(new JTextField(10));
        enrollPanel.add(new JButton("Enroll"));
        enrollPanel.add(new JButton("Drop"));

        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gradePanel.setBorder(BorderFactory.createTitledBorder("Grade Management"));
        gradePanel.add(new JLabel("Student ID:"));
        gradePanel.add(new JTextField(10));
        gradePanel.add(new JLabel("Course ID:"));
        gradePanel.add(new JTextField(10));
        gradePanel.add(new JLabel("Letter Grade:"));
        String[] grades = {"A", "B", "C", "D", "F"};
        gradePanel.add(new JComboBox<>(grades));
        gradePanel.add(new JButton("Assign/Update Grade"));

        JTextArea viewArea = new JTextArea(15, 50);
        viewArea.setEditable(false);
        viewArea.setText("Select Enrollment or Grade Actions: ");
        JScrollPane scrollPane = new JScrollPane(viewArea);

        JPanel topContainer = new JPanel(new GridLayout(2, 1));
        topContainer.add(enrollPanel);
        topContainer.add(gradePanel);

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.add(new JButton("Create Student Transcript Report"));
        buttonPanel.add(new JButton("Create Course Enrollment Report"));
        buttonPanel.add(new JButton("Instructor Performance Report"));

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setText("Reports here");
        JScrollPane scrollPane = new JScrollPane(reportArea);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentDatabaseGUI gui = new StudentDatabaseGUI();
            gui.setVisible(true);
        });
    }

    private static String replaceIfEmpty(String input, String replacement)
    {
        if(input.equals("")){return replacement;}
        return input;
    }
        
}
