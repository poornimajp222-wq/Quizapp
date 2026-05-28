const questions = [

{
    question:"Which language is used for Swing?",
    options:["Python","Java","C","PHP"],
    answer:"Java"
},

{
    question:"Which company developed Java?",
    options:["Microsoft","Apple","Sun Microsystems","Google"],
    answer:"Sun Microsystems"
},

{
    question:"HTML stands for?",
    options:[
        "Hyper Text Markup Language",
        "High Transfer Machine Language",
        "Hyperlinks Text Mark Language",
        "None"
    ],
    answer:"Hyper Text Markup Language"
}

];

let current = 0;
let score = 0;

let timeLeft = 10;
let timer;

function loadQuestion(){

    clearInterval(timer);

    timeLeft = 10;

    document.getElementById("timer").innerText =
        "⏰ Time Left: " + timeLeft + "s";

    timer = setInterval(() => {

        timeLeft--;

        document.getElementById("timer").innerText =
            "⏰ Time Left: " + timeLeft + "s";

        if(timeLeft <= 0){

            clearInterval(timer);

            nextQuestion();
        }

    },1000);

    document.getElementById("score").innerText =
        "⭐ Score: " + score;

    document.getElementById("question").innerText =
        questions[current].question;

    let optionsHTML = "";

    questions[current].options.forEach(option => {

        optionsHTML += `
        <div class="option">
            <label>
                <input type="radio"
                       name="option"
                       value="${option}">
                ${option}
            </label>
        </div>
        `;

    });

    document.getElementById("options").innerHTML =
        optionsHTML;
}

function nextQuestion(){

    const selected =
    document.querySelector('input[name="option"]:checked');

    if(selected &&
       selected.value === questions[current].answer){

        score++;
    }

    current++;

    if(current < questions.length){

        loadQuestion();

    }else{

        clearInterval(timer);

        let percentage =
        ((score / questions.length) * 100).toFixed(0);

        document.querySelector(".quiz-box").innerHTML =

        `
        <div class="result">

        <h2>🎉 Quiz Completed 🎉</h2>

        <br>

        <h3>✅ Score: ${score}/${questions.length}</h3>

        <br>

        <h3>📊 Percentage: ${percentage}%</h3>

        </div>
        `;
    }
}

loadQuestion();