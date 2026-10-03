import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class OnlineExam extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel main = new JPanel(cards);
    private JTextField usernameField, nameField;
    private JPasswordField passwordField, newPasswordField;
    private JLabel timerLabel, questionLabel, resultLabel;
    private JPanel optionsPanel;
    private JRadioButton[] options;
    private int current = 0, secondsLeft = 30 * 60;
    private javax.swing.Timer timer;
    private int score = 0;
    private final boolean[] answered = new boolean[5];
    private final int[] selected = new int[5];
    private final String[] questions = {
        "Which keyword is used to create a class in Java?",
        "Which method starts a Java program?",
        "Which component is used for a single-line text input in Swing?",
        "Which keyword prevents a variable from being changed?",
        "Which layout is useful for switching between panels?"
    };
    private final String[][] choices = {
        {"class", "define", "struct", "object"},
        {"run()", "main()", "start()", "execute()"},
        {"JTextField", "JLabel", "JButton", "JPanel"},
        {"static", "final", "private", "const"},
        {"FlowLayout", "GridLayout", "CardLayout", "BorderLayout"}
    };
    private final int[] answers = {0, 1, 0, 1, 2};

    public OnlineExam() {
        setTitle("Online Examination System");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (cardsPanelIsExam()) {
                    int r = JOptionPane.showConfirmDialog(OnlineExam.this,
                            "Are you sure you want to quit the exam? Your exam will be submitted.",
                            "Confirm Exit", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) submitExam();
                } else dispose();
            }
        });

        main.add(loginPanel(), "login");
        main.add(profilePanel(), "profile");
        main.add(examPanel(), "exam");
        main.add(resultPanel(), "result");
        add(main);
        cards.show(main, "login");
    }

    private boolean cardsPanelIsExam() { return timer != null && timer.isRunning(); }

    private JPanel loginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        JPanel box = new JPanel(new GridLayout(5, 1, 8, 8));
        JLabel title = new JLabel("ONLINE EXAMINATION SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        usernameField = new JTextField("student");
        passwordField = new JPasswordField("1234");
        JButton login = new JButton("Login");
        JLabel hint = new JLabel("Demo login: student / 1234", SwingConstants.CENTER);
        box.add(title); box.add(new JLabel("Username:")); box.add(usernameField);
        box.add(new JLabel("Password:")); box.add(passwordField);
        box.add(login); box.add(hint);
        login.addActionListener(e -> doLogin());
        p.add(box);
        return p;
    }

    private void doLogin() {
        if (usernameField.getText().trim().equals("student") && new String(passwordField.getPassword()).equals("1234")) {
            cards.show(main, "profile");
        } else JOptionPane.showMessageDialog(this, "Invalid login. Use student / 1234.");
    }

    private JPanel profilePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        JPanel box = new JPanel(new GridLayout(6, 1, 8, 8));
        JLabel title = new JLabel("Student Profile", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        nameField = new JTextField("Student Name");
        newPasswordField = new JPasswordField("1234");
        JButton start = new JButton("Start Exam");
        box.add(title); box.add(new JLabel("Name:")); box.add(nameField);
        box.add(new JLabel("Change Password (optional):")); box.add(newPasswordField); box.add(start);
        start.addActionListener(e -> startExam());
        p.add(box);
        return p;
    }

    private JPanel examPanel() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        JPanel top = new JPanel(new BorderLayout());
        timerLabel = new JLabel("30:00", SwingConstants.RIGHT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        top.add(new JLabel("Online Exam"), BorderLayout.WEST);
        top.add(timerLabel, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        questionLabel = new JLabel(); questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        center.add(questionLabel, BorderLayout.NORTH);
        optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        center.add(optionsPanel, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);

        JPanel nav = new JPanel();
        JButton prev = new JButton("Previous");
        JButton next = new JButton("Next");
        JButton submit = new JButton("Submit Exam");
        prev.addActionListener(e -> { saveAnswer(); if (current > 0) { current--; loadQuestion(); } });
        next.addActionListener(e -> { saveAnswer(); if (current < questions.length - 1) { current++; loadQuestion(); } });
        submit.addActionListener(e -> confirmSubmit());
        nav.add(prev); nav.add(next); nav.add(submit);
        p.add(nav, BorderLayout.SOUTH);
        return p;
    }

    private void startExam() {
        current = 0; score = 0; secondsLeft = 30 * 60;
        Arrays.fill(answered, false); Arrays.fill(selected, -1);
        loadQuestion(); cards.show(main, "exam");
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(1000, e -> tick()); timer.start();
    }

    private void tick() {
        secondsLeft--;
        timerLabel.setText(String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60));
        if (secondsLeft <= 0) { timer.stop(); JOptionPane.showMessageDialog(this, "Time is over. The exam will be submitted."); submitExam(); }
    }

    private void loadQuestion() {
        questionLabel.setText("Question " + (current + 1) + " of " + questions.length + ": " + questions[current]);
        optionsPanel.removeAll();
        ButtonGroup group = new ButtonGroup();
        options = new JRadioButton[4];
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton(choices[current][i]);
            group.add(options[i]); optionsPanel.add(options[i]);
        }
        if (selected[current] >= 0) options[selected[current]].setSelected(true);
        optionsPanel.revalidate(); optionsPanel.repaint();
    }

    private void saveAnswer() {
        if (options == null) return;
        for (int i = 0; i < options.length; i++) if (options[i].isSelected()) { selected[current] = i; answered[current] = true; return; }
    }

    private void confirmSubmit() {
        saveAnswer();
        int r = JOptionPane.showConfirmDialog(this, "Submit your exam now?", "Confirm Submission", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) submitExam();
    }

    private void submitExam() {
        if (timer != null) timer.stop();
        saveAnswer(); score = 0;
        for (int i = 0; i < questions.length; i++) if (selected[i] == answers[i]) score++;
        resultLabel.setText("Score: " + score + " / " + questions.length);
        cards.show(main, "result");
    }

    private JPanel resultPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        JPanel box = new JPanel(new GridLayout(5, 1, 10, 10));
        JLabel title = new JLabel("Exam Result", SwingConstants.CENTER); title.setFont(new Font("Arial", Font.BOLD, 26));
        resultLabel = new JLabel("Score: 0 / 5", SwingConstants.CENTER); resultLabel.setFont(new Font("Arial", Font.BOLD, 22));
        JButton details = new JButton("Show Breakdown");
        JButton logout = new JButton("Logout");
        box.add(title); box.add(resultLabel); box.add(details); box.add(logout);
        details.addActionListener(e -> {
            int correct = 0; for (int i = 0; i < questions.length; i++) if (selected[i] == answers[i]) correct++;
            JOptionPane.showMessageDialog(this, "Correct: " + correct + "\nIncorrect: " + (questions.length - correct) + "\nTime taken: " + ((30*60-secondsLeft)/60) + " minutes");
        });
        logout.addActionListener(e -> { usernameField.setText("student"); passwordField.setText("1234"); cards.show(main, "login"); });
        p.add(box);
        // update score label whenever result screen is shown
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OnlineExam().setVisible(true));
    }
}
