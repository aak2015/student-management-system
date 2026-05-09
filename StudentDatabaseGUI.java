import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        if (userRole.equalsIgnoreCase("STUDENT")) {
            tabbedPane.setEnabledAt(0, false);
            tabbedPane.setEnabledAt(1, false);
            tabbedPane.setSelectedIndex(2);
        } else if (userRole.equalsIgnoreCase("INSTRUCTOR")) {
            tabbedPane.setEnabledAt(0, false);
        }
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));

        JTextField studentIdField = new JTextField();
        studentIdField.setEditable(false);
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField majorField = new JTextField();
        JTextField searchField = new JTextField();

        inputPanel.add(new JLabel("Student ID (auto):"));
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
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String major = majorField.getText();

            String SQL = "INSERT INTO Student (FirstName, LastName, Email, Major) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, email);
                statement.setString(4, major);
                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newID = generatedKeys.getInt(1);

                    String loginSQL = "INSERT INTO Login (username, password, AccessLevel, StudentID) VALUES (?, 'changeme', 'STUDENT', ?)";
                    try (PreparedStatement loginStmt = conn.prepareStatement(loginSQL)) {
                        loginStmt.setString(1, String.valueOf(newID));
                        loginStmt.setInt(2, newID);
                        loginStmt.executeUpdate();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            majorField.setText("");
            studentIdField.setText("");
            loadStudents(model);
        });
        buttonPanel.add(addBtn);

        JButton updateButton = new JButton("Update Student");
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String email = emailField.getText();
                String major = majorField.getText();

                String SQL = "UPDATE Student SET FirstName=?, LastName=?, Email=?, Major=? WHERE StudentID=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement statement = conn.prepareStatement(SQL)) {
                    statement.setString(1, firstName);
                    statement.setString(2, lastName);
                    statement.setString(3, email);
                    statement.setString(4, major);
                    statement.setInt(5, id);
                    statement.executeUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                loadStudents(model);
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
                int id = (int) model.getValueAt(selectedRow, 0);

                String SQL = "DELETE FROM Student WHERE StudentID=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement statement = conn.prepareStatement(SQL)) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                loadStudents(model);
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
            if (key.isEmpty()) {
                loadStudents(model);
                return;
            }
            String SQL = "SELECT StudentID, FirstName, LastName, Email, Major FROM Student WHERE LOWER(FirstName) LIKE ? OR LOWER(LastName) LIKE ? OR LOWER(Email) LIKE ? OR LOWER(Major) LIKE ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(SQL)) {
                String searchKey = "%" + key + "%";
                statement.setString(1, searchKey);
                statement.setString(2, searchKey);
                statement.setString(3, searchKey);
                statement.setString(4, searchKey);
                ResultSet rs = statement.executeQuery();
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("StudentID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Major")
                    });
                }
            } catch (Exception ex) {
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
        try (Connection conn = DatabaseConnection.getConnection()) {
            String SQL = "SELECT StudentID, FirstName, LastName, Email, Major FROM Student";
            PreparedStatement statement = conn.prepareStatement(SQL);
            ResultSet rs = statement.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("StudentID"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getString("Email"),
                    rs.getString("Major")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadCourses(DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String SQL = "SELECT c.CourseID, c.CourseName, c.Department, c.Credits, i.FirstName, i.LastName FROM Courses c LEFT JOIN Instructors i ON c.InstructorID = i.InstructorID";
            PreparedStatement statement = conn.prepareStatement(SQL);
            ResultSet rs = statement.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                String instructorName = "";
                String first = rs.getString("FirstName");
                String last = rs.getString("LastName");
                if (first != null && last != null) {
                    instructorName = first + " " + last;
                }
                model.addRow(new Object[]{
                    rs.getInt("CourseID"),
                    rs.getString("CourseName"),
                    rs.getString("Department"),
                    rs.getInt("Credits"),
                    instructorName
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JComboBox<String> createInstructorComboBox(java.util.List<Integer> instructorIds) {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("-- None --");
        instructorIds.clear();
        instructorIds.add(-1);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String SQL = "SELECT InstructorID, FirstName, LastName FROM Instructors";
            PreparedStatement stmt = conn.prepareStatement(SQL);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                instructorIds.add(rs.getInt("InstructorID"));
                combo.addItem(rs.getString("FirstName") + " " + rs.getString("LastName"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return combo;
    }

    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));

        JTextField courseIdField = new JTextField();
        courseIdField.setEditable(false);
        JTextField courseNameField = new JTextField();
        JTextField departmentField = new JTextField();
        JTextField creditsField = new JTextField();
        JTextField searchField = new JTextField();

        java.util.List<Integer> instructorIds = new java.util.ArrayList<>();
        JComboBox<String> instructorCombo = createInstructorComboBox(instructorIds);

        inputPanel.add(new JLabel("Course ID (auto):"));
        inputPanel.add(courseIdField);
        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(courseNameField);
        inputPanel.add(new JLabel("Department:"));
        inputPanel.add(departmentField);
        inputPanel.add(new JLabel("Credits:"));
        inputPanel.add(creditsField);
        inputPanel.add(new JLabel("Instructor:"));
        inputPanel.add(instructorCombo);
        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(searchField);

        String[] columns = {"CourseID", "Course Name", "Department", "Credits", "Instructor"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        loadCourses(model);
        JScrollPane scrollPane = new JScrollPane(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                courseIdField.setText(model.getValueAt(row, 0).toString());
                courseNameField.setText(model.getValueAt(row, 1).toString());
                departmentField.setText(model.getValueAt(row, 2).toString());
                creditsField.setText(model.getValueAt(row, 3).toString());
                String instructorInTable = model.getValueAt(row, 4).toString();
                for (int i = 0; i < instructorCombo.getItemCount(); i++) {
                    if (instructorCombo.getItemAt(i).equals(instructorInTable)) {
                        instructorCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add Course");
        addBtn.addActionListener(e -> {
            String name = courseNameField.getText();
            String dept = departmentField.getText();
            int credits = Integer.parseInt(creditsField.getText());
            int selectedIndex = instructorCombo.getSelectedIndex();
            int instructorID = instructorIds.get(selectedIndex);

            String SQL = "INSERT INTO Courses (CourseName, Department, Credits, InstructorID) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(SQL)) {
                statement.setString(1, name);
                statement.setString(2, dept);
                statement.setInt(3, credits);
                if (instructorID == -1) {
                    statement.setNull(4, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(4, instructorID);
                }
                statement.executeUpdate();
                loadCourses(model);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            courseIdField.setText("");
            courseNameField.setText("");
            departmentField.setText("");
            creditsField.setText("");
            instructorCombo.setSelectedIndex(0);
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
                int selectedIndex = instructorCombo.getSelectedIndex();
                int instructorID = instructorIds.get(selectedIndex);

                String SQL = "UPDATE Courses SET CourseName=?, Department=?, Credits=?, InstructorID=? WHERE CourseID=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement statement = conn.prepareStatement(SQL)) {
                    statement.setString(1, name);
                    statement.setString(2, dept);
                    statement.setInt(3, credits);
                    if (instructorID == -1) {
                        statement.setNull(4, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(4, instructorID);
                    }
                    statement.setInt(5, id);
                    statement.executeUpdate();
                    loadCourses(model);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }

            courseIdField.setText("");
            courseNameField.setText("");
            departmentField.setText("");
            creditsField.setText("");
            instructorCombo.setSelectedIndex(0);
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete Course");
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = Integer.parseInt(courseIdField.getText());
                String SQL = "DELETE FROM Courses WHERE CourseID=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement statement = conn.prepareStatement(SQL)) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                    loadCourses(model);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }

            courseIdField.setText("");
            courseNameField.setText("");
            departmentField.setText("");
            creditsField.setText("");
            instructorCombo.setSelectedIndex(0);
        });
        buttonPanel.add(deleteButton);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String key = searchField.getText().toLowerCase();
            if (key.isEmpty()) {
                loadCourses(model);
                return;
            }
            String SQL = "SELECT c.CourseID, c.CourseName, c.Department, c.Credits, i.FirstName, i.LastName FROM Courses c LEFT JOIN Instructors i ON c.InstructorID = i.InstructorID WHERE LOWER(c.CourseName) LIKE ? OR LOWER(c.Department) LIKE ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(SQL)) {
                String searchKey = "%" + key + "%";
                statement.setString(1, searchKey);
                statement.setString(2, searchKey);
                ResultSet rs = statement.executeQuery();
                model.setRowCount(0);
                while (rs.next()) {
                    String instructorName = "";
                    String first = rs.getString("FirstName");
                    String last = rs.getString("LastName");
                    if (first != null && last != null) {
                        instructorName = first + " " + last;
                    }
                    model.addRow(new Object[]{
                        rs.getInt("CourseID"),
                        rs.getString("CourseName"),
                        rs.getString("Department"),
                        rs.getInt("Credits"),
                        instructorName
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        buttonPanel.add(searchButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadEnrollments(DefaultTableModel model) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean isStudent = userRole.equalsIgnoreCase("STUDENT");
            String SQL = "SELECT e.StudentID, e.CourseID, e.Semester, COALESCE(g.LetterGrade, 'IP') AS Grade " +
                         "FROM Enrollments e " +
                         "LEFT JOIN Grades g ON e.StudentID = g.StudentID AND e.CourseID = g.CourseID" +
                         (isStudent ? " WHERE e.StudentID = ?" : "");
            PreparedStatement statement = conn.prepareStatement(SQL);
            if (isStudent) {
                statement.setInt(1, studentID);
            }
            ResultSet rs = statement.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("StudentID"),
                    rs.getInt("CourseID"),
                    rs.getString("Semester"),
                    rs.getString("Grade")
                });
            }
        } catch (Exception ex) {
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

        // Pre-fill and lock Student ID field for students
        if (userRole.equalsIgnoreCase("STUDENT")) {
            studentIdField.setText(String.valueOf(studentID));
            studentIdField.setEditable(false);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                // Only allow selecting own rows if student
                if (!userRole.equalsIgnoreCase("STUDENT")) {
                    studentIdField.setText(model.getValueAt(row, 0).toString());
                }
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
            int sID = Integer.parseInt(studentIdField.getText());
            int cID = Integer.parseInt(courseIdField.getText());
            String semester = semesterField.getText();

            String SQL = "INSERT INTO Enrollments (StudentID, CourseID, Semester) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(SQL)) {
                statement.setInt(1, sID);
                statement.setInt(2, cID);
                statement.setString(3, semester);
                statement.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            studentIdField.setText(userRole.equalsIgnoreCase("STUDENT") ? String.valueOf(studentID) : "");
            courseIdField.setText("");
            semesterField.setText("");
            loadEnrollments(model);
        });
        enrollPanel.add(enrollButton);

        JButton dropButton = new JButton("Drop");
        dropButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an enrollment to drop.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String grade = model.getValueAt(selectedRow, 3).toString();
            if (!grade.equals("IP")) {
                JOptionPane.showMessageDialog(this, "Cannot drop a course that has already been graded.",
                        "Illegal Operation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int sID = Integer.parseInt(studentIdField.getText());
            int cID = Integer.parseInt(courseIdField.getText());

            String SQL = "DELETE FROM Enrollments WHERE StudentID=? AND CourseID=?";
            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(SQL)) {
                statement.setInt(1, sID);
                statement.setInt(2, cID);
                statement.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            studentIdField.setText(userRole.equalsIgnoreCase("STUDENT") ? String.valueOf(studentID) : "");
            courseIdField.setText("");
            loadEnrollments(model);
        });
        enrollPanel.add(dropButton);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel topContainer = new JPanel(new GridLayout(2, 1));
        topContainer.add(enrollPanel);

        // Only show grade management panel for non-students
        if (!userRole.equalsIgnoreCase("STUDENT")) {
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
                int sID = Integer.parseInt(gStudentIdField.getText());
                int cID = Integer.parseInt(gCourseIdField.getText());
                String grade = (String) gradeBox.getSelectedItem();

                String SQL = "INSERT INTO Grades (StudentID, CourseID, LetterGrade) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE LetterGrade=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement statement = conn.prepareStatement(SQL)) {
                    statement.setInt(1, sID);
                    statement.setInt(2, cID);
                    statement.setString(3, grade);
                    statement.setString(4, grade);
                    statement.executeUpdate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                gStudentIdField.setText("");
                gCourseIdField.setText("");
                loadEnrollments(model);
            });
            gradePanel.add(assignGradeButton);
            topContainer.add(gradePanel);
        }

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        boolean isStudent = userRole.equalsIgnoreCase("STUDENT");
        JPanel buttonPanel = new JPanel(new GridLayout(1, isStudent ? 1 : 3, 10, 10));

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if (userRole.equalsIgnoreCase("ADMIN") || userRole.equalsIgnoreCase("INSTRUCTOR")) {
            JTextField reportStudentIdField = new JTextField(10);
            idPanel.add(new JLabel("Student ID for Transcript:"));
            idPanel.add(reportStudentIdField);

            JButton transcriptButton = new JButton("Student Transcript Report");
            transcriptButton.addActionListener(e -> {
                String idText = reportStudentIdField.getText().trim();
                if (idText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a Student ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int targetID = Integer.parseInt(idText);
                generateTranscript(targetID, reportArea);
            });
            buttonPanel.add(transcriptButton);
        } else {
            JButton transcriptButton = new JButton("My Transcript");
            transcriptButton.addActionListener(e -> generateTranscript(studentID, reportArea));
            buttonPanel.add(transcriptButton);
        }

        if (!isStudent) {
        JButton enrollmentReportButton = new JButton("Course Enrollment Report");
        enrollmentReportButton.addActionListener(e -> {
            String SQL = "SELECT c.CourseName, s.StudentID, s.FirstName, s.LastName " +
                         "FROM Enrollments e " +
                         "JOIN Student s ON e.StudentID = s.StudentID " +
                         "JOIN Courses c ON e.CourseID = c.CourseID " +
                         "ORDER BY c.CourseName";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(SQL)) {
                ResultSet rs = statement.executeQuery();
                StringBuilder report = new StringBuilder();
                while (rs.next()) {
                    report.append("Course: ").append(rs.getString("CourseName")).append("\n");
                    report.append("Student ID: ").append(rs.getInt("StudentID")).append("\n");
                    report.append("Name: ").append(rs.getString("FirstName")).append(" ").append(rs.getString("LastName")).append("\n");
                    report.append("-----------------------------\n");
                }
                reportArea.setText(report.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        buttonPanel.add(enrollmentReportButton);

        JButton instructorReportButton = new JButton("Instructor Performance Report");
        instructorReportButton.addActionListener(e -> {
            String SQL = "SELECT i.FirstName, i.LastName, g.LetterGrade, COUNT(*) AS GradeCount " +
                         "FROM Grades g " +
                         "JOIN Enrollments en ON g.StudentID = en.StudentID AND g.CourseID = en.CourseID " +
                         "JOIN Courses c ON en.CourseID = c.CourseID " +
                         "JOIN Instructors i ON c.InstructorID = i.InstructorID " +
                         "GROUP BY i.InstructorID, i.FirstName, i.LastName, g.LetterGrade " +
                         "ORDER BY i.LastName, i.FirstName, g.LetterGrade";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement statement = conn.prepareStatement(SQL)) {
                ResultSet rs = statement.executeQuery();
                StringBuilder report = new StringBuilder();
                String currentInstructor = "";
                while (rs.next()) {
                    String name = rs.getString("FirstName") + " " + rs.getString("LastName");
                    if (!name.equals(currentInstructor)) {
                        if (!currentInstructor.isEmpty()) {
                            report.append("-----------------------------\n");
                        }
                        report.append("Instructor: ").append(name).append("\n");
                        currentInstructor = name;
                    }
                    report.append("  ").append(rs.getString("LetterGrade")).append(": ").append(rs.getInt("GradeCount")).append(" student(s)\n");
                }
                reportArea.setText(report.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        buttonPanel.add(instructorReportButton);
        } // end if (!isStudent)

        topPanel.add(idPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void generateTranscript(int targetID, JTextArea reportArea) {
        String SQL = "SELECT s.StudentID, s.FirstName, s.LastName, c.CourseName, g.LetterGrade " +
                     "FROM Student s " +
                     "JOIN Grades g ON s.StudentID = g.StudentID " +
                     "JOIN Courses c ON g.CourseID = c.CourseID " +
                     "WHERE s.StudentID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(SQL)) {
            statement.setInt(1, targetID);
            ResultSet rs = statement.executeQuery();
            StringBuilder report = new StringBuilder();
            while (rs.next()) {
                report.append("Student ID: ").append(rs.getInt("StudentID")).append("\n");
                report.append("Name: ").append(rs.getString("FirstName")).append(" ").append(rs.getString("LastName")).append("\n");
                report.append("Course: ").append(rs.getString("CourseName")).append("\n");
                report.append("Grade: ").append(rs.getString("LetterGrade")).append("\n");
                report.append("-----------------------------\n");
            }
            if (report.length() == 0) {
                report.append("No records found for Student ID: ").append(targetID);
            }
            reportArea.setText(report.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}