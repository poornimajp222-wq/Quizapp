import javax.swing.*;
import javax.swing.border.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class TechQuiz extends JFrame {

    // ─── COLORS ───────────────────────────────────────────────────────────
    static final Color BG        = new Color(5, 8, 16);
    static final Color SURFACE   = new Color(13, 18, 36);
    static final Color SURFACE2  = new Color(19, 25, 41);
    static final Color CYAN      = new Color(0, 200, 255);
    static final Color CYAN2     = new Color(0, 255, 231);
    static final Color GOLD      = new Color(255, 215, 0);
    static final Color PINK      = new Color(255, 45, 122);
    static final Color GREEN     = new Color(0, 255, 136);
    static final Color RED       = new Color(255, 59, 92);
    static final Color TEXT      = new Color(226, 234, 247);
    static final Color TEXT2     = new Color(122, 144, 184);
    static final Color BORDER_C  = new Color(0, 200, 255, 46);

    // ─── FONTS ────────────────────────────────────────────────────────────
    static Font FONT_TITLE, FONT_MONO, FONT_BODY, FONT_BODY_BOLD;
    static {
        FONT_TITLE     = new Font("Monospaced", Font.BOLD, 28);
        FONT_MONO      = new Font("Monospaced", Font.PLAIN, 13);
        FONT_BODY      = new Font("SansSerif", Font.PLAIN, 15);
        FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 15);
    }

    // ─── QUESTIONS ────────────────────────────────────────────────────────
    static final String[][] QUESTIONS = {
        {"DATA STRUCTURES",      "What is the time complexity of searching in a balanced Binary Search Tree?",             "O(n)","O(log n)","O(n²)","O(1)"},
        {"OPERATING SYSTEMS",    "Which scheduling algorithm can lead to the 'Convoy Effect'?",                             "Round Robin","Shortest Job First","First Come First Serve","Priority Scheduling"},
        {"DBMS",                 "Which normal form eliminates transitive dependencies?",                                   "1NF","2NF","3NF","BCNF"},
        {"COMPUTER NETWORKS",    "How many layers does the OSI model have?",                                                "5","6","7","8"},
        {"C PROGRAMMING",        "What does sizeof() return for a pointer on a 64-bit system?",                             "4 bytes","8 bytes","2 bytes","Depends on type"},
        {"OOP / JAVA",           "Which concept allows a subclass to provide its own implementation of a parent method?",   "Overloading","Inheritance","Overriding","Encapsulation"},
        {"ALGORITHMS",           "What is the worst-case time complexity of QuickSort?",                                    "O(n log n)","O(n²)","O(log n)","O(n)"},
        {"COMPUTER ARCHITECTURE","Which memory is fastest and closest to the CPU?",                                         "RAM","Cache","Registers","ROM"},
        {"DIGITAL ELECTRONICS",  "The output of an XOR gate is 1 when:",                                                   "Both inputs are 1","Both inputs are 0","Inputs are different","Inputs are same"},
        {"DATA STRUCTURES",      "Which data structure uses LIFO (Last In, First Out)?",                                   "Queue","Stack","Linked List","Tree"},
        {"COMPUTER NETWORKS",    "Which protocol is used to assign IP addresses dynamically?",                              "FTP","DNS","DHCP","SMTP"},
        {"ALGORITHMS",           "What is the space complexity of Merge Sort?",                                             "O(1)","O(log n)","O(n)","O(n log n)"},
    };
    static final int[] ANSWERS = {1, 2, 2, 2, 1, 2, 1, 2, 2, 1, 2, 2};

    // ─── STATE ─────────────────────────────────────────────────────────────
    CardLayout cards;
    JPanel mainPanel;

    // Registration fields
    JTextField fName, fUSN;
    JComboBox<String> fBranch, fSem;

    // Quiz state
    int currentQ = 0, score = 0, correct = 0, wrong = 0;
    int timeLeft = 20;
    Timer quizTimer;
    String studentName, studentUSN, studentBranch, studentSem;
    int[] userAnswers;

    // Quiz UI refs
    JLabel lblQNum, lblCategory, lblQuestion, lblScore, lblTimer;
    JProgressBar timerBar, progressBar;
    JButton[] optBtns;
    TimerRingPanel timerRing;

    // ─── MAIN ──────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new TechQuiz().setVisible(true);
        });
    }

    TechQuiz() {
        super("⚡ TECHQUIZ — Engineering Knowledge Challenge");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 620);
        setMinimumSize(new Dimension(680, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        cards = new CardLayout();
        mainPanel = new JPanel(cards);
        mainPanel.setBackground(BG);

        mainPanel.add(buildRegisterScreen(), "register");
        mainPanel.add(buildQuizScreen(),     "quiz");
        mainPanel.add(buildResultScreen(),   "result");

        add(mainPanel);
        cards.show(mainPanel, "register");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SCREEN 1 — REGISTRATION
    // ══════════════════════════════════════════════════════════════════════
    JPanel buildRegisterScreen() {
        JPanel outer = new GradBgPanel();
        outer.setLayout(new GridBagLayout());

        JPanel card = new CyberCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(500, 480));

        // Logo
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoRow.setOpaque(false);
        JLabel icon = new JLabel("⚡");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 38));
        logoRow.add(icon);
        card.add(logoRow);
        card.add(Box.createVerticalStrut(4));

        JLabel title = glowLabel("TECHQUIZ", 30, CYAN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        JLabel sub = monoLabel("∙  ENGINEERING KNOWLEDGE CHALLENGE  ∙", 11, TEXT2);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(28));

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 12, 6, 12);

        fName   = styledField("e.g. Ravi Kumar");
        fUSN    = styledField("e.g. 1SI22CS001");
        fBranch = styledCombo("CSE","ISE","ECE","EEE","ME","CV","AI&ML","DS");
        fSem    = styledCombo("1","2","3","4","5","6","7","8");

        addFormRow(form, gc, 0, "FULL NAME",  fName,   2);
        addFormRow(form, gc, 1, "USN",        fUSN,    2);
        addFormRow(form, gc, 2, "BRANCH",     fBranch, 1);
        addFormRow(form, gc, 2, "SEMESTER",   fSem,    1);

        card.add(form);
        card.add(Box.createVerticalStrut(22));

        // Start button
        JButton btnStart = cyberButton("▶   LAUNCH QUIZ", CYAN, BG);
        btnStart.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStart.setMaximumSize(new Dimension(360, 46));
        btnStart.addActionListener(e -> validateAndStart());
        card.add(btnStart);
        card.add(Box.createVerticalStrut(10));

        outer.add(card);
        return outer;
    }

    void addFormRow(JPanel form, GridBagConstraints gc, int row, String lbl, JComponent field, int span) {
        int col = (span == 2 || gc.gridx == 0) ? 0 : 2;
        if (span == 2) {
            gc.gridx = 0; gc.gridy = row * 2; gc.gridwidth = 4;
            form.add(monoLabel(lbl, 11, CYAN), gc);
            gc.gridy = row * 2 + 1;
            form.add(field, gc);
        } else {
            boolean isFirst = (gc.gridx == 0 || gc.gridx == -1);
            // label
            GridBagConstraints lc = (GridBagConstraints) gc.clone();
            lc.gridx = isFirst ? 0 : 2; lc.gridy = row * 2; lc.gridwidth = 1;
            lc.insets = new Insets(6, isFirst ? 12 : 8, 2, 4);
            form.add(monoLabel(lbl, 11, CYAN), lc);
            // field
            GridBagConstraints fc = (GridBagConstraints) gc.clone();
            fc.gridx = isFirst ? 0 : 2; fc.gridy = row * 2 + 1; fc.gridwidth = 1;
            fc.insets = new Insets(2, isFirst ? 12 : 8, 6, isFirst ? 8 : 12);
            form.add(field, fc);
            gc.gridx = isFirst ? 2 : 0; // toggle for next half-row call
        }
    }

    void validateAndStart() {
        String name   = fName.getText().trim();
        String branch = (String) fBranch.getSelectedItem();
        String sem    = (String) fSem.getSelectedItem();
        String usn    = fUSN.getText().trim().toUpperCase();

        if (name.isEmpty())   { popupError("Please enter your name!"); return; }
        if (branch == null || branch.equals("Select"))  { popupError("Please select your branch!"); return; }
        if (sem    == null || sem.equals("Select"))     { popupError("Please select your semester!"); return; }
        if (usn.isEmpty())    { popupError("Please enter your USN!"); return; }

        studentName = name; studentBranch = branch; studentSem = sem; studentUSN = usn;
        currentQ = 0; score = 0; correct = 0; wrong = 0;
        userAnswers = new int[QUESTIONS.length];
        Arrays.fill(userAnswers, -1);

        cards.show(mainPanel, "quiz");
        loadQuestion();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SCREEN 2 — QUIZ
    // ══════════════════════════════════════════════════════════════════════
    JPanel buildQuizScreen() {
        JPanel outer = new GradBgPanel();
        outer.setLayout(new GridBagLayout());

        JPanel card = new CyberCard();
        card.setPreferredSize(new Dimension(620, 520));
        card.setLayout(new BorderLayout(0, 0));

        // ── TOP BAR ──
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel leftInfo = new JPanel();
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.setOpaque(false);

        lblQNum = monoLabel("QUESTION 1 / " + QUESTIONS.length, 12, TEXT2);
        lblScore = new JLabel("⬡  0 pts");
        lblScore.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblScore.setForeground(GOLD);
        leftInfo.add(lblQNum);
        leftInfo.add(Box.createVerticalStrut(4));
        leftInfo.add(lblScore);

        timerRing = new TimerRingPanel();

        topBar.add(leftInfo,  BorderLayout.WEST);
        topBar.add(timerRing, BorderLayout.EAST);

        // ── PROGRESS BAR ──
        progressBar = new JProgressBar(0, QUESTIONS.length);
        progressBar.setValue(1);
        progressBar.setStringPainted(false);
        progressBar.setBackground(new Color(255,255,255,18));
        progressBar.setForeground(CYAN);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 5));

        // ── CENTER ──
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(14, 0, 4, 0));

        lblCategory = monoLabel("CATEGORY", 10, CYAN);
        lblCategory.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblQuestion = new JLabel("<html><body style='width:540px'>Loading...</body></html>");
        lblQuestion.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblQuestion.setForeground(TEXT);
        lblQuestion.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblQuestion.setBorder(new EmptyBorder(10, 0, 18, 0));

        center.add(lblCategory);
        center.add(lblQuestion);

        // Options
        optBtns = new JButton[4];
        String[] letters = {"A","B","C","D"};
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            optBtns[i] = new OptionButton(letters[i], "");
            optBtns[i].addActionListener(e -> selectAnswer(idx));
            optBtns[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            center.add(optBtns[i]);
            center.add(Box.createVerticalStrut(8));
        }

        // ── BOTTOM ──
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton skipBtn = cyberButton("SKIP  →", TEXT2, SURFACE2);
        skipBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
        skipBtn.setPreferredSize(new Dimension(120, 36));
        skipBtn.addActionListener(e -> {
            if (quizTimer != null) quizTimer.stop();
            userAnswers[currentQ] = -1;
            wrong++;
            nextQuestion();
        });
        bottom.add(skipBtn);

        JPanel inner = new JPanel(new BorderLayout(0, 0));
        inner.setOpaque(false);
        inner.add(topBar,      BorderLayout.NORTH);
        inner.add(progressBar, BorderLayout.CENTER);
        inner.add(center,      BorderLayout.SOUTH);

        card.add(inner,  BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        outer.add(card);
        return outer;
    }

    void loadQuestion() {
        String[] q = QUESTIONS[currentQ];
        lblQNum.setText("QUESTION " + (currentQ+1) + " / " + QUESTIONS.length);
        lblCategory.setText("◈  " + q[0]);
        lblQuestion.setText("<html><body style='width:520px'>" + q[1] + "</body></html>");
        progressBar.setValue(currentQ + 1);

        for (int i = 0; i < 4; i++) {
            ((OptionButton)optBtns[i]).reset(q[i+2]);
            optBtns[i].setEnabled(true);
        }

        startTimer();
    }

    void startTimer() {
        if (quizTimer != null) quizTimer.stop();
        timeLeft = 20;
        timerRing.setTime(20, 20);

        quizTimer = new Timer(1000, e -> {
            timeLeft--;
            timerRing.setTime(timeLeft, 20);
            if (timeLeft <= 0) {
                quizTimer.stop();
                userAnswers[currentQ] = -1;
                wrong++;
                markOptions(-1);
                showTimedToast("⏱  Time out!", RED);
                Timer delay = new Timer(1300, ev -> nextQuestion());
                delay.setRepeats(false); delay.start();
            }
        });
        quizTimer.start();
    }

    void selectAnswer(int idx) {
        if (quizTimer != null) quizTimer.stop();
        userAnswers[currentQ] = idx;
        boolean isCorrect = (idx == ANSWERS[currentQ]);
        if (isCorrect) {
            int pts = 10 + Math.max(0, timeLeft);
            score += pts;
            correct++;
            showTimedToast("✓  Correct!  +" + pts + " pts", GREEN);
        } else {
            wrong++;
            showTimedToast("✗  Wrong answer", RED);
        }
        lblScore.setText("⬡  " + score + " pts");
        markOptions(idx);
        Timer delay = new Timer(1300, e -> nextQuestion());
        delay.setRepeats(false); delay.start();
    }

    void markOptions(int chosen) {
        for (int i = 0; i < 4; i++) {
            optBtns[i].setEnabled(false);
            OptionButton ob = (OptionButton) optBtns[i];
            if (i == ANSWERS[currentQ]) ob.markCorrect();
            else if (i == chosen)       ob.markWrong();
        }
    }

    void nextQuestion() {
        currentQ++;
        if (currentQ >= QUESTIONS.length) showResults();
        else loadQuestion();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  SCREEN 3 — RESULTS
    // ══════════════════════════════════════════════════════════════════════
    JPanel resultCard;
    JLabel resPercent, resGrade, resMsg, resScore, resCorrect, resWrong;
    ScoreRingPanel scoreRing;
    JPanel reviewPanel;
    boolean reviewVisible = false;

    JPanel buildResultScreen() {
        JPanel outer = new GradBgPanel();
        outer.setLayout(new GridBagLayout());

        resultCard = new CyberCard();
        resultCard.setPreferredSize(new Dimension(600, 560));
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));

        // Ring
        scoreRing = new ScoreRingPanel();
        scoreRing.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultCard.add(scoreRing);
        resultCard.add(Box.createVerticalStrut(6));

        resGrade = glowLabel("—", 22, GOLD);
        resGrade.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultCard.add(resGrade);

        resMsg = new JLabel("—");
        resMsg.setFont(FONT_BODY);
        resMsg.setForeground(TEXT2);
        resMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultCard.add(resMsg);
        resultCard.add(Box.createVerticalStrut(18));

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 4, 8, 6));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(0,200,255,30), 1, true),
            new EmptyBorder(12, 16, 12, 16)
        ));
        infoPanel.setMaximumSize(new Dimension(560, 80));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Labels added dynamically in showResults()
        resultCard.add(infoPanel);
        resultCard.add(Box.createVerticalStrut(14));

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(560, 80));
        statsRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        resScore   = statCard("0", "SCORE",   GOLD);
        resCorrect = statCard("0", "CORRECT", GREEN);
        resWrong   = statCard("0", "WRONG",   RED);
        statsRow.add(wrapStat(resScore,   "SCORE",   GOLD));
        statsRow.add(wrapStat(resCorrect, "CORRECT", GREEN));
        statsRow.add(wrapStat(resWrong,   "WRONG",   RED));

        resultCard.add(statsRow);
        resultCard.add(Box.createVerticalStrut(16));

        // Buttons
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(560, 44));
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRetake = cyberButton("↺  RETAKE", CYAN, BG);
        JButton btnReview = cyberButton("☰  REVIEW", TEXT2, SURFACE2);
        btnRetake.addActionListener(e -> retake());
        btnReview.addActionListener(e -> toggleReview());

        btnRow.add(btnRetake);
        btnRow.add(btnReview);
        resultCard.add(btnRow);
        resultCard.add(Box.createVerticalStrut(10));

        // Review panel (hidden initially)
        reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
        reviewPanel.setOpaque(false);
        reviewPanel.setVisible(false);
        reviewPanel.setMaximumSize(new Dimension(560, 400));
        reviewPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(reviewPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setMaximumSize(new Dimension(560, 220));
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultCard.add(scroll);

        outer.add(resultCard);
        return outer;
    }

    void showResults() {
        int pct = (int) Math.round((correct * 100.0) / QUESTIONS.length);

        String grade, msg;
        Color gradeColor;
        if      (pct >= 90) { grade="S RANK"; msg="Exceptional! You're a tech wizard 🧙";      gradeColor=GOLD; }
        else if (pct >= 75) { grade="A RANK"; msg="Outstanding performance! Keep it up 🚀";    gradeColor=CYAN; }
        else if (pct >= 60) { grade="B RANK"; msg="Good work! A little more practice! 💪";     gradeColor=GREEN; }
        else if (pct >= 40) { grade="C RANK"; msg="Keep studying, you're getting there 📚";    gradeColor=PINK; }
        else                { grade="D RANK"; msg="Don't give up! Review the topics 🔁";       gradeColor=RED; }

        resGrade.setText(grade);
        resGrade.setForeground(gradeColor);
        resMsg.setText(msg);
        resScore.setText(String.valueOf(score));
        resCorrect.setText(String.valueOf(correct));
        resWrong.setText(String.valueOf(wrong));

        scoreRing.setScore(pct, gradeColor);

        // Info panel
        Component infoPanel = resultCard.getComponent(3); // index from build order
        if (infoPanel instanceof JPanel ip) {
            ip.removeAll();
            String[][] info = {{"NAME", studentName},{"USN", studentUSN},{"BRANCH", studentBranch},{"SEM", "SEM " + studentSem}};
            for (String[] row : info) {
                JLabel k = monoLabel(row[0], 10, CYAN); k.setHorizontalAlignment(SwingConstants.LEFT);
                ip.add(k);
            }
            for (String[] row : info) {
                JLabel v = new JLabel(row[1]); v.setFont(FONT_BODY_BOLD); v.setForeground(TEXT);
                ip.add(v);
            }
            ip.revalidate(); ip.repaint();
        }

        // Build review
        reviewPanel.removeAll();
        String[] opts = {"A","B","C","D"};
        for (int i = 0; i < QUESTIONS.length; i++) {
            boolean ok = userAnswers[i] == ANSWERS[i];
            JPanel item = new JPanel();
            item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
            item.setBackground(new Color(255,255,255,8));
            item.setBorder(new CompoundBorder(
                new MatteBorder(0,3,0,0, ok ? GREEN : RED),
                new EmptyBorder(8,12,8,12)
            ));
            item.setMaximumSize(new Dimension(540, 80));

            JLabel qLbl = new JLabel("<html><b>Q"+(i+1)+". "+QUESTIONS[i][1]+"</b></html>");
            qLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            qLbl.setForeground(TEXT);

            String userA = userAnswers[i] >= 0 ? opts[userAnswers[i]]+": "+QUESTIONS[i][userAnswers[i]+2] : "No answer";
            String corrA = opts[ANSWERS[i]]+": "+QUESTIONS[i][ANSWERS[i]+2];
            JLabel aLbl  = new JLabel("<html><font color='#7a90b8'>Your: </font>"
                + "<font color='"+(ok?"#00ff88":"#ff3b5c")+"'>"+userA+"</font>"
                + "  <font color='#7a90b8'>Correct: </font><font color='#00ff88'>"+corrA+"</font></html>");
            aLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));

            item.add(qLbl);
            item.add(Box.createVerticalStrut(4));
            item.add(aLbl);
            reviewPanel.add(item);
            reviewPanel.add(Box.createVerticalStrut(6));
        }

        cards.show(mainPanel, "result");
    }

    void toggleReview() {
        reviewVisible = !reviewVisible;
        reviewPanel.setVisible(reviewVisible);
        resultCard.revalidate(); resultCard.repaint();
    }

    void retake() {
        reviewVisible = false;
        reviewPanel.setVisible(false);
        fName.setText(""); fUSN.setText("");
        fBranch.setSelectedIndex(0); fSem.setSelectedIndex(0);
        cards.show(mainPanel, "register");
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TOAST
    // ══════════════════════════════════════════════════════════════════════
    JWindow toastWin;
    void showTimedToast(String msg, Color color) {
        if (toastWin != null) toastWin.dispose();
        toastWin = new JWindow(this);
        JLabel lbl = new JLabel("  " + msg + "  ", SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        lbl.setForeground(color);
        lbl.setBackground(SURFACE2);
        lbl.setOpaque(true);
        lbl.setBorder(new LineBorder(color.darker(), 1, true));
        toastWin.add(lbl);
        toastWin.pack();
        toastWin.setSize(280, 42);
        Point loc = getLocationOnScreen();
        toastWin.setLocation(loc.x + getWidth()/2 - 140, loc.y + 30);
        toastWin.setVisible(true);
        Timer t = new Timer(1800, e -> { toastWin.dispose(); });
        t.setRepeats(false); t.start();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HELPERS — UI FACTORIES
    // ══════════════════════════════════════════════════════════════════════
    JLabel glowLabel(String text, int size, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.BOLD, size));
        l.setForeground(c);
        return l;
    }

    JLabel monoLabel(String text, int size, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.PLAIN, size));
        l.setForeground(c);
        return l;
    }

    JTextField styledField(String placeholder) {
        JTextField tf = new JTextField(20);
        tf.setBackground(new Color(0, 200, 255, 10));
        tf.setForeground(TEXT);
        tf.setCaretColor(CYAN);
        tf.setFont(FONT_BODY_BOLD);
        tf.setBorder(new CompoundBorder(
            new LineBorder(new Color(0,200,255,60), 1, true),
            new EmptyBorder(8,12,8,12)
        ));
        tf.setPreferredSize(new Dimension(220, 40));
        // Placeholder
        tf.setText(placeholder);
        tf.setForeground(TEXT2);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(TEXT2); }
            }
        });
        return tf;
    }

    String getFieldValue(JTextField tf, String placeholder) {
        String v = tf.getText().trim();
        return v.equals(placeholder) ? "" : v;
    }

    JComboBox<String> styledCombo(String... items) {
        String[] all = new String[items.length+1];
        all[0] = "Select";
        System.arraycopy(items, 0, all, 1, items.length);
        JComboBox<String> cb = new JComboBox<>(all);
        cb.setBackground(SURFACE);
        cb.setForeground(TEXT);
        cb.setFont(FONT_BODY);
        cb.setPreferredSize(new Dimension(160, 40));
        cb.setBorder(new LineBorder(new Color(0,200,255,60), 1, true));
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                setBackground(sel ? new Color(0,200,255,40) : SURFACE);
                setForeground(TEXT);
                setBorder(new EmptyBorder(4,8,4,8));
                return this;
            }
        });
        return cb;
    }

    JButton cyberButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text) {
            boolean hover = false;
            { addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){ hover=true; repaint(); }
                public void mouseExited(MouseEvent e){ hover=false; repaint(); }
            }); }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = hover ? fg.darker() : bg;
                g2.setColor(base);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                if (hover) {
                    g2.setColor(new Color(fg.getRed(),fg.getGreen(),fg.getBlue(),30));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                }
                g2.setColor(new Color(fg.getRed(),fg.getGreen(),fg.getBlue(),120));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 44));
        return btn;
    }

    JLabel statCard(String val, String lbl, Color c) {
        JLabel l = new JLabel(val, SwingConstants.CENTER);
        l.setFont(new Font("Monospaced", Font.BOLD, 26));
        l.setForeground(c);
        return l;
    }

    JPanel wrapStat(JLabel val, String label, Color c) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(255,255,255,8));
        p.setBorder(new CompoundBorder(
            new LineBorder(new Color(255,255,255,18),1,true),
            new EmptyBorder(12,8,12,8)
        ));
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lbl = monoLabel(label, 10, TEXT2);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(val); p.add(Box.createVerticalStrut(4)); p.add(lbl);
        return p;
    }

    void popupError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Required",
            JOptionPane.WARNING_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  INNER PANEL CLASSES
    // ══════════════════════════════════════════════════════════════════════

    /** Gradient background panel */
    class GradBgPanel extends JPanel {
        GradBgPanel() { setBackground(BG); }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            // subtle grid
            g2.setColor(new Color(0,200,255,8));
            for (int x = 0; x < getWidth(); x+=60)
                g2.drawLine(x,0,x,getHeight());
            for (int y = 0; y < getHeight(); y+=60)
                g2.drawLine(0,y,getWidth(),y);
        }
    }

    /** Glassmorphic card panel */
    class CyberCard extends JPanel {
        CyberCard() {
            setBackground(SURFACE);
            setBorder(new EmptyBorder(32, 36, 28, 36));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // card fill
            g2.setColor(SURFACE);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),24,24);
            // border
            g2.setColor(BORDER_C);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,24,24);
            // top glow line
            LinearGradientPaint gp = new LinearGradientPaint(0,0,getWidth(),0,
                new float[]{0f,0.5f,1f},
                new Color[]{new Color(0,200,255,0),CYAN,new Color(0,200,255,0)});
            g2.setPaint(gp);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(30,1,getWidth()-30,1);
            // corner accents
            g2.setColor(new Color(0,200,255,80));
            g2.setStroke(new BasicStroke(1.5f));
            int cs = 20;
            g2.drawLine(8,8,8+cs,8); g2.drawLine(8,8,8,8+cs);
            g2.drawLine(getWidth()-8,getHeight()-8,getWidth()-8-cs,getHeight()-8);
            g2.drawLine(getWidth()-8,getHeight()-8,getWidth()-8,getHeight()-8-cs);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Circular countdown timer */
    class TimerRingPanel extends JPanel {
        int current = 20, max = 20;
        TimerRingPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(64,64));
        }
        void setTime(int cur, int m) { current=cur; max=m; repaint(); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int sz = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth()-sz)/2, y = (getHeight()-sz)/2;
            // bg ring
            g2.setColor(new Color(255,255,255,18));
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x+3,y+3,sz-6,sz-6);
            // arc
            Color arcColor = current<=5 ? RED : current<=10 ? GOLD : CYAN;
            g2.setColor(arcColor);
            float ratio = (float)current/max;
            int angle = (int)(360*ratio);
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(x+3,y+3,sz-6,sz-6,90,-angle);
            // text
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.setColor(current<=5 ? RED : TEXT);
            String t = String.valueOf(current);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(t, getWidth()/2-fm.stringWidth(t)/2, getHeight()/2+fm.getAscent()/2-2);
            g2.dispose();
        }
    }

    /** Score ring for results */
    class ScoreRingPanel extends JPanel {
        int pct = 0; Color col = CYAN;
        int animated = 0;
        Timer anim;
        ScoreRingPanel() { setOpaque(false); setPreferredSize(new Dimension(140,140)); }
        void setScore(int p, Color c) {
            pct=p; col=c; animated=0;
            if (anim!=null) anim.stop();
            anim = new Timer(16, e -> {
                animated = Math.min(animated+2, pct);
                repaint();
                if (animated>=pct) ((Timer)e.getSource()).stop();
            });
            anim.start();
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int sz=getWidth()-8, x=4, y=4;
            g2.setColor(new Color(255,255,255,14));
            g2.setStroke(new BasicStroke(9f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2.drawOval(x+5,y+5,sz-10,sz-10);
            g2.setColor(col);
            g2.setStroke(new BasicStroke(9f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            int arc=(int)(360.0*animated/100);
            g2.drawArc(x+5,y+5,sz-10,sz-10,90,-arc);
            // pct text
            g2.setFont(new Font("Monospaced",Font.BOLD,26));
            g2.setColor(col);
            String t=animated+"%";
            FontMetrics fm=g2.getFontMetrics();
            g2.drawString(t,getWidth()/2-fm.stringWidth(t)/2,getHeight()/2+fm.getAscent()/2-4);
            g2.dispose();
        }
    }

    /** Styled option button */
    class OptionButton extends JButton {
        String letter, text;
        boolean isCorrect=false, isWrong=false, hovering=false;

        OptionButton(String letter, String text) {
            this.letter=letter; this.text=text;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(540, 48));
            setMaximumSize(new Dimension(99999, 48));
            setFont(new Font("SansSerif",Font.PLAIN,14));
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){ hovering=true; repaint(); }
                public void mouseExited(MouseEvent e){ hovering=false; repaint(); }
            });
        }

        void reset(String newText) {
            this.text=newText; isCorrect=false; isWrong=false; hovering=false; repaint();
        }
        void markCorrect() { isCorrect=true; isWrong=false; repaint(); }
        void markWrong()   { isWrong=true; isCorrect=false; repaint(); }

        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();

            Color border, bg, letterBg, letterFg;
            if (isCorrect) {
                border=GREEN; bg=new Color(0,255,136,20); letterBg=GREEN; letterFg=Color.BLACK;
            } else if (isWrong) {
                border=RED;   bg=new Color(255,59,92,20);  letterBg=RED;   letterFg=Color.WHITE;
            } else if (hovering) {
                border=new Color(0,200,255,160); bg=new Color(0,200,255,15); letterBg=new Color(0,200,255,30); letterFg=CYAN;
            } else {
                border=new Color(255,255,255,30); bg=new Color(255,255,255,8); letterBg=new Color(0,200,255,20); letterFg=CYAN;
            }

            // bg
            g2.setColor(bg);
            g2.fillRoundRect(0,0,w,h,12,12);
            // left accent bar
            if (isCorrect||isWrong||hovering) {
                g2.setColor(border);
                g2.fillRoundRect(0,4,4,h-8,4,4);
            }
            // border
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0,0,w-1,h-1,12,12);
            // letter box
            int bx=10, by=(h-28)/2, bw=28;
            g2.setColor(letterBg);
            g2.fillRoundRect(bx,by,bw,28,8,8);
            g2.setColor(new Color(0,200,255,60));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(bx,by,bw,28,8,8);
            g2.setFont(new Font("Monospaced",Font.BOLD,12));
            g2.setColor(letterFg);
            FontMetrics fm=g2.getFontMetrics();
            g2.drawString(letter, bx+bw/2-fm.stringWidth(letter)/2, by+20);
            // option text
            g2.setFont(new Font("SansSerif",Font.PLAIN,14));
            g2.setColor(TEXT);
            g2.drawString(text, bx+bw+12, h/2+5);
            g2.dispose();
        }
    }
}
