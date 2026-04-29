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
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Major:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(new JTextField());

        String[] columns = {"StudentID", "First Name", "Last Name", "Email", "Major"};
        Object[][] data = {{"123456", "Example", "Example", "example@example.com", "Example Major"}};
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        
        //TODO: add functionality to Student buttons

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton("Add Student"));
        buttonPanel.add(new JButton("Update Student"));
        buttonPanel.add(new JButton("Delete Student"));
        buttonPanel.add(new JButton("Search"));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.add(new JLabel("Course ID:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Department:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Credits:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Instructor:"));
        inputPanel.add(new JTextField());
        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(new JTextField());

        String[] columns = {"CourseID", "Course Name", "Department", "Credits", "Instructor"};
        Object[][] data = {{"CSEX123", "Example Class", "Example Department", "3", "Prof. Examples"}};
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        
        //TODO: add functionality to Course buttons

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton("Add Course"));
        buttonPanel.add(new JButton("Update Course"));
        buttonPanel.add(new JButton("Delete Course"));
        buttonPanel.add(new JButton("Search"));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

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
}