import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class QuizApp extends JFrame implements ActionListener {
    CardLayout card = new CardLayout();
    JPanel mainPanel = new JPanel(card);
    // Welcome Panel
    JPanel welcomePanel = new JPanel();
    // Quiz Panel
    JPanel quizPanel = new JPanel();
    // Result Panel
    JPanel resultPanel = new JPanel();
    JLabel questionLabel, timerLabel, resultLabel;
    JRadioButton op1, op2, op3, op4;
    ButtonGroup bg;
    JButton startBtn, nextBtn, exitBtn;
    String questions[] = {
            "Which language is used for Swing?",
            "Which company developed Java?"
    };
    String options[][] = {
            {"Python", "Java", "C", "PHP"},
            {"Microsoft", "Apple", "Sun Microsystems", "Google"}
    };
    int answers[] = {1, 2};
    int current = 0;
    int score = 0;
    int time = 10;
    Timer timer;
    public QuizApp() {
        setTitle("Quiz Application");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Welcome Screen
        welcomePanel.setLayout(new FlowLayout());
        JLabel title = new JLabel("WELCOME TO QUIZ");
        startBtn = new JButton("Start Quiz");
        welcomePanel.add(title);
        welcomePanel.add(startBtn);
        startBtn.addActionListener(e -> {
            loadQuestion();
            card.show(mainPanel, "Quiz");
            startTimer();
        });
        // Quiz Screen
        quizPanel.setLayout(new GridLayout(8, 1));
        timerLabel = new JLabel("Time: 10");
        questionLabel = new JLabel();
        op1 = new JRadioButton();
        op2 = new JRadioButton();
        op3 = new JRadioButton();
        op4 = new JRadioButton();
        bg = new ButtonGroup();
        bg.add(op1);
        bg.add(op2);
        bg.add(op3);
        bg.add(op4);
        nextBtn = new JButton("Next");
        nextBtn.addActionListener(this);
        quizPanel.add(timerLabel);
        quizPanel.add(questionLabel);
        quizPanel.add(op1);
        quizPanel.add(op2);
        quizPanel.add(op3);
        quizPanel.add(op4);
        quizPanel.add(nextBtn);
        // Result Screen
        resultPanel.setLayout(new FlowLayout());
        resultLabel = new JLabel();
        exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        resultPanel.add(resultLabel);
        resultPanel.add(exitBtn);
        // Add Panels
        mainPanel.add(welcomePanel, "Welcome");
        mainPanel.add(quizPanel, "Quiz");
        mainPanel.add(resultPanel, "Result");
        add(mainPanel);
        setVisible(true);
    }
    void loadQuestion() {
        bg.clearSelection();
        questionLabel.setText(questions[current]);
        op1.setText(options[current][0]);
        op2.setText(options[current][1]);
        op3.setText(options[current][2]);
        op4.setText(options[current][3]);
    }
    void startTimer() {
        time = 10;
        timerLabel.setText("Time: " + time);
        timer = new Timer(1000, e -> {
            time--;
            timerLabel.setText("Time: " + time);
            if (time == 0) {
                timer.stop();
                checkAnswer();
            }
        });
        timer.start();
    }
    void checkAnswer() {
        if (current == answers.length) {
            return;
        }
        if (op1.isSelected() && answers[current] == 0)
            score++;
        if (op2.isSelected() && answers[current] == 1)
            score++;
        if (op3.isSelected() && answers[current] == 2)
            score++;
        if (op4.isSelected() && answers[current] == 3)
            score++;
        current++;
        if (current < questions.length) {
            loadQuestion();
            timer.stop();
            startTimer();
        } else {
            timer.stop();
            int percentage = (score * 100) / questions.length;
            resultLabel.setText(
                    "Score: " + score +
                    " / " + questions.length +
                    "   Percentage: " + percentage + "%"
            );
            card.show(mainPanel, "Result");
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        checkAnswer();
    }
    public static void main(String[] args) {
        new QuizApp();
    }
}
