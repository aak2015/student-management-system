import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentDatabaseGUI extends JFrame {
    private String userRole;
    private int studentID;

    public StudentDatabaseGUI(String accessLevel, int studentID) {
        this.userRole = accessLevel;
        this.studentID = studentID;
        setTitle("Student Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Student Management", createStudentPanel());
        tabbedPane.addTab("Course Management", createCoursePanel());
        tabbedPane.addTab("Enrollment & Grades Management", createEnrollmentGradePanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        applyUserRoleRestrictions(tabbedPane);
        add(tabbedPane);
    }

    private void applyUserRoleRestrictions(JTabbedPane tabbedPane) {
        if(userRole.equalsIgnoreCase("STUDENT")){
            tabbedPane.setEnabledAt(0, false);
            tabbedPane.setEnabledAt(1, false);
            tabbedPane.setEnabledAt(2, false);
        }else if(userRole.equalsIgnoreCase("INSTRUCTOR")){
            tabbedPane.setEnabledAt(0, false);
        }
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));

        JTextField studentIdField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField majorField = new JTextField();
        JTextField searchField = new JTextField();

        inputPanel.add(new JLabel("Student ID:"));
        inputPanel.add(studentIdField);
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Major:"));
        inputPanel.add(majorField);
        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(searchField);

        String[] columns = {"StudentID", "First Name", "Last Name", "Email", "Major"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                studentIdField.setText(model.getValueAt(selectedRow, 0).toString());
                studentIdField.setEditable(false);
                firstNameField.setText(model.getValueAt(selectedRow, 1).toString());
                lastNameField.setText(model.getValueAt(selectedRow, 2).toString());
                emailField.setText(model.getValueAt(selectedRow, 3).toString());
                majorField.setText(model.getValueAt(selectedRow, 4).toString());
            }
        });

        loadStudents(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add Student");
        addBtn.addActionListener(e -> {
            int StudentID = (Integer.parseInt(studentIdField.getText()));
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String major = majorField.getText();

            model.addRow(new Object[]{StudentID, firstName, lastName, email, major});

            String SQL = "INSERT INTO Student (StudentID, FirstName, LastName, Email, Major) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection()){
                PreparedStatement statement = conn.prepareStatement(SQL);
                statement.setInt(1, StudentID);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, email);
                statement.setString(5, major);
                statement.executeUpdate();
            }catch(Exception ex){
                ex.printStackTrace();
            }

            studentIdField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            majorField.setText("");
        });

        buttonPanel.add(addBtn);

        JButton updateButton = new JButton("Update Student");
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                
                int studentId = (int) model.getValueAt(selectedRow,0);
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String email = emailField.getText();
                String major = majorField.getText();

                model.setValueAt(studentId, selectedRow, 0);
                model.setValueAt(firstName, selectedRow, 1);
                model.setValueAt(lastName, selectedRow, 2);
                model.setValueAt(email, selectedRow, 3);
                model.setValueAt(major, selectedRow, 4);

                String SQL = "UPDATE Student SET FirstName=?, LastName=?, Email=?, Major=? WHERE StudentID=?";
                
                try (Connection conn = DatabaseConnection.getConnection()){
                    PreparedStatement statement = conn.prepareStatement(SQL);
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, major);
                    statement.setInt(5, studentId);
                    statement.executeUpdate();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }

            studentIdField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            majorField.setText("");
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Student");
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int studentId = (int) model.getValueAt(selectedRow, 0);
                model.removeRow(selectedRow);

                String SQL = "DELETE FROM Student WHERE StudentID=?";
                
                try (Connection conn = DatabaseConnection.getConnection()){
                    PreparedStatement statement = conn.prepareStatement(SQL);
                    statement.setInt(1, studentId);
                    statement.executeUpdate();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }

            studentIdField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            majorField.setText("");
        });
        buttonPanel.add(deleteButton);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String key = searchField.getText().toLowerCase();
            if(key.isEmpty()){
                loadStudents(model);
                return;
            }
            String SQL = "SELECT StudentID, FirstName, LastName, Email, Major FROM Student WHERE LOWER(FirstName) LIKE ? OR LOWER(LastName) LIKE ? OR LOWER(Email) LIKE ? OR LOWER(Major) LIKE ?";
            try (Connection conn = DatabaseConnection.getConnection()){
                PreparedStatement statement = conn.prepareStatement(SQL);
                String searchKey = "%" + key + "%";
                statement.setString(1, searchKey);
                statement.setString(2, searchKey);
                statement.setString(3, searchKey);
                statement.setString(4, searchKey);
                var rs = statement.executeQuery();
                model.setRowCount(0); 
                while(rs.next()){
                    model.addRow(new Object[]{
                        rs.getInt("StudentID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Major")
                    });
                }
            }catch(Exception ex){
                ex.printStackTrace();            
            }

            studentIdField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            majorField.setText("");
        });
        buttonPanel.add(searchButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStudents(DefaultTableModel model) {
        try(Connection conn = DatabaseConnection.getConnection()){
            String SQL = "SELECT StudentID, FirstName, LastName, Email, Major FROM Student";
            PreparedStatement statement = conn.prepareStatement(SQL);
            var rs = statement.executeQuery();
            model.setRowCount(0); 
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("StudentID"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getString("Email"),
                    rs.getString("Major")
                });
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }   
    }

    private void loadCourses(DefaultTableModel model) {
        try(Connection conn = DatabaseConnection.getConnection()){
            String SQL = "SELECT CourseID, CourseName, Department, Credits FROM Courses";
            PreparedStatement statement = conn.prepareStatement(SQL);
            var rs = statement.executeQuery();
            model.setRowCount(0); 
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("CourseID"),
                    rs.getString("CourseName"),
                    rs.getString("Department"),
                    rs.getInt("Credits"),
                });
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));

        JTextField courseIdField = new JTextField();
        JTextField courseNameField = new JTextField();
        JTextField departmentField = new JTextField();
        JTextField creditsField = new JTextField();
        JTextField instructorField = new JTextField();
        JTextField searchField = new JTextField();

        inputPanel.add(new JLabel("Course ID:"));
        inputPanel.add(courseIdField);
        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(courseNameField);
        inputPanel.add(new JLabel("Department:"));
        inputPanel.add(departmentField);
        inputPanel.add(new JLabel("Credits:"));
        inputPanel.add(creditsField);
        inputPanel.add(new JLabel("Instructor:"));
        inputPanel.add(instructorField);
        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(searchField);

        String[] columns = {"CourseID", "Course Name", "Department", "Credits"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        loadCourses(model);
        JScrollPane scrollPane = new JScrollPane(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                courseIdField.setText(model.getValueAt(row, 0).toString());
                courseIdField.setEditable(false);
                courseNameField.setText(model.getValueAt(row, 1).toString());
                departmentField.setText(model.getValueAt(row, 2).toString());
                creditsField.setText(model.getValueAt(row, 3).toString());
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add Course");
        addBtn.addActionListener(e -> {
            int id = Integer.parseInt(courseIdField.getText());
            String name = courseNameField.getText();
            String dept = departmentField.getText();
            int credits = Integer.parseInt(creditsField.getText());

            String SQL = "INSERT INTO Courses (CourseID, CourseName, Department, Credits) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection()){
                PreparedStatement statement = conn.prepareStatement(SQL);
                statement.setInt(1, id);
                statement.setString(2, name);
                statement.setString(3, dept);
                statement.setInt(4, credits);
                statement.executeUpdate();

                loadCourses(model);
            }catch(Exception ex){
                ex.printStackTrace();
            }

            courseIdField.setText("");
            courseNameField.setText("");
            departmentField.setText("");
            creditsField.setText("");
            instructorField.setText("");
        });

        buttonPanel.add(addBtn);

        JButton updateButton = new JButton("Update Course");
        updateButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = Integer.parseInt(courseIdField.getText());
                String name = courseNameField.getText();
                String dept = departmentField.getText();
                int credits = Integer.parseInt(creditsField.getText());
                String SQL = "UPDATE Courses SET CourseName=?, Department=?, Credits=? WHERE CourseID=?";
                try (Connection conn = DatabaseConnection.getConnection()){
                    PreparedStatement statement = conn.prepareStatement(SQL);
                    statement.setString(1, name);
                    statement.setString(2, dept);
                    statement.setInt(3, credits);
                    statement.setInt(4, id);
                    statement.executeUpdate();
                    loadCourses(model);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
            courseIdField.setText("");
            courseNameField.setText("");
            departmentField.setText("");
            creditsField.setText("");
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Course");
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = Integer.parseInt(courseIdField.getText());
                String SQL = "DELETE FROM Courses WHERE CourseID=?";
                try (Connection conn = DatabaseConnection.getConnection()){
                    PreparedStatement statement = conn.prepareStatement(SQL);
                    statement.setInt(1, id);
                    statement.executeUpdate();
                    loadCourses(model);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
            courseIdField.setText("");
            courseNameField.setText("");
            departmentField.setText("");
            creditsField.setText("");
        });
        buttonPanel.add(deleteButton);

        buttonPanel.add(new JButton("Search"));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadEnrollments(DefaultTableModel model) {
        try(Connection conn = DatabaseConnection.getConnection()){
            String SQL = "SELECT e.StudentID, e.CourseID, e.Semester, COALESCE(g.LetterGrade, 'IP') AS Grade " +
                         "FROM Enrollments e " +
                         "LEFT JOIN Grades g ON e.StudentID = g.StudentID AND e.CourseID = g.CourseID";
            PreparedStatement statement = conn.prepareStatement(SQL);
            var rs = statement.executeQuery();
            model.setRowCount(0); 
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("StudentID"),
                    rs.getInt("CourseID"),
                    rs.getString("Semester"),
                    rs.getString("Grade")
                });
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private JPanel createEnrollmentGradePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel enrollPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        enrollPanel.setBorder(BorderFactory.createTitledBorder("Enrollment Management"));
       
        String[] columns = {"StudentID", "CourseID", "Semester", "Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        JTextField studentIdField = new JTextField(10);
        JTextField courseIdField = new JTextField(10);
        JTextField semesterField = new JTextField(10);

        table.getSelectionModel().addListSelectionListener(e -> {
        int row = table.getSelectedRow();
            if (row != -1) {
                studentIdField.setText(model.getValueAt(row, 0).toString());
                courseIdField.setText(model.getValueAt(row, 1).toString());
                semesterField.setText(model.getValueAt(row, 2).toString());
            }
        });
       

        loadEnrollments(model);
        enrollPanel.add(new JLabel("Student ID:"));
        enrollPanel.add(studentIdField);
        enrollPanel.add(new JLabel("Course ID:"));
        enrollPanel.add(courseIdField);
        enrollPanel.add(new JLabel("Semester:"));
        enrollPanel.add(semesterField);

        JButton enrollButton = new JButton("Enroll");
        enrollButton.addActionListener(e -> {
            int studentID = Integer.parseInt(studentIdField.getText());
            int courseID = Integer.parseInt(courseIdField.getText());
            String semester = semesterField.getText();

            String SQL = "INSERT INTO Enrollments (StudentID, CourseID, Semester) VALUES (?, ?, ?)";

            try(Connection conn = DatabaseConnection.getConnection()){
                PreparedStatement statement = conn.prepareStatement(SQL);
                statement.setInt(1, studentID);
                statement.setInt(2, courseID);
                statement.setString(3, semester);
                statement.executeUpdate();
            }catch(Exception ex){
                ex.printStackTrace();
            }

            studentIdField.setText("");
            courseIdField.setText("");
            semesterField.setText("");

            loadEnrollments(model);

        });
        enrollPanel.add(enrollButton);

        JButton dropButton = new JButton("Drop");
        dropButton.addActionListener(e -> {
            int studentID = Integer.parseInt(studentIdField.getText());
            int courseID = Integer.parseInt(courseIdField.getText());
            String SQL = "DELETE FROM Enrollments WHERE StudentID=? AND CourseID=?";
            try(Connection conn = DatabaseConnection.getConnection()){
                PreparedStatement statement = conn.prepareStatement(SQL);
                statement.setInt(1, studentID);
                statement.setInt(2, courseID);
                statement.executeUpdate();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            studentIdField.setText("");
            courseIdField.setText("");

            loadEnrollments(model);
        });
        enrollPanel.add(dropButton);

        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gradePanel.setBorder(BorderFactory.createTitledBorder("Grade Management"));

        JTextField gStudentIdField = new JTextField(10);
        JTextField gCourseIdField = new JTextField(10);
        JComboBox<String> gradeBox = new JComboBox<>(new String[]{"A", "B", "C", "D", "F"});

        gradePanel.add(new JLabel("Student ID:"));
        gradePanel.add(gStudentIdField);
        gradePanel.add(new JLabel("Course ID:"));
        gradePanel.add(gCourseIdField);
        gradePanel.add(new JLabel("Letter Grade:"));
        gradePanel.add(gradeBox);

        JButton assignGradeButton = new JButton("Assign/Update Grade");
        assignGradeButton.addActionListener(e -> {
            int studentID = Integer.parseInt(gStudentIdField.getText());
            int courseID = Integer.parseInt(gCourseIdField.getText());
            String grade = (String) gradeBox.getSelectedItem();

            String SQL = "INSERT INTO Grades (StudentID, CourseID, LetterGrade) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE LetterGrade=?";

            try(Connection conn = DatabaseConnection.getConnection()){
                PreparedStatement statement = conn.prepareStatement(SQL);
                statement.setInt(1, studentID);
                statement.setInt(2, courseID);
                statement.setString(3, grade);
                statement.setString(4, grade);
                statement.executeUpdate();
            }catch(Exception ex){
                ex.printStackTrace();
            }

            gStudentIdField.setText("");
            gCourseIdField.setText("");

            loadEnrollments(model);
        });
        gradePanel.add(assignGradeButton);

        JScrollPane scrollPane = new JScrollPane(table);

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

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        JButton transcriptButton = new JButton("Create Student Transcript Report");
        transcriptButton.addActionListener(e -> {
            String SQL = "SELECT s.StudentID, s.FirstName, s.LastName, c.courseName, g.letterGrade " +
                            "FROM Student s " +
                            "JOIN Grades g ON s.StudentID = g.StudentID " +
                            "JOIN Courses c ON g.CourseID = c.CourseID " +
                            "WHERE s.StudentID = ?";
            try(Connection conn = DatabaseConnection.getConnection()){
                PreparedStatement statement = conn.prepareStatement(SQL);
                statement.setInt(1, studentID);
                var rs = statement.executeQuery();
                StringBuilder report = new StringBuilder();
                while(rs.next()){
                    report.append("Student ID: ").append(rs.getInt("StudentID")).append("\n");                    report.append("Name: ").append(rs.getString("FirstName")).append(" ").append(rs.getString("LastName")).append("\n");
                    report.append("Course: ").append(rs.getString("courseName")).append("\n");
                    report.append("Grade: ").append(rs.getString("letterGrade")).append("\n");
                    report.append("-----------------------------\n");
                }
                reportArea.setText(report.toString());
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        buttonPanel.add(transcriptButton);

        JButton enrollment = new JButton("Create Course Enrollment Report");
        enrollment.addActionListener(e -> {
            String SQL = "SELECT c.CourseName, s.StudentID, s.FirstName, s.LastName " +
                            "FROM Enrollments e " +
                            "JOIN Student s ON e.StudentID = s.StudentID " +
                            "JOIN Courses c ON e.CourseID = c.CourseID " +
                            "ORDER BY c.CourseName";
            try(Connection conn = DatabaseConnection.getConnection()){
                PreparedStatement statement = conn.prepareStatement(SQL);
                var rs = statement.executeQuery();
                StringBuilder report = new StringBuilder();
                while(rs.next()){
                    report.append("Course: ").append(rs.getString("CourseName")).append("\n");
                    report.append("Student ID: ").append(rs.getInt("StudentID")).append("\n");
                    report.append("Name: ").append(rs.getString("FirstName")).append(" ").append("\n");
                    report.append("-----------------------------\n");
                }
                reportArea.setText(report.toString());
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
        buttonPanel.add(enrollment);

        buttonPanel.add(new JButton("Instructor Performance Report"));


        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentDatabaseGUI("ADMIN", -1).setVisible(true);
        });
    }
}